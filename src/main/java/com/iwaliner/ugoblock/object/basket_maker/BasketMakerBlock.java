package com.iwaliner.ugoblock.object.basket_maker;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.controller.RotationControllerBlockEntity;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasketMakerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SOUTH_BOX = Block.box(0D, 0D, 8D, 16D, 16D, 16D);
    private static final VoxelShape NORTH_BOX = Block.box(0D, 0.0D, 0D, 16D, 16D, 8D);
    private static final VoxelShape WEST_BOX = Block.box(0D, 0.0D, 0D, 8D, 16D, 16D);
    private static final VoxelShape EAST_BOX = Block.box(8D, 0.0D, 0D, 16D, 16D, 16D);
    private static final VoxelShape UP_BOX = Block.box(0D, 8.0D, 0D, 16D, 16D, 16D);
    private static final VoxelShape DOWN_BOX = Block.box(0D, 0.0D, 0D, 16D, 8D, 16D);
    public BasketMakerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if(state.getValue(FACING)==Direction.EAST){
            return EAST_BOX;
        }else if(state.getValue(FACING)==Direction.WEST){
            return WEST_BOX;
        }else if(state.getValue(FACING)==Direction.SOUTH){
            return SOUTH_BOX;
        }else if(state.getValue(FACING)==Direction.NORTH){
            return NORTH_BOX;
        }else if(state.getValue(FACING)==Direction.DOWN){
            return DOWN_BOX;
        }else {
            return UP_BOX;
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FACING);
    }
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof BasketMakerBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof BasketMakerBlockEntity blockEntity) {
            if (stack.getItem() == Register.shape_card.get()&&blockEntity.getItem(0).isEmpty()) {
                blockEntity.setItem(0,stack);
                player.setItemInHand(hand,ItemStack.EMPTY);
                player.level().playSound(player,pos, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS,1F,1F);
                return InteractionResult.SUCCESS;
            }else if(stack.is(Register.block_imitation_wand.get())){
                return InteractionResult.PASS;
            }else  {
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
      return InteractionResult.PASS;
    }
    public Direction.Axis getAxis(BlockState state){
        Direction facing=state.getValue(FACING);
        return facing.getAxis();
    }
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {

    }

    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasketMakerBlockEntity(pos,state);
    }
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, Register.BasketMakerBlockEntity.get(), BasketMakerBlockEntity::tick);
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
        if(livingEntity instanceof Player&&state.getBlock() instanceof BasketMakerBlock){
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
                    }
                    break;
            }
            level.removeBlockEntity(pos);
        }

        super.onRemove(state2, level, pos, state2, b);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.basket_maker").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.basket_maker2").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.basket_maker3").withStyle(ChatFormatting.GREEN));
    }
}
