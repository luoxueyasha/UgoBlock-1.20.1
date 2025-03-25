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

public class PortableAlternateWirelessRedstoneTransmitterItem extends Item {
    public PortableAlternateWirelessRedstoneTransmitterItem(Properties p_41383_) {
        super(p_41383_);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack=player.getItemInHand(hand);
        if(stack.getItem() instanceof PortableAlternateWirelessRedstoneTransmitterItem) {
            if (!isColor1Null(stack) && !isColor2Null(stack) && !isColor3Null(stack)) {
                level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    data.setSignal(getColor1(stack), getColor2(stack), getColor3(stack), !isPowered(stack));
                });
                setPowered(stack, !isPowered(stack));
                level.playSound(player, player.blockPosition(), SoundEvents.UI_STONECUTTER_SELECT_RECIPE, SoundSource.BLOCKS, 1F, 1F);
                return InteractionResultHolder.consume(stack);
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
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
            return tag.getBoolean("signal");
    }
    public static DyeColor getColor1(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        if(!tag.contains("color1")){
           return DyeColor.byId(0);
        }else{
           return DyeColor.byId((int) tag.getByte("color1"));
        }
    }
    public static DyeColor getColor2(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        if(!tag.contains("color2")){
            return DyeColor.byId(0);
        }else{
            return DyeColor.byId((int)tag.getByte("color2"));
        }
    }
    public static DyeColor getColor3(ItemStack stack){
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        if(!tag.contains("color3")){
            return DyeColor.byId(0);
        }else{
            return DyeColor.byId((int)tag.getByte("color3"));
        }
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
            list.add(Utils.getComponentFrequencyColors(getColor1(stack),getColor2(stack),getColor3(stack)));
        }

    }
    public static boolean makeSureTagIsValid(@javax.annotation.Nullable CompoundTag tag) {
        if (tag == null) {
            return false;
        } else if (!tag.contains("color1")) {
            return false;
        } else if (!tag.contains("color2")) {
            return false;
        } else if (!tag.contains("color3")) {
            return false;
        }else{
            return true;
        }
    }
}
