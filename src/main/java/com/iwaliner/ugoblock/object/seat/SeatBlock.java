package com.iwaliner.ugoblock.object.seat;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeatBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0D, 0D, 0D, 16D, 2D, 16D);

    public SeatBlock(Properties p_49795_) {
        super(p_49795_);
    }

    public RenderShape getRenderShape(BlockState p_49098_) {
        return RenderShape.INVISIBLE;
    }
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        AABB aabb=new AABB(pos);
        for (Entity entity : level.getEntities((Entity) null, aabb.move(0D,0D,0D).inflate(0d, 0d, 0d), (o) -> {
            return (o instanceof SeatEntity);
        })) {
           entity.discard();
            break;
        }
        super.destroy(level, pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state2, boolean flag) {
        super.onPlace(state, level, pos, state2, flag);
        SeatEntity seatEntity=new SeatEntity(level,pos,false);
        if(!level.isClientSide()){
            level.addFreshEntity(seatEntity);
        }
    }
    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack) {
            SeatEntity seatEntity=new SeatEntity(level,pos,false);
            if(!level.isClientSide()){
                level.addFreshEntity(seatEntity);
            }
    }
    public Entity getSeatEntity(Level level,BlockPos pos){
        AABB aabb = new AABB(pos);
        for (Entity entity : level.getEntities((Entity) null, aabb.move(0D, 0D, 0D).inflate(0d, 0d, 0d), (o) -> {
            return (o instanceof SeatEntity);
        })) {
            return entity;
        }
        return null;
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
            AABB aabb = new AABB(pos);
            boolean flag = false;
            Entity seatEntity=getSeatEntity(level,pos);
            if(seatEntity!=null){
                flag = true;
                if(!player.isPassenger()&&seatEntity.getPassengers().isEmpty()){
                    player.startRiding(seatEntity);
                    level.playSound(player, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
           /* for (Entity entity : level.getEntities((Entity) null, aabb.move(0D, 0D, 0D).inflate(0d, 0d, 0d), (o) -> {
                return (o instanceof SeatEntity);
            })) {
                flag = true;
                if(!player.isPassenger()&&entity.getPassengers().isEmpty()){
                    player.startRiding(entity);
                    level.playSound(player, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
                break;
            }*/
            if (!flag) {
                SeatEntity seatEntity2 = new SeatEntity(level, pos,false);
                if(!level.isClientSide()){
                level.addFreshEntity(seatEntity2);
                }
                return InteractionResult.SUCCESS;
            }
        return InteractionResult.CONSUME;
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.seat1").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.seat2").withStyle(ChatFormatting.GREEN));
    }
}
