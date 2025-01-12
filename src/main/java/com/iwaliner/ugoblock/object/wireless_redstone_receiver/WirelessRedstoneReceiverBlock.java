package com.iwaliner.ugoblock.object.wireless_redstone_receiver;

import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.object.rotation_controller.RotationControllerBlockEntity;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.WirelessRedstoneTransmitterBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WirelessRedstoneReceiverBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty COLOR1 = IntegerProperty.create("color1",0,15);
    public static final IntegerProperty COLOR2 = IntegerProperty.create("color2",0,15);
    public static final IntegerProperty COLOR3 = IntegerProperty.create("color3",0,15);

    public WirelessRedstoneReceiverBlock(Properties p_49795_) {
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
        if(level.getBlockEntity(pos) instanceof WirelessRedstoneReceiverBlockEntity blockEntity) {
            Optional<Vec2> optional = getRelativeHitCoordinatesForBlockFace(result);
            if (optional.isEmpty()||result.getDirection().getAxis()== Direction.Axis.Y) {
                return InteractionResult.PASS;
            } else {
                if(optional.get().x>0.125F&&optional.get().x<0.40625F){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.WHITE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.ORANGE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.MAGENTA);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.YELLOW);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIME);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.PINK);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.CYAN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.PURPLE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BROWN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.GREEN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.RED);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor1(level,pos,state,blockEntity,DyeColor.BLACK);
                        return InteractionResult.SUCCESS;
                    }
                }else if(optional.get().x>0.40625F&&optional.get().x<0.59375F){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.WHITE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.ORANGE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.MAGENTA);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.YELLOW);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIME);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.PINK);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.CYAN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.PURPLE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BROWN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.GREEN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.RED);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor2(level,pos,state,blockEntity,DyeColor.BLACK);
                        return InteractionResult.SUCCESS;
                    }
                }else if(optional.get().x>0.59375F&&optional.get().x<0.75F){
                    if(stack.is(Items.WHITE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.WHITE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.ORANGE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.ORANGE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.MAGENTA_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.MAGENTA);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_BLUE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIGHT_BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.YELLOW_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.YELLOW);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIME_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIME);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PINK_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.PINK);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GRAY_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.LIGHT_GRAY_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.LIGHT_GRAY);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.CYAN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.CYAN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.PURPLE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.PURPLE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLUE_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BLUE);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BROWN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BROWN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.GREEN_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.GREEN);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.RED_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.RED);
                        return InteractionResult.SUCCESS;
                    }else if(stack.is(Items.BLACK_DYE)){
                        setColor3(level,pos,state,blockEntity,DyeColor.BLACK);
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
    private void setColors(Level level,BlockPos pos,BlockState state,WirelessRedstoneReceiverBlockEntity blockEntity,DyeColor color1,DyeColor color2,DyeColor color3){
        blockEntity.setColor1(color1);
        blockEntity.setColor2(color2);
        blockEntity.setColor3(color3);
        level.setBlock(pos,state.setValue(COLOR1,color1.getId()).setValue(COLOR2,color2.getId()).setValue(COLOR3,color3.getId()),3);
        level.playSound(null,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    private void setColor1(Level level,BlockPos pos,BlockState state,WirelessRedstoneReceiverBlockEntity blockEntity,DyeColor color1){
        blockEntity.setColor1(color1);
        level.setBlock(pos,state.setValue(COLOR1,color1.getId()),3);
        level.playSound(null,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    private void setColor2(Level level,BlockPos pos,BlockState state,WirelessRedstoneReceiverBlockEntity blockEntity,DyeColor color2){
        blockEntity.setColor2(color2);
        level.setBlock(pos,state.setValue(COLOR2,color2.getId()),3);
        level.playSound(null,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }
    private void setColor3(Level level,BlockPos pos,BlockState state,WirelessRedstoneReceiverBlockEntity blockEntity,DyeColor color3){
        blockEntity.setColor3(color3);
        level.setBlock(pos,state.setValue(COLOR3,color3.getId()),3);
        level.playSound(null,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
    }



    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessRedstoneReceiverBlockEntity(pos,state);
    }
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, Register.WirelessRedstoneReceiverBlockEntity.get(), WirelessRedstoneReceiverBlockEntity::tick);
    }

    public int getDirectSignal(BlockState p_55127_, BlockGetter p_55128_, BlockPos p_55129_, Direction p_55130_) {
        return p_55127_.getSignal(p_55128_, p_55129_, p_55130_);
    }

    @Override
    public boolean isSignalSource(BlockState p_60571_) {
        return true;
    }

    public int getSignal(BlockState p_55101_, BlockGetter p_55102_, BlockPos p_55103_, Direction p_55104_) {
        return p_55101_.getValue(POWERED) ? 15 : 0;
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if(level.getBlockEntity(pos) instanceof WirelessRedstoneReceiverBlockEntity blockEntity) {
            level.setBlock(pos, state.setValue(POWERED,blockEntity.isRemotePowered()), 3);
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.wireless_redstone_frequency").withStyle(ChatFormatting.GREEN));
    }
}
