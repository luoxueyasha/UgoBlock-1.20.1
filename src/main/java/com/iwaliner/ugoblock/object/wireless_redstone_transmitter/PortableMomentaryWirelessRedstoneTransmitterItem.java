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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PortableMomentaryWirelessRedstoneTransmitterItem extends Item {
    public PortableMomentaryWirelessRedstoneTransmitterItem(Properties p_41383_) {
        super(p_41383_);
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i0, boolean b0) {
        super.inventoryTick(stack, level, entity, i0, b0);
        CompoundTag tag=stack.getTag();
        if(stack.getItem() instanceof PortableMomentaryWirelessRedstoneTransmitterItem){
            if(tag==null){
                stack.setTag(new CompoundTag());
            }
            int coolTime= tag.contains("coolTime")? tag.getInt("coolTime") : 0;
            if(coolTime>0){
                if(coolTime==1){
                    if (!isColor1Null(stack) && !isColor2Null(stack) && !isColor3Null(stack)) {
                        level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                            data.setSignal(getColor1(stack), getColor2(stack), getColor3(stack), false);
                        });
                        setPowered(stack, false);
                    }
                }
                tag.putInt("coolTime",coolTime-1);
                stack.setTag(tag);
            }
        }
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack=player.getItemInHand(hand);
        if(stack.getItem() instanceof PortableMomentaryWirelessRedstoneTransmitterItem) {
            if (!isColor1Null(stack) && !isColor2Null(stack) && !isColor3Null(stack)) {
                level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    data.setSignal(getColor1(stack), getColor2(stack), getColor3(stack), true);
                });
                setPowered(stack, true);
                CompoundTag tag=stack.getTag();
                tag.putInt("coolTime",4);
                stack.setTag(tag);
                level.playSound(player, player.blockPosition(), SoundEvents.UI_STONECUTTER_SELECT_RECIPE, SoundSource.BLOCKS, 1F, 1F);
                return InteractionResultHolder.fail(stack);
            }else{
                player.displayClientMessage(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter_color_not_set").withStyle(ChatFormatting.YELLOW), true);
            }
        }
        return InteractionResultHolder.fail(stack);
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

    private static DyeColor getColor(ItemStack stack, String tagID){
        if(stack == null || stack.isEmpty() || tagID == null || tagID.isEmpty()){
            return DyeColor.byId(0);
        }
        CompoundTag tag=stack.getTag();
        if(tag==null){
            tag=new CompoundTag();
            stack.setTag(new CompoundTag());
        }
        if(!tag.contains(tagID)){
            return DyeColor.byId(0);
        }
        return DyeColor.byId(tag.getByte(tagID));
    }

    public static DyeColor getColor1(ItemStack stack){
        return getColor(stack, "color1");
    }
    public static DyeColor getColor2(ItemStack stack){
        return getColor(stack, "color2");
    }
    public static DyeColor getColor3(ItemStack stack){
        return getColor(stack, "color3");
    }

    public static void setPowered(ItemStack stack,boolean power){
        if(stack.getTag()!=null) {
            stack.getTag().putBoolean("signal", power);
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
       list.add(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.portable_wireless_redstone_transmitter2").withStyle(ChatFormatting.GREEN));
        if(!isColor1Null(stack)&&!isColor2Null(stack)&&!isColor3Null(stack)){
            list.add(Utils.getComponentFrequencyColors(getColor1(stack),getColor2(stack),getColor3(stack)));
        }
    }
}
