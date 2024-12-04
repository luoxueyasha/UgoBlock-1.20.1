package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlideControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public SlideControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED,FACING);
    }
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof SlideControllerBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof SlideControllerBlockEntity blockEntity) {
            if (stack.getItem() == Register.shape_card.get()&&blockEntity.getItem(0).isEmpty()) {
                blockEntity.setItem(0,stack);
                blockEntity.setPositionList(ShapeCardItem.getPositionList(stack.getTag()));
                player.setItemInHand(hand,ItemStack.EMPTY);
            }else if (stack.getItem() == Register.end_location_card.get()&&stack.getTag()!=null&&blockEntity.getItem(1).isEmpty()) {
               blockEntity.setItem(1,stack);
                blockEntity.setEndPos(EndLocationCardItem.getEndPos(stack.getTag()));
                player.setItemInHand(hand,ItemStack.EMPTY);
            }else if(!level.isClientSide){
                this.openContainer(level, pos, player);
                return InteractionResult.CONSUME;
            }
        }


        return InteractionResult.SUCCESS;
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {

        if(level.getBlockEntity(pos) instanceof SlideControllerBlockEntity blockEntity/*&&blockEntity.getCoolTime()==0*/) {
            List<BlockPos> posList=blockEntity.getPositionList();
            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos);
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
        }
    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);

            if (flag != level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof SlideControllerBlockEntity blockEntity) {
                   if(!blockEntity.isMoving()&&blockEntity.getEndPos()!=null) { /**動いている最中は赤石入力の変化を無視する*/
                       int startSecond= Mth.floor((double) blockEntity.getStartTime()/20D);
                       int durationSecond = Mth.floor((double) blockEntity.getDuration()/20D);
                       if(blockEntity.getEndPos()!=null&&durationSecond>0) {
                           ModCoreUgoBlock.logger.info("AA");
                           BlockPos startPos;
                           BlockPos endPos;
                           List<BlockPos> posList=blockEntity.getPositionList();
                           if(!state.getValue(POWERED)){
                               ModCoreUgoBlock.logger.info("BB");
                              startPos=pos.relative(state.getValue(FACING));
                               endPos=blockEntity.getEndPos();
                               if(blockEntity.isNotFirstTime()) {
                                   List<BlockPos> posList0 = blockEntity.getPositionList();
                                   for (int i = 0; i < posList0.size(); i++) {
                                       posList.set(i, new BlockPos(posList0.get(i).getX() + (blockEntity.getEndPos().getX() - startPos.getX()), posList0.get(i).getY() + (blockEntity.getEndPos().getY() - startPos.getY()), posList0.get(i).getZ() + (blockEntity.getEndPos().getZ() - startPos.getZ())));
                                   }
                               }
                           }else{
                               ModCoreUgoBlock.logger.info("CC");
                               startPos=pos.relative(state.getValue(FACING));
                               endPos=new BlockPos(startPos.getX()+(startPos.getX()-blockEntity.getEndPos().getX()),startPos.getY()+(startPos.getY()-blockEntity.getEndPos().getY()),startPos.getZ()+(startPos.getZ()-blockEntity.getEndPos().getZ()));
                               if(blockEntity.isNotFirstTime()) {
                                   List<BlockPos> posList0 = blockEntity.getPositionList();
                                   for (int i = 0; i < posList0.size(); i++) {
                                       posList.set(i, new BlockPos(posList0.get(i).getX() + (startPos.getX() - blockEntity.getEndPos().getX()), posList0.get(i).getY() + (startPos.getY() - blockEntity.getEndPos().getY()), posList0.get(i).getZ() + (startPos.getZ() - blockEntity.getEndPos().getZ())));
                                   }
                               }
                           }
                           if(endPos==null){
                               ModCoreUgoBlock.logger.info("DD");
                               endPos=startPos;
                           }

                           if(posList!=null) {
                               ModCoreUgoBlock.logger.info("EE");
                               blockEntity.setPositionList(posList);
                              // blockEntity.setMoveTick(startSecond*20+durationSecond*20);
                               blockEntity.setMoving(true);
                               BlockPos transitionPos = new BlockPos(startPos.getX() - endPos.getX(), startPos.getY() - endPos.getY(), startPos.getZ() - endPos.getZ());
                               for (BlockPos eachPos : posList) {
                                   if(!blockEntity.getBlockPos().equals(eachPos)) {
                                       makeMoveableBlock(level, eachPos, startSecond, durationSecond, transitionPos);
                                       ModCoreUgoBlock.logger.info("transition:["+transitionPos.getX()+","+transitionPos.getY()+","+transitionPos.getZ()+"]");
                                       ModCoreUgoBlock.logger.info("start:["+startPos.getX()+","+startPos.getY()+","+startPos.getZ()+"]");
                                       ModCoreUgoBlock.logger.info("each:["+eachPos.getX()+","+eachPos.getY()+","+eachPos.getZ()+"]");
                                       ModCoreUgoBlock.logger.info("end:["+endPos.getX()+","+eachPos.getY()+","+eachPos.getZ()+"]");
                                   }
                               }

                           }
                       }

                           level.setBlock(pos, state.cycle(POWERED), 2);
                           level.scheduleTick(pos, this, 2);

                   }
            }



    }
    public static void makeMoveableBlock(Level level,BlockPos startPos,int start,int duration,BlockPos traisitionPos){
        BlockEntity blockEntity=level.getBlockEntity(startPos);
        BlockState state=level.getBlockState(startPos);
        MoveableBlockEntity moveableBlock;
        if(!state.isAir()) {
            if (blockEntity != null) {
                moveableBlock = new MoveableBlockEntity(level, startPos, state.hasProperty(BlockStateProperties.WATERLOGGED)? state.setValue(BlockStateProperties.WATERLOGGED,false) : level.getFluidState(startPos).isEmpty()? state : Blocks.AIR.defaultBlockState(), start * 20, duration * 20, traisitionPos, blockEntity);
                if(blockEntity instanceof SlideControllerBlockEntity slideControllerBlockEntity&&!slideControllerBlockEntity.getPositionList().isEmpty()&&!slideControllerBlockEntity.getEndPos().equals(ShapeCardItem.errorPos())){

                    List<BlockPos> newPos=new ArrayList<>();
                    for(int i=0;i< ((SlideControllerBlockEntity) blockEntity).getPositionList().size();i++){
                        newPos.add(slideControllerBlockEntity.getPositionList().get(i).offset(traisitionPos.getX(),traisitionPos.getY(),traisitionPos.getZ()));
                    //    newPos.add(new BlockPos(slideControllerBlockEntity.getPositionList().get(i).getX()+traisitionPos.getX(),slideControllerBlockEntity.getPositionList().get(i).getY()+traisitionPos.getY(),slideControllerBlockEntity.getPositionList().get(i).getZ()+traisitionPos.getZ()));
                    }
                    //slideControllerBlockEntity.clearPositionList();
                    slideControllerBlockEntity.setPositionList(newPos);
                    BlockPos newEndPos=slideControllerBlockEntity.getEndPos().offset(traisitionPos.getX(),traisitionPos.getY(),traisitionPos.getZ());
                    //BlockPos newEndPos=new BlockPos(slideControllerBlockEntity.getEndPos().getX()+traisitionPos.getX(),slideControllerBlockEntity.getEndPos().getY()+traisitionPos.getY(),slideControllerBlockEntity.getEndPos().getZ()+traisitionPos.getZ());
                    slideControllerBlockEntity.setEndPos(newEndPos);

                    moveableBlock = new MoveableBlockEntity(level, startPos, state.hasProperty(BlockStateProperties.WATERLOGGED)? state.setValue(BlockStateProperties.WATERLOGGED,false) : level.getFluidState(startPos).isEmpty()? state : Blocks.AIR.defaultBlockState(), start * 20+1, duration * 20, traisitionPos, slideControllerBlockEntity);

                }
            } else {
                moveableBlock = new MoveableBlockEntity(level, startPos, state.hasProperty(BlockStateProperties.WATERLOGGED)? state.setValue(BlockStateProperties.WATERLOGGED,false) : level.getFluidState(startPos).isEmpty()? state : Blocks.AIR.defaultBlockState(), start * 20+1, duration * 20, traisitionPos, null);
            }
            //moveableBlock.setPos(moveableBlock.position().add(0.1F,0.1F,0.1F));
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }
        }

    }
    public static void destroyOldBlock(Level level,BlockPos pos){
        level.removeBlockEntity(pos);
        level.setBlock(pos,Blocks.AIR.defaultBlockState(),82);
    }

    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SlideControllerBlockEntity(pos,state);
    }
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, Register.SlideController.get(), SlideControllerBlockEntity::tick);
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
        if(livingEntity instanceof Player&&state.getBlock() instanceof SlideControllerBlock){
            ItemStack endLocationCard=new ItemStack(Register.end_location_card.get());
            CompoundTag tag=new CompoundTag();
            tag.put("end_location", NbtUtils.writeBlockPos(ShapeCardItem.errorPos()));
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
