package com.iwaliner.ugoblock.object.wireless_redstone_transmitter;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.object.controller.RotationControllerBlockEntity;
import com.iwaliner.ugoblock.object.wireless_redstone_receiver.WirelessRedstoneReceiverBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WirelessRedstoneTransmitterBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty COLOR1 = IntegerProperty.create("color1",0,15);
    public static final IntegerProperty COLOR2 = IntegerProperty.create("color2",0,15);
    public static final IntegerProperty COLOR3 = IntegerProperty.create("color3",0,15);
    public WirelessRedstoneTransmitterBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(COLOR1,0).setValue(COLOR2,0).setValue(COLOR3,0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED,COLOR1,COLOR2,COLOR3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof WirelessRedstoneTransmitterBlockEntity blockEntity) {
            Optional<Vec2> optional = getRelativeHitCoordinatesForBlockFace(result);
            if (optional.isEmpty()||result.getDirection().getAxis()== Direction.Axis.Y) {
                return InteractionResult.PASS;
            } else {
                if(/*optional.get().x>0.125F&&*/optional.get().x<0.40625F){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.WHITE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.ORANGE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.MAGENTA,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.YELLOW,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIME,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.PINK,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.CYAN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.PURPLE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BROWN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.GREEN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.RED,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BLACK,player);
                        return InteractionResult.SUCCESS;
                    }
                }else if(optional.get().x>0.40625F&&optional.get().x<0.59375F){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.WHITE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.ORANGE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.MAGENTA,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.YELLOW,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIME,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.PINK,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.CYAN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.PURPLE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BROWN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.GREEN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.RED,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BLACK,player);
                        return InteractionResult.SUCCESS;
                    }
                }else if(optional.get().x>0.59375F/*&&optional.get().x<0.75F*/){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.WHITE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.ORANGE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.MAGENTA,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.YELLOW,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIME,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.PINK,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.CYAN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.PURPLE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BLUE,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BROWN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.GREEN,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.RED,player);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BLACK,player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult p_261714_) {
        Direction direction = p_261714_.getDirection();

        BlockPos blockpos = p_261714_.getBlockPos().relative(direction);
        Vec3 vec3 = p_261714_.getLocation().subtract((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        Optional optional;
        switch (direction) {
            case NORTH:
                optional = Optional.of(new Vec2((float)(1.0D - d0), (float)d1));
                break;
            case SOUTH:
                optional = Optional.of(new Vec2((float)d0, (float)d1));
                break;
            case WEST:
                optional = Optional.of(new Vec2((float)d2, (float)d1));
                break;
            case EAST:
                optional = Optional.of(new Vec2((float)(1.0D - d2), (float)d1));
                break;
            case DOWN:
            case UP:
                optional = Optional.empty();
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return optional;

    }

    private void setColor1(Level level, BlockPos pos, BlockState state, WirelessRedstoneTransmitterBlockEntity blockEntity, DyeColor color1, Player player){
        level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
            boolean alreadyExist=!data.isSignalNull(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3());

        });
        blockEntity.setColor1(color1);
        level.setBlock(pos,state.setValue(COLOR1,color1.getId()),3);
        level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
            boolean alreadyExist=!data.isSignalNull(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3());
            if(alreadyExist){
                player.displayClientMessage(Component.translatable("info.ugoblock.frequency_already_exists_color1").withStyle(ChatFormatting.GREEN), false);
            }
        });
        level.playSound(player,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    private void setColor2(Level level,BlockPos pos,BlockState state,WirelessRedstoneTransmitterBlockEntity blockEntity,DyeColor color2, Player player){
        blockEntity.setColor2(color2);
        level.setBlock(pos,state.setValue(COLOR2,color2.getId()),3);
        level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
            boolean alreadyExist=!data.isSignalNull(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3());
            if(alreadyExist){
                player.displayClientMessage(Component.translatable("info.ugoblock.frequency_already_exists_color2").withStyle(ChatFormatting.YELLOW), false);
            }
        });
        level.playSound(player,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    private void setColor3(Level level,BlockPos pos,BlockState state,WirelessRedstoneTransmitterBlockEntity blockEntity,DyeColor color3, Player player){
        blockEntity.setColor3(color3);
        level.setBlock(pos,state.setValue(COLOR3,color3.getId()),3);
        level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
            boolean alreadyExist=!data.isSignalNull(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3());
            if(alreadyExist){
                 player.displayClientMessage(Component.translatable("info.ugoblock.frequency_already_exists_color3").withStyle(ChatFormatting.RED), false);
            }
        });
        level.playSound(player,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
            if (flag != level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof WirelessRedstoneTransmitterBlockEntity blockEntity) {
                level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    data.setSignal(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3(),level.hasNeighborSignal(pos));
                });
                          level.setBlock(pos, state.cycle(POWERED), 2);

            }

    }


    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessRedstoneTransmitterBlockEntity(pos,state);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean b) {
        if(state2.isAir()&&level.getBlockEntity(pos) instanceof WirelessRedstoneTransmitterBlockEntity blockEntity) {
            level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
               data.setSignalNull(blockEntity.getColor1(),blockEntity.getColor2(),blockEntity.getColor3());
            });

            level.removeBlockEntity(pos);
        }

        super.onRemove(state2, level, pos, state2, b);
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.wireless_redstone_frequency").withStyle(ChatFormatting.GREEN));
    }
}
