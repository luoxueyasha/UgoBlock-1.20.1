package com.iwaliner.ugoblock.object.wireless_redstone_transmitter;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class PortableAlternateWirelessRedstoneTransmitterItem extends Item {
    public PortableAlternateWirelessRedstoneTransmitterItem(Properties p_41383_) {
        super(p_41383_);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack=player.getItemInHand(hand);
        if(stack.getItem() instanceof PortableAlternateWirelessRedstoneTransmitterItem) {
            if (!isColor1Null(stack) && !isColor2Null(stack) && !isColor3Null(stack)) {
                CompoundTag stackTag = Utils.getCompoundTagOrNewTag(stack);
                level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    data.setSignal(getColor1(stackTag), getColor2(stackTag), getColor3(stackTag), !isPowered(stack));
                });
                setPowered(stack, !isPowered(stack));
                level.playSound(player, player.blockPosition(), SoundEvents.UI_STONECUTTER_SELECT_RECIPE, SoundSource.BLOCKS, 1F, 1F);
                return InteractionResultHolder.fail(stack);
            }else{
                player.displayClientMessage(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter_color_not_set").withStyle(ChatFormatting.YELLOW), true);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
    public void setSignal(CompoundTag tag,DyeColor color1,DyeColor color2,DyeColor color3,boolean power){
        String name="signal["+ color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        if(tag==null){
            tag=new CompoundTag();
        }
        tag.putBoolean(name,power);
    }
    public static boolean isColor1Null(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        return !tag.contains("color1");
    }
    public static boolean isColor2Null(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        return !tag.contains("color2");
    }
    public static boolean isColor3Null(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        return !tag.contains("color3");
    }
    public static boolean isPowered(ItemStack stack){
        return Utils.getCompoundTagOrNewTag(stack).getBoolean("signal");
    }

    private static DyeColor getColor(ItemStack stack, String tagID){
        if(stack == null || stack.isEmpty() || tagID == null || tagID.isEmpty()){
            return DyeColor.byId(0);
        }
        return getColor(Utils.getCompoundTagOrNewTag(stack), tagID);
    }

    private static DyeColor getColor(CompoundTag tag, String tagID){
        // tag should not be null at this point.
        if(tag == null || tag.isEmpty() || tagID == null || tagID.isEmpty()){
            return DyeColor.byId(0);
        }
        if(!tag.contains(tagID)){
            return DyeColor.byId(0);
        }
        return DyeColor.byId(tag.getByte(tagID));
    }

    // @debug, we should extract Utils.genNewCompoundTag(stack) from getColor. Do this process before getcolor and input tag itself
    public static DyeColor getColor1(ItemStack stack){
        return getColor(stack, "color1");
    }
    public static DyeColor getColor2(ItemStack stack){
        return getColor(stack, "color2");
    }
    public static DyeColor getColor3(ItemStack stack){
        return getColor(stack, "color3");
    }
    public static DyeColor getColor1(CompoundTag tag){
        return getColor(tag, "color1");
    }
    public static DyeColor getColor2(CompoundTag tag){
        return getColor(tag, "color2");
    }
    public static DyeColor getColor3(CompoundTag tag){
        return getColor(tag, "color3");
    }

    public static void setPowered(ItemStack stack,boolean power){
        if(stack.getTag()!=null) {
            stack.getTag().putBoolean("signal", power);
        }
    }
    public static void setColor1(ItemStack stack,DyeColor color1){
        if(stack.getTag()==null) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putByte("color1", (byte) color1.getId());
    }
    public static void setColor2(ItemStack stack,DyeColor color2){
        if(stack.getTag()==null) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putByte("color2", (byte) color2.getId());
    }
    public static void setColor3(ItemStack stack,DyeColor color3){
        if(stack.getTag()==null) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putByte("color3", (byte) color3.getId());
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
       list.add(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter2").withStyle(ChatFormatting.GREEN));
        if(!isColor1Null(stack)&&!isColor2Null(stack)&&!isColor3Null(stack)){
            CompoundTag stackTag = Utils.getCompoundTagOrNewTag(stack);
            list.add(Utils.getComponentFrequencyColors(getColor1(stackTag),getColor2(stackTag),getColor3(stackTag)));
        }

    }
}
