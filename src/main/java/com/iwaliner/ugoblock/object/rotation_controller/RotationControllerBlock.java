package com.iwaliner.ugoblock.object.rotation_controller;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RotationControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty MOVING = BooleanProperty.create("moving");
    public static final BooleanProperty COUNTER_CLOCKWISE = BooleanProperty.create("counter_clockwise");
    public RotationControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(MOVING,false).setValue(COUNTER_CLOCKWISE,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED,FACING,MOVING,COUNTER_CLOCKWISE);
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
            if (stack.getItem() == Register.shape_card.get()&&blockEntity.getItem(0).isEmpty()) {
                blockEntity.setItem(0,stack);
                blockEntity.setPositionList(Utils.getPositionList(stack.getTag()));
                player.setItemInHand(hand,ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }else if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            else if(!state.getValue(MOVING)){
                this.openContainer(level, pos, player);
                return InteractionResult.CONSUME;
            }
        }


        return InteractionResult.PASS;
    }
    public MovingBlockEntity.trigonometricFunctionType getTrigonometricFunctionType(BlockState state){
        boolean isCounterClockwise=state.getValue(COUNTER_CLOCKWISE);
        Direction facing=state.getValue(FACING);
            switch (facing.getAxis()){
                case X : return isCounterClockwise? MovingBlockEntity.trigonometricFunctionType.X_COUNTERCLOCKWISE : MovingBlockEntity.trigonometricFunctionType.X_CLOCKWISE;
                case Y : return isCounterClockwise? MovingBlockEntity.trigonometricFunctionType.Y_COUNTERCLOCKWISE : MovingBlockEntity.trigonometricFunctionType.Y_CLOCKWISE;
                case Z : return isCounterClockwise? MovingBlockEntity.trigonometricFunctionType.Z_COUNTERCLOCKWISE : MovingBlockEntity.trigonometricFunctionType.Z_CLOCKWISE;
                default: return MovingBlockEntity.trigonometricFunctionType.NONE;
            }
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.hasCards()) {
            BlockPos startPos = pos.relative(state.getValue(FACING));
            List<BlockPos> posList=state.getValue(POWERED)? blockEntity.getPositionList() :  Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),blockEntity.getDegreeAngle());

            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos); /**neighborChangedでエンティティ化したら、その2tickあとにここでエンティティ化済みのブロックを除去する。時間をあけるのは一瞬何も表示されなくなる(=一瞬消えることでちらつく)のを軽減するため。*/
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
        }
        for (Entity entity : level.getEntities((Entity) null, new AABB(pos.relative(state.getValue(FACING))).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
            return (o instanceof MovingBlockEntity);
        })) {
           // entity.discard();
        }
    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
            if (flag != level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity) {
                   if(!blockEntity.isMoving()&&blockEntity.hasCards()) { /**動いている最中は赤石入力の変化を無視する*/
                      int start=blockEntity.getStartTime();
                      int duration=blockEntity.getDuration();
                       MovingBlockEntity.trigonometricFunctionType type = getTrigonometricFunctionType(state);
                       BlockPos startPos = pos.relative(state.getValue(FACING));
                      if(!flag) {
                          if (duration > 0) {
                              List<BlockPos> posList = blockEntity.getPositionList();
                              if (posList != null) {

                                  blockEntity.setMoving(true);
                                  /**ブロックをエンティティ化*/
                                  makeMoveableBlock(level, pos, startPos, start, duration, type, blockEntity.getDegreeAngle(),blockEntity.getPositionList(),0,false);
                              }
                          }
                          level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                          level.scheduleTick(pos, this, 2);
                      }else{
                          boolean b1=false;
                          for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                              return (o instanceof MovingBlockEntity);
                          })) {
                              b1=true;
                              MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                              CompoundTag tag=movingBlock.getCompoundTag();
                              int angle=movingBlock.getDegreeAngle();
                              makeMoveableBlockReverse(level,pos,startPos,start,duration,type,angle,tag);
                              ((MovingBlockEntity) entity).setDiscardTime(2);
                          }
                          if(!b1){
                          //    List<BlockPos> posList = blockEntity.getPositionList();
                              List<BlockPos> rotatedPosList = Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),blockEntity.getDegreeAngle());
                            //  if ((blockEntity.getDegreeAngle()+blockEntity.getVisualDegree())%90==0) {
                                /*  if (blockEntity.isNotFirstTime()) {
                                      List<BlockPos> posList0 = blockEntity.getPositionList();
                                      Vector3f origin = startPos.getCenter().toVector3f();
                                      for (int i = 0; i < posList0.size(); i++) {
                                          BlockPos eachPos = posList0.get(i).offset(-startPos.getX(),-startPos.getY(),-startPos.getZ());
                                          Vector3f transition = new Vector3f(eachPos.getX(), eachPos.getY(), eachPos.getZ());

                                          Vector3f transitionRotated = transition.rotateY(Mth.PI * (blockEntity.getVisualDegree() != 0 ? blockEntity.getDegreeAngle() : 0) / 180f);
                                          Vector3f positionRotated = origin.add(transitionRotated);
                                          BlockPos rotatedPos = new BlockPos(*//*startPos.getX()+*//*Mth.floor(positionRotated.x), *//*startPos.getY()+*//*Mth.floor(positionRotated.y), *//*startPos.getZ()+*//*Mth.floor(positionRotated.z));
                                          posList.set(i, rotatedPos);
                                      }
                                  }*/
                           //   }

                              /**ブロックをエンティティ化*/
                              makeMoveableBlock(level, pos, startPos, start, duration, type, -blockEntity.getDegreeAngle(),blockEntity.isNotFirstTime()? rotatedPosList: blockEntity.getPositionList(), blockEntity.getVisualDegree(),true);
                          }
                          blockEntity.setMoving(true);
                          level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                          level.scheduleTick(pos, this, 2);
                      }
                   }else if(blockEntity.hasCards()&&blockEntity.isLoop()){
                       if(!flag) {
                           blockEntity.setMoving(false);
                           level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                           level.scheduleTick(pos, this, 2);
                       }else{
                           blockEntity.setMoving(false);
                           level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                           level.scheduleTick(pos, this, 2);
                       }
                   }
            }

    }

    public void makeMoveableBlock(Level level,BlockPos controllerPos,BlockPos startPos,int start,int duration,MovingBlockEntity.trigonometricFunctionType type,int degree,List<BlockPos> positionList,int visualDegree,boolean rotateState){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            CompoundTag entityTag=new CompoundTag();
            CompoundTag posTag=new CompoundTag();
            List<BlockPos> posList=rotationControllerBlockEntity.getPositionList();
            CompoundTag stateTag=new CompoundTag();
            CompoundTag blockEntityTag=new CompoundTag();
            for(int i=0; i<posList.size();i++){
                BlockPos eachPos=positionList.get(i);
                BlockState eachState=level.getBlockState(eachPos);
                BlockEntity eachBlockEntity = level.getBlockEntity(eachPos);
                if (eachBlockEntity != null) {

                    blockEntityTag.put("blockEntity_" + String.valueOf(i),eachBlockEntity.saveWithFullMetadata());
                }else{
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }
                if(eachState.is(Register.TAG_DISABLE_MOVING)){
                    eachState=Blocks.AIR.defaultBlockState();
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
                    Direction oldDirection=eachState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction newDirection=oldDirection;
                    if(degree==90){
                        newDirection=oldDirection.getCounterClockWise();
                    }else if(degree==-90){
                        newDirection=oldDirection.getClockWise();
                    }else if(degree==180||degree==-180){
                        newDirection=oldDirection.getOpposite();
                    }
                    eachState=eachState.setValue(BlockStateProperties.HORIZONTAL_FACING,newDirection);
                }
                eachState= eachState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachPos).isEmpty() ? eachState : Blocks.AIR.defaultBlockState();
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(new BlockPos(posList.get(i).getX() - startPos.getX(), posList.get(i).getY() - startPos.getY(), posList.get(i).getZ() - startPos.getZ())));
                stateTag.put("state_" + String.valueOf(i), NbtUtils.writeBlockState(eachState));

            }
            entityTag.put("positionList",posTag);
            entityTag.put("stateList",stateTag);
            entityTag.put("blockEntityList",blockEntityTag);

            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, type,degree,entityTag,visualDegree,rotationControllerBlockEntity.isLoop(),!rotateState);
            rotationControllerBlockEntity.setVisualDegree(degree);
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }
    private void makeMoveableBlockReverse(Level level,BlockPos controllerPos,BlockPos startPos,int start,int duration,MovingBlockEntity.trigonometricFunctionType type,int degree,CompoundTag tag){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, Utils.getReverseTrigonometricFunctionType(type),-degree,tag,degree,rotationControllerBlockEntity.isLoop(),false);
            rotationControllerBlockEntity.setVisualDegree(degree);
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }
    public static void destroyOldBlock(Level level,BlockPos pos){
        BlockState state=level.getBlockState(pos);
        if(!state.is(Register.TAG_DISABLE_MOVING)) {
            level.removeBlockEntity(pos);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 82);
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
              //  if (movingBlock.tickCount > movingBlock.getStartTick() + movingBlock.getDuration() + 2) {
                    List<BlockState> stateList = movingBlock.getStateList();
                    if(movingBlock.shouldRotate()) {
                        for (BlockState movingState : stateList) {
                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, movingBlock);
                            for (ItemStack itemStack : movingState.getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level.isClientSide) {
                                    level.addFreshEntity(itemEntity);
                                }
                            }

                        }
                        movingBlock.discard();
                        //       }
                    }
            }
            level.removeBlockEntity(pos);
        }

        super.onRemove(state2, level, pos, state2, b);
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.rotation_controller").withStyle(ChatFormatting.GREEN));
    }
}
