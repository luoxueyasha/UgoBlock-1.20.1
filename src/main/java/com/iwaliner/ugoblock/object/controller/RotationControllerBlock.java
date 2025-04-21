package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerBlock;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RotationControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty MOVING = BooleanProperty.create("moving");
    public static final BooleanProperty COUNTER_CLOCKWISE = BooleanProperty.create("counter_clockwise");
    public static final BooleanProperty IGNORE = BooleanProperty.create("ignore");
    public RotationControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(MOVING,false).setValue(COUNTER_CLOCKWISE,false).setValue(IGNORE,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED,FACING,MOVING,COUNTER_CLOCKWISE,IGNORE);
    }
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RotationControllerBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity) {
            if (stack.getItem() == Register.shape_card.get()&&blockEntity.getItem(0).isEmpty()&&!player.isSuppressingBounce()) {
                blockEntity.setItem(0,stack);
                player.setItemInHand(hand,ItemStack.EMPTY);
                player.level().playSound(player,pos, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS,1F,1F);
                return InteractionResult.SUCCESS;
            }else if(stack.is(Register.block_imitation_wand.get())){
                return InteractionResult.PASS;
            }else if(!state.getValue(MOVING)&&!state.getValue(POWERED)) {
                if (!player.isSuppressingBounce()) {
                    if (level.isClientSide) {
                        return InteractionResult.SUCCESS;
                    }
                    this.openContainer(level, pos, player);
                    return InteractionResult.CONSUME;
                }else if(!blockEntity.getImitatingState().isAir()){
                    blockEntity.setImitatingState(Blocks.AIR.defaultBlockState());
                    level.playSound(player,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if(state.getValue(MOVING)){
            player.displayClientMessage(Component.translatable("info.ugoblock.controller_denyed_opening_gui").withStyle(ChatFormatting.YELLOW), true);
        }else{
            player.displayClientMessage(Component.translatable("info.ugoblock.idle_rotation_controller_denyed_opening_gui").withStyle(ChatFormatting.YELLOW), true);
        }
         return InteractionResult.PASS;
    }
    public static Direction.Axis getAxis(BlockState state){
        Direction facing=state.getValue(FACING);
        return facing.getAxis();
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&state.getBlock() instanceof RotationControllerBlock) {
            removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);
        }
    }
    public static void removeBlocks(Level level, BlockPos startPos, RotationControllerBlockEntity blockEntity, BlockState state){
        if(blockEntity.hasCards()) {
            List<BlockPos> posList=state.getValue(POWERED)? blockEntity.getPositionList() :  Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),getDegreeAngle(state,blockEntity));
            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos);
                    }
                }
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        updateDestroyedPos(level, eachPos);
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
            List<BlockPos> basketPosList=blockEntity.getBasketPosList();
             if(basketPosList!=null){
            for (int i = 0; i < basketPosList.size(); i++) {
                   BlockPos eachBasketPos=basketPosList.get(i).offset(startPos.getX(),startPos.getY(),startPos.getZ());
                    destroyOldBlock(level,eachBasketPos);
            }
                 for (int i = 0; i < basketPosList.size(); i++) {
                     BlockPos eachBasketPos=basketPosList.get(i).offset(startPos.getX(),startPos.getY(),startPos.getZ());
                     updateDestroyedPos(level,eachBasketPos);
                 }
            }
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
        boolean signal=level.hasNeighborSignal(pos);
        int signalVolume=level.getBestNeighborSignal(pos);
        if (level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.isPreRedstoneSignalON()!=signal&&!level.isClientSide()) {
            blockEntity.setPreRedstoneSignal(signal);
            BlockPos startPos = pos.relative(state.getValue(FACING));
            Direction controllerDirection=state.getValue(BlockStateProperties.FACING);
            if(state.getValue(IGNORE)){
                level.setBlock(pos, state.setValue(IGNORE,false), 2);
                return;
            }
            int start=blockEntity.getStartTime();
            int duration=blockEntity.getDuration();
            boolean makingEntitySuccess=false;
            if(blockEntity.hasCards()&&blockEntity.isLoop()){
                if(!flag){
                    if(blockEntity.isIgnore()){
                        blockEntity.setIgnore(false);
                        return;
                    }
                    if (duration > 0) {
                        List<BlockPos> posList = blockEntity.getPositionList();

                        if (posList != null) {

                            blockEntity.setMoving(true);
                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), getDegreeAngle(state,blockEntity),blockEntity.getPositionList(),0,false,BlockPos.ZERO,0);
                            if(makingEntitySuccess) {
                                //removeBlocks(level,startPos,blockEntity,state);
                                blockEntity.setMoving(true);
                                level.setBlock(pos, state.setValue(POWERED,true).setValue(MOVING, true), 2);
                                level.scheduleTick(pos, this, 0);
                            }
                        }
                    }
                }else{
                    for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                        return (o instanceof MovingBlockEntity);
                    })) {
                        MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                        movingBlock.setDiscardTime(-1);
                        blockEntity.setMoving(false);
                        blockEntity.setIgnore(true);
                        int angle=blockEntity.getDegreeAngleAndLoop();
                        ItemStack card=blockEntity.getItem(0);

                        RotationControllerBlockEntity newBlockEntity=new RotationControllerBlockEntity(pos,state);
                        newBlockEntity.setDegreeAngle(angle);
                        newBlockEntity.setDuration(duration);
                        newBlockEntity.setItem(0,card);
                        newBlockEntity.setMoving(false);
                        newBlockEntity.setIgnore(true);
                        level.removeBlockEntity(pos);
                        level.setBlockEntity(newBlockEntity);
                        level.setBlock(pos,state.setValue(RotationControllerBlock.POWERED,false).setValue(RotationControllerBlock.MOVING,false),2);
                    }
                }

            } else if(!blockEntity.isMoving()&&blockEntity.hasCards()&&!blockEntity.isLoop()) { /**動いている最中は赤石入力の変化を無視する*/

                if(!flag) {
                    if (duration > 0) {
                        List<BlockPos> posList = blockEntity.getPositionList();

                        if (posList != null) {

                            blockEntity.setMoving(true);
                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), getDegreeAngle(state,blockEntity),blockEntity.getPositionList(),0,false,BlockPos.ZERO,0);

                        }
                    }
                    if(makingEntitySuccess) {
                       level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                        level.scheduleTick(pos, this, 0);
                    }
                }else{

                    boolean b1=false;
                    for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                        return (o instanceof MovingBlockEntity);
                    })) {
                        b1=true;
                        MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                         makeMoveableBlockReverse(level,pos,movingBlock);
                    }
                    if(!b1){

                        List<BlockPos> rotatedPosList = Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),getDegreeAngle(state,blockEntity));
                        List<BlockPos> rotatedBasketPosList = Utils.rotateBasketPosList(blockEntity.getBasketPosList(),BlockPos.ZERO,startPos,state.getValue(FACING).getAxis(),-getDegreeAngle(state,blockEntity),blockEntity.getBasketOriginPosList());
                        rotatedPosList.addAll(rotatedBasketPosList);
                        if(!blockEntity.isLoop()){
                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),rotatedPosList, blockEntity.getVisualDegree(),true,BlockPos.ZERO,0);
                            if(makingEntitySuccess) {
                                blockEntity.setMoving(true);
                                level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                                level.scheduleTick(pos, this, 0);
                            }
                        }else{
                            removeBlocks(level,startPos,blockEntity,state);
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),blockEntity.isNotFirstTime()? rotatedPosList: blockEntity.getPositionList(), 0,true,BlockPos.ZERO,0);
                        }
                    }else{
                        blockEntity.setMoving(true);
                        level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                    }
                }
            }
            blockEntity.setPreRedstoneSignal(signal);
        }

    }
    public static int getDegreeAngle(BlockState state, RotationControllerBlockEntity blockEntity){
        Direction controllerDirection=state.getValue(FACING);
        boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
        if(invertRotation){
          return   -blockEntity.getDegreeAngle();
        }
        return blockEntity.getDegreeAngle();
    }
    public static void makeMoveableBlockReverse(Level level, BlockPos controllerPos, MovingBlockEntity movingBlock){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            movingBlock.setTimeCount(0);
            movingBlock.setDuration(movingBlock.getDuration());
            movingBlock.setStartRotation(movingBlock.getDegreeAngle());
            rotationControllerBlockEntity.setVisualDegree(movingBlock.getDegreeAngle());
            movingBlock.setDegreeAngle(-movingBlock.getDegreeAngle());
            movingBlock.setShouldRotateState(false);
        }
    }
    public static void destroyOldBlock(Level level,BlockPos pos){
        BlockState state=level.getBlockState(pos);
        if(!state.is(Register.TAG_DISABLE_MOVING)) {
            level.removeBlockEntity(pos);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 82);
        }
    }
    public static void updateDestroyedPos(Level level,BlockPos pos){
        BlockState state=level.getBlockState(pos);
        if(!state.is(Register.TAG_DISABLE_MOVING)) {
            level.removeBlockEntity(pos);
            level.updateNeighborsAt(pos,state.getBlock());
        }
    }
    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotationControllerBlockEntity(pos,state);
    }
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, Register.RotationController.get(), RotationControllerBlockEntity::tick);
    }
    public BlockState getStateForPlacement(BlockPlaceContext p_55087_) {
        return this.defaultBlockState().setValue(FACING, p_55087_.getNearestLookingDirection().getOpposite());
    }
    public BlockState rotate(BlockState p_55115_, Rotation p_55116_) {
        return p_55115_.setValue(FACING, p_55116_.rotate(p_55115_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_55112_, Mirror p_55113_) {
        return p_55112_.rotate(p_55113_.getRotation(p_55112_.getValue(FACING)));
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, livingEntity, stack);
        if(livingEntity instanceof Player&&state.getBlock() instanceof RotationControllerBlock){
            ItemEntity itemEntity1=new ItemEntity(level,livingEntity.getX(),livingEntity.getY()+1D,livingEntity.getZ(),new ItemStack(Register.shape_card.get()));
            if(!level.isClientSide){
                level.addFreshEntity(itemEntity1);
            }
        }
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean b) {
        if(state2.isAir()) {

            for (Entity entity : level.getEntities((Entity) null, new AABB(pos.relative(state.getValue(FACING))).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                return (o instanceof MovingBlockEntity);
            })) {
                MovingBlockEntity movingBlock = (MovingBlockEntity) entity;
                List<BlockPos> posList=movingBlock.getPosList();
                posList.addAll(movingBlock.getBasketPosList());
                    if(movingBlock.shouldRotate()) {
                        for (int i=0;i<posList.size();i++) {
                            BlockState movingState=movingBlock.getStateList().get(i);
                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, movingBlock);
                            for (ItemStack itemStack : movingState.getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level.isClientSide) {
                                    level.addFreshEntity(itemEntity);
                                }
                            }
                        }
                        movingBlock.discardWithPassenger();
                    }
                    break;
            }
            level.removeBlockEntity(pos);
        }
        super.onRemove(state2, level, pos, state2, b);
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.rotation_controller").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.rotation_controller_scroll").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.block_imitatable").withStyle(ChatFormatting.GREEN));
    }
}
