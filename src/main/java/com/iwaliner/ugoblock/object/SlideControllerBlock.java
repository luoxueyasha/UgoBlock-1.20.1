package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.register.BlockEntityRegister;
import com.iwaliner.ugoblock.register.ItemAndBlockRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof SlideControllerBlockEntity blockEntity) {
            if (stack.getItem() == ItemAndBlockRegister.shape_card.get()) {
                blockEntity.setPositionList(ShapeCardItem.getPositionList(stack.getTag()));
                player.setItemInHand(hand,ItemStack.EMPTY);
            }else if (stack.getItem() == ItemAndBlockRegister.end_location_card.get()&&stack.getTag()!=null) {
                blockEntity.setEndPos(EndLocationCardItem.getEndPos(stack.getTag()));
                player.setItemInHand(hand,ItemStack.EMPTY);
            }
        }


        return InteractionResult.SUCCESS;
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {

        if(level.getBlockEntity(pos) instanceof SlideControllerBlockEntity blockEntity) {
            int startSecond;
            int durationSecond;
            BlockPos startPos;
            BlockPos endPos;
            List<BlockPos> posList=blockEntity.getPositionList();
            if(state.getValue(POWERED)){ /**赤石信号がONになったとき*/
            startSecond = 0;
            durationSecond = 4;
            startPos=pos.above();
            endPos=blockEntity.getEndPos();
            if(!blockEntity.isFirstTime()) {
                List<BlockPos> posList0 = blockEntity.getPositionList();
                for (int i = 0; i < posList0.size(); i++) {
                    posList.set(i, new BlockPos(posList0.get(i).getX() + (blockEntity.getEndPos().getX() - startPos.getX()), posList0.get(i).getY() + (blockEntity.getEndPos().getY() - startPos.getY()), posList0.get(i).getZ() + (blockEntity.getEndPos().getZ() - startPos.getZ())));
                }
            }
            }else{ /**赤石信号がOFFになったとき*/
               startSecond = 0;
                durationSecond = 4;
                startPos=pos.above();
                endPos=new BlockPos(startPos.getX()+(startPos.getX()-blockEntity.getEndPos().getX()),startPos.getY()+(startPos.getY()-blockEntity.getEndPos().getY()),startPos.getZ()+(startPos.getZ()-blockEntity.getEndPos().getZ()));
                if(!blockEntity.isFirstTime()) {
                    List<BlockPos> posList0 = blockEntity.getPositionList();
                    for (int i = 0; i < posList0.size(); i++) {
                        posList.set(i, new BlockPos(posList0.get(i).getX() + (startPos.getX() - blockEntity.getEndPos().getX()), posList0.get(i).getY() + (startPos.getY() - blockEntity.getEndPos().getY()), posList0.get(i).getZ() + (startPos.getZ() - blockEntity.getEndPos().getZ())));
                    }
                }
            }
            if(endPos==null){
                endPos=startPos;
            }

            if(posList!=null) {
                BlockPos transitionPos = new BlockPos(startPos.getX() - endPos.getX(), startPos.getY() - endPos.getY(), startPos.getZ() - endPos.getZ());
                for (BlockPos eachPos : posList) {
                    makeMoveableBlock(level, eachPos, startSecond, durationSecond, transitionPos);
                }
                for (BlockPos eachPos : posList) {
                    destroyOldBlock(level, eachPos);
                }
                blockEntity.setFirstTime(false);
            }
        }
    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
        if (!level.isClientSide) {
            if (flag != level.hasNeighborSignal(pos)) {
                    level.scheduleTick(pos, this, 1);
                    level.setBlock(pos, state.cycle(POWERED), 2);

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
            } else {
                moveableBlock = new MoveableBlockEntity(level, startPos, state.hasProperty(BlockStateProperties.WATERLOGGED)? state.setValue(BlockStateProperties.WATERLOGGED,false) : level.getFluidState(startPos).isEmpty()? state : Blocks.AIR.defaultBlockState(), start * 20, duration * 20, traisitionPos, null);

            }
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }
        }

    }
    public static void destroyOldBlock(Level level,BlockPos startPos){
        level.removeBlockEntity(startPos);
        level.removeBlock(startPos,false);
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
        return createTickerHelper(p_152162_, BlockEntityRegister.SlideController.get(), SlideControllerBlockEntity::tick);
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
        if(livingEntity instanceof Player){
            ItemEntity itemEntity1=new ItemEntity(level,(double)pos.getX()+0.5D,(double)pos.getY()+2.5D,(double)pos.getZ()+0.5D,new ItemStack(ItemAndBlockRegister.shape_card.get()));
            ItemEntity itemEntity2=new ItemEntity(level,(double)pos.getX()+0.5D,(double)pos.getY()+2.5D,(double)pos.getZ()+0.5D,new ItemStack(ItemAndBlockRegister.end_location_card.get()));
            if(!level.isClientSide){
                level.addFreshEntity(itemEntity1);
                level.addFreshEntity(itemEntity2);
            }
        }
    }
}
