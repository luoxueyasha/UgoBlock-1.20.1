package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.register.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SlideControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SlideControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {



        return InteractionResult.SUCCESS;
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
       BlockPos transitionPos=new BlockPos(0,0,5);
        int startSecond=3;
        int durationSecond=4;

        makeMoveableBlock(level,pos.above(),startSecond,durationSecond,transitionPos);
        makeMoveableBlock(level,pos.above().offset(0,0,1),startSecond,durationSecond,transitionPos);
        destroyOldBlock(level,pos.above());
        destroyOldBlock(level,pos.above().offset(0,0,1));

    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
        if (!level.isClientSide) {
            if (flag != level.hasNeighborSignal(pos)) {
                if(!flag) {
                    level.scheduleTick(pos, this, 1);
                }
                    level.setBlock(pos, state.cycle(POWERED), 2);

            }


        }

    }
    public static void makeMoveableBlock(Level level,BlockPos startPos,int start,int duration,BlockPos traisitionPos){
        BlockEntity blockEntity=level.getBlockEntity(startPos);
        BlockState state=level.getBlockState(startPos);
        MoveableBlockEntity moveableBlock;
        if(blockEntity !=null) {
            moveableBlock = new MoveableBlockEntity(level, startPos, state, start * 20, duration * 20, traisitionPos, blockEntity);
        }else{
           moveableBlock = new MoveableBlockEntity(level, startPos, state, start * 20, duration * 20, traisitionPos, null);

        }
        if(!level.isClientSide) {
            level.addFreshEntity(moveableBlock);
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
}
