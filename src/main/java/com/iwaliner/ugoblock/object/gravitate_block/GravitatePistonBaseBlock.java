package com.iwaliner.ugoblock.object.gravitate_block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class GravitatePistonBaseBlock extends PistonBaseBlock {
    private final boolean isSticky;

    public GravitatePistonBaseBlock(BlockBehaviour.Properties p_60164_) {
        super(false,p_60164_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = false;
    }
    public void setPlacedBy(Level p_60172_, BlockPos p_60173_, BlockState p_60174_, LivingEntity p_60175_, ItemStack p_60176_) {
        if (!p_60172_.isClientSide) {
            this.checkIfExtend(p_60172_, p_60173_, p_60174_);
        }
    }
    public void neighborChanged(BlockState p_60198_, Level p_60199_, BlockPos p_60200_, Block p_60201_, BlockPos p_60202_, boolean p_60203_) {
        if (!p_60199_.isClientSide) {
            this.checkIfExtend(p_60199_, p_60200_, p_60198_);
        }
    }
    public void onPlace(BlockState p_60225_, Level p_60226_, BlockPos p_60227_, BlockState p_60228_, boolean p_60229_) {
        if (!p_60228_.is(p_60225_.getBlock())) {
            if (!p_60226_.isClientSide && p_60226_.getBlockEntity(p_60227_) == null) {
                this.checkIfExtend(p_60226_, p_60227_, p_60225_);
            }
        }
    }
    private void checkIfExtend(Level p_60168_, BlockPos p_60169_, BlockState p_60170_) {
        Direction direction = p_60170_.getValue(FACING);
        boolean flag = this.getNeighborSignal(p_60168_, p_60169_, direction);
        if (flag && !p_60170_.getValue(EXTENDED)) {
            if ((new PistonStructureResolver(p_60168_, p_60169_, direction, true)).resolve()) {
                p_60168_.blockEvent(p_60169_, this, 0, direction.get3DDataValue());
            }
        } else if (!flag && p_60170_.getValue(EXTENDED)) {
            BlockPos blockpos = p_60169_.relative(direction, 2);
            BlockState blockstate = p_60168_.getBlockState(blockpos);
            int i = 1;
            if (blockstate.is(Blocks.MOVING_PISTON) && blockstate.getValue(FACING) == direction) {
                BlockEntity blockentity = p_60168_.getBlockEntity(blockpos);
                if (blockentity instanceof PistonMovingBlockEntity) {
                    PistonMovingBlockEntity pistonmovingblockentity = (PistonMovingBlockEntity)blockentity;
                    if (pistonmovingblockentity.isExtending() && (pistonmovingblockentity.getProgress(0.0F) < 0.5F || p_60168_.getGameTime() == pistonmovingblockentity.getLastTicked() || ((ServerLevel)p_60168_).isHandlingTick())) {
                        i = 2;
                    }
                }
            }
            p_60168_.blockEvent(p_60169_, this, i, direction.get3DDataValue());
        }
    }
    private boolean getNeighborSignal(SignalGetter p_277378_, BlockPos p_60179_, Direction p_60180_) {
        for(Direction direction : Direction.values()) {
            if (direction != p_60180_ && p_277378_.hasSignal(p_60179_.relative(direction), direction)) {
                return true;
            }
        }
        if (p_277378_.hasSignal(p_60179_, Direction.DOWN)) {
            return true;
        } else {
            BlockPos blockpos = p_60179_.above();
            for(Direction direction1 : Direction.values()) {
                if (direction1 != Direction.DOWN && p_277378_.hasSignal(blockpos.relative(direction1), direction1)) {
                    return true;
                }
            }
            return false;
        }
    }
    public boolean triggerEvent(BlockState p_60192_, Level p_60193_, BlockPos p_60194_, int p_60195_, int p_60196_) {
        Direction direction = p_60192_.getValue(FACING);
        BlockState blockstate = p_60192_.setValue(EXTENDED, Boolean.valueOf(true));
        if (!p_60193_.isClientSide) {
            boolean flag = this.getNeighborSignal(p_60193_, p_60194_, direction);
            if (flag && (p_60195_ == 1 || p_60195_ == 2)) {
                p_60193_.setBlock(p_60194_, blockstate, 2);
                return false;
            }
            if (!flag && p_60195_ == 0) {
                return false;
            }
        }
        if (p_60195_ == 0) {
            if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(p_60193_, p_60194_, direction, true)) return false;
            if (!this.moveBlocks(p_60193_, p_60194_, direction, true)) {
                return false;
            }
            p_60193_.setBlock(p_60194_, blockstate, 67);
            p_60193_.playSound((Player)null, p_60194_, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, p_60193_.random.nextFloat() * 0.25F + 0.6F);
            p_60193_.gameEvent(GameEvent.BLOCK_ACTIVATE, p_60194_, GameEvent.Context.of(blockstate));
        } else if (p_60195_ == 1 || p_60195_ == 2) {
            if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(p_60193_, p_60194_, direction, false)) return false;
            BlockEntity blockentity1 = p_60193_.getBlockEntity(p_60194_.relative(direction));
            if (blockentity1 instanceof PistonMovingBlockEntity) {
                ((PistonMovingBlockEntity)blockentity1).finalTick();
            }
            BlockState blockstate1 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            p_60193_.setBlock(p_60194_, blockstate1, 20);
            p_60193_.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(p_60194_, blockstate1, this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(p_60196_ & 7)), direction, false, true));
            p_60193_.blockUpdated(p_60194_, blockstate1.getBlock());
            blockstate1.updateNeighbourShapes(p_60193_, p_60194_, 2);
            if (this.isSticky) {
                BlockPos blockpos = p_60194_.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
                BlockState blockstate2 = p_60193_.getBlockState(blockpos);
                boolean flag1 = false;
                if (blockstate2.is(Blocks.MOVING_PISTON)) {
                    BlockEntity blockentity = p_60193_.getBlockEntity(blockpos);
                    if (blockentity instanceof PistonMovingBlockEntity) {
                        PistonMovingBlockEntity pistonmovingblockentity = (PistonMovingBlockEntity)blockentity;
                        if (pistonmovingblockentity.getDirection() == direction && pistonmovingblockentity.isExtending()) {
                            pistonmovingblockentity.finalTick();
                            flag1 = true;
                        }
                    }
                }
                if (!flag1) {
                    if (p_60195_ != 1 || blockstate2.isAir() || !isPushable(blockstate2, p_60193_, blockpos, direction.getOpposite(), false, direction) || blockstate2.getPistonPushReaction() != PushReaction.NORMAL && !blockstate2.is(Blocks.PISTON) && !blockstate2.is(Blocks.STICKY_PISTON)&& !blockstate2.is(Register.gravitate_piston_base_block.get())) {
                        p_60193_.removeBlock(p_60194_.relative(direction), false);
                    } else {
                        this.moveBlocks(p_60193_, p_60194_, direction, false);
                    }
                }
            } else {
                p_60193_.removeBlock(p_60194_.relative(direction), false);
            }

            p_60193_.playSound((Player)null, p_60194_, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, p_60193_.random.nextFloat() * 0.15F + 0.6F);
            p_60193_.gameEvent(GameEvent.BLOCK_DEACTIVATE, p_60194_, GameEvent.Context.of(blockstate1));
        }
        net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(p_60193_, p_60194_, direction, (p_60195_ == 0));
        return true;
    }
    public static boolean isPushable(BlockState p_60205_, Level p_60206_, BlockPos p_60207_, Direction p_60208_, boolean p_60209_, Direction p_60210_) {
        if (p_60207_.getY() >= p_60206_.getMinBuildHeight() && p_60207_.getY() <= p_60206_.getMaxBuildHeight() - 1 && p_60206_.getWorldBorder().isWithinBounds(p_60207_)) {
            if (p_60205_.isAir()) {
                return true;
            } else if (!p_60205_.is(Blocks.OBSIDIAN) && !p_60205_.is(Blocks.CRYING_OBSIDIAN) && !p_60205_.is(Blocks.RESPAWN_ANCHOR) && !p_60205_.is(Blocks.REINFORCED_DEEPSLATE)) {
                if (p_60208_ == Direction.DOWN && p_60207_.getY() == p_60206_.getMinBuildHeight()) {
                    return false;
                } else if (p_60208_ == Direction.UP && p_60207_.getY() == p_60206_.getMaxBuildHeight() - 1) {
                    return false;
                } else {
                    if (!p_60205_.is(Blocks.PISTON) && !p_60205_.is(Blocks.STICKY_PISTON)&& !p_60205_.is(Register.gravitate_piston_base_block.get())) {
                        if (p_60205_.getDestroySpeed(p_60206_, p_60207_) == -1.0F) {
                            return false;
                        }
                        switch (p_60205_.getPistonPushReaction()) {
                            case BLOCK:
                                return false;
                            case DESTROY:
                                return p_60209_;
                            case PUSH_ONLY:
                                return p_60208_ == p_60210_;
                        }
                    } else if (p_60205_.getValue(EXTENDED)) {
                        return false;
                    }
                    return !p_60205_.hasBlockEntity();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private boolean moveBlocks(Level level, BlockPos pos, Direction direction0, boolean flag) {
        BlockPos blockpos = pos.relative(direction0);
        if (!flag && level.getBlockState(blockpos).is(getHeadBlock())) {
            level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
        }
        PistonStructureResolver pistonstructureresolver = new PistonStructureResolver(level, pos, direction0, flag);
        if (!pistonstructureresolver.resolve()) {
            return false;
        } else {
            double speed=1D;
            double vx=direction0.getStepX()*speed;
            double vy=direction0.getStepY()*speed;
            double vz=direction0.getStepZ()*speed;
            Vec3 vec3=new Vec3(vx,vy,vz);
            /*BlockPos gravitateBlockPos=pos.relative(direction0);
            GravitateBlockEntity gravitateBlockEntity=new GravitateBlockEntity(level,gravitateBlockPos,level.getBlockState(gravitateBlockPos));
            gravitateBlockEntity.setDeltaMovement(vec3);
            level.setBlockAndUpdate(gravitateBlockPos, Blocks.AIR.defaultBlockState());
            level.addFreshEntity(gravitateBlockEntity);*/
            Map<BlockPos, BlockState> map = Maps.newHashMap();
            List<BlockPos> list = pistonstructureresolver.getToPush();
            List<BlockState> list1 = Lists.newArrayList();

            for(int i = 0; i < list.size(); ++i) {
                BlockPos blockpos1 = list.get(i);
                BlockState blockstate = level.getBlockState(blockpos1);
                list1.add(blockstate);
                map.put(blockpos1, blockstate);
            }
            List<BlockPos> list2 = pistonstructureresolver.getToDestroy();
            BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
            Direction direction = flag ? direction0 : direction0.getOpposite();
            int j = 0;
            for(int k = list2.size() - 1; k >= 0; --k) {
                BlockPos blockpos2 = list2.get(k);
                BlockState blockstate1 = level.getBlockState(blockpos2);
                BlockEntity blockentity = blockstate1.hasBlockEntity() ? level.getBlockEntity(blockpos2) : null;
                dropResources(blockstate1, level, blockpos2, blockentity);
                level.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 18);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos2, GameEvent.Context.of(blockstate1));
                if (!blockstate1.is(BlockTags.FIRE)) {
                    level.addDestroyBlockEffect(blockpos2, blockstate1);
                }
                ablockstate[j++] = blockstate1;
            }
            for(int l = list.size() - 1; l >= 0; --l) {
                BlockPos blockpos3 = list.get(l);
                BlockState blockstate5 = level.getBlockState(blockpos3);
                blockpos3 = blockpos3.relative(direction);
                map.remove(blockpos3);
                  GravitateBlockEntity gravitateBlockEntity2=new GravitateBlockEntity(level,blockpos3,level.getBlockState(blockpos3));
                gravitateBlockEntity2.setDeltaMovement(vec3);
                level.addFreshEntity(gravitateBlockEntity2);
                level.setBlockAndUpdate(blockpos3, Blocks.AIR.defaultBlockState());
                ablockstate[j++] = blockstate5;
            }
            BlockPos gravitateBlockPos=pos.relative(direction0);
            GravitateBlockEntity gravitateBlockEntity=new GravitateBlockEntity(level,gravitateBlockPos,level.getBlockState(gravitateBlockPos));
            gravitateBlockEntity.setDeltaMovement(vec3);
            level.setBlockAndUpdate(gravitateBlockPos, Blocks.AIR.defaultBlockState());
            level.addFreshEntity(gravitateBlockEntity);

            if (flag) {
                PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState blockstate4 = getHeadBlock().defaultBlockState().setValue(PistonHeadBlock.FACING, direction0).setValue(PistonHeadBlock.TYPE, pistontype);
                BlockState blockstate6 = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction0).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
                level.setBlock(blockpos, blockstate6, 68);
                level.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(blockpos, blockstate6, blockstate4, direction0, true, true));
            }
            if (flag) {
                level.updateNeighborsAt(blockpos, getHeadBlock());
            }
            return true;
        }
    }
    private Block getHeadBlock(){
        return Register.gravitate_piston_head_block.get();
    }

}
