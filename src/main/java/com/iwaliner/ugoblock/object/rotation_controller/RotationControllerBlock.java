package com.iwaliner.ugoblock.object.rotation_controller;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

import java.util.List;

public class RotationControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty MOVING = BooleanProperty.create("moving");
    public static final BooleanProperty COUNTER_CLOCKWISE = BooleanProperty.create("counter_clockwise");
    public RotationControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(MOVING,false).setValue(COUNTER_CLOCKWISE,true));
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
            }else if(stack.isEmpty()){
                player.turn(0D,90D);
            }
            else if(!level.isClientSide){
                this.openContainer(level, pos, player);
                return InteractionResult.CONSUME;
            }
        }


        return InteractionResult.SUCCESS;
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {

        if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.hasCards()) {
            List<BlockPos> posList=blockEntity.getPositionList();
            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos); /**neighborChangedでエンティティ化したら、その2tickあとにここでエンティティ化済みのブロックを除去する。時間をあけるのは一瞬何も表示されなくなる(=一瞬消えることでちらつく)のを軽減するため。*/
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
        }
    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);

            if (flag != level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity) {
                   if(!blockEntity.isMoving()&&blockEntity.hasCards()) { /**動いている最中は赤石入力の変化を無視する*/
                      int start=blockEntity.getStartTime();
                      int duration=blockEntity.getDuration();
                       MovingBlockEntity.trigonometricFunctionType type = MovingBlockEntity.trigonometricFunctionType.Y_COUNTERCLOCKWISE;
                       BlockPos startPos = pos.relative(state.getValue(FACING));
                      if(!flag) {
                          if (duration > 0) {
                              List<BlockPos> posList = blockEntity.getPositionList();
                              if (posList != null) {

                                  blockEntity.setMoving(true);
                                  /**ブロックをエンティティ化*/
                                  makeMoveableBlock(level, pos, startPos, start, duration, type, blockEntity.getDegreeAngle());
                              }
                          }
                          level.setBlock(pos, state.cycle(POWERED), 2);
                          level.scheduleTick(pos, this, 2);
                      }else{
                          ModCoreUgoBlock.logger.info("reverse??");
                          for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                              return (o instanceof MovingBlockEntity);
                          })) {
                              MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                              CompoundTag tag=movingBlock.getCompoundTag();
                              int angle=movingBlock.getDegreeAngle();
                              makeMoveableBlockReverse(level,pos,startPos,start,duration,type,angle,tag);
                              movingBlock.discard();
                              //reverseRotation((MovingBlockEntity) entity,duration,Utils.getReverseTrigonometricFunctionType(type),blockEntity.getDegreeAngle());
                          }
                          level.setBlock(pos, state.cycle(POWERED), 2);
                      }
                   }
            }



    }

    private void makeMoveableBlock(Level level,BlockPos controllerPos,BlockPos startPos,int start,int duration,MovingBlockEntity.trigonometricFunctionType type,int degree){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            CompoundTag entityTag=new CompoundTag();
            List<BlockPos> posList=rotationControllerBlockEntity.getPositionList();
            CompoundTag posTag=new CompoundTag();
            CompoundTag stateTag=new CompoundTag();
            CompoundTag blockEntityTag=new CompoundTag();
            float radian= Mth.PI*((float) degree/180f);
            for(int i=0; i<posList.size();i++){
                BlockPos eachPos=posList.get(i);
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
                eachState= eachState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachPos).isEmpty() ? eachState : Blocks.AIR.defaultBlockState();
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(new BlockPos(posList.get(i).getX() - startPos.getX(), posList.get(i).getY() - startPos.getY(), posList.get(i).getZ() - startPos.getZ())));
                stateTag.put("state_" + String.valueOf(i), NbtUtils.writeBlockState(eachState));

            }
            entityTag.put("positionList",posTag);
            entityTag.put("stateList",stateTag);
            entityTag.put("blockEntityList",blockEntityTag);

            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, type,degree,entityTag);

            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }
    private void makeMoveableBlockReverse(Level level,BlockPos controllerPos,BlockPos startPos,int start,int duration,MovingBlockEntity.trigonometricFunctionType type,int degree,CompoundTag tag){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {

            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, Utils.getReverseTrigonometricFunctionType(type),-degree,tag);
            moveableBlock.setYRot(-degree);

            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }
    private void reverseRotation(MovingBlockEntity entity,int duration,MovingBlockEntity.trigonometricFunctionType type,int degree){
        CompoundTag tag=entity.getCompoundTag();
        int angle=entity.getDegreeAngle();


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
            ItemStack endLocationCard=new ItemStack(Register.end_location_card.get());
            CompoundTag tag=new CompoundTag();
            tag.put("end_location", NbtUtils.writeBlockPos(Utils.errorPos()));
            tag.put("start_location", NbtUtils.writeBlockPos(pos.relative(state.getValue(FACING))));
            endLocationCard.setTag(tag);
            ItemEntity itemEntity1=new ItemEntity(level,livingEntity.getX(),livingEntity.getY()+1D,livingEntity.getZ(),new ItemStack(Register.shape_card.get()));
            ItemEntity itemEntity2=new ItemEntity(level,livingEntity.getX(),livingEntity.getY()+1D,livingEntity.getZ(),endLocationCard);
            if(!level.isClientSide){
                level.addFreshEntity(itemEntity1);
                level.addFreshEntity(itemEntity2);
            }
        }
    }


}
