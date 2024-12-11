package com.iwaliner.ugoblock.object;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EndLocationCardItem extends Item {
    public EndLocationCardItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level=context.getLevel();
        BlockPos pos=context.getClickedPos();
        BlockState state=level.getBlockState(pos);
        ItemStack stack=context.getItemInHand();
        if(stack.getItem() instanceof EndLocationCardItem) {
            CompoundTag tag =stack.getTag();
            if(tag==null){
                tag=new CompoundTag();
            }
            if(state.getBlock() instanceof SlideControllerBlock){

            }else{
                tag.put("end_location", NbtUtils.writeBlockPos(pos));
                stack.setTag(tag);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if(stack.getItem() instanceof EndLocationCardItem) {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                tag = new CompoundTag();
            }

            if (tag.contains("end_location")) {
                tag.put("end_location" , NbtUtils.writeBlockPos(ShapeCardItem.errorPos()));
            }
        }
        return true;
    }
    public static BlockPos getEndPos(CompoundTag tag){
        return NbtUtils.readBlockPos(tag.getCompound("end_location"));
    }
    public static void setEndPos(ItemStack stack,BlockPos pos){
        if(stack.getTag()!=null) {
            stack.getTag().put("end_location", NbtUtils.writeBlockPos(pos));
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
       list.add(Component.translatable("info.ugoblock.end_location_card"));
        if(stack.getTag()!=null) {
            BlockPos pos = NbtUtils.readBlockPos(stack.getTag().getCompound("end_location"));
            if (!pos.equals(ShapeCardItem.errorPos())) {
                list.add(Component.translatable("info.ugoblock.end_location_card_location").append("[").append(String.valueOf(pos.getX())).append(", ").append(String.valueOf(pos.getY())).append(", ").append(String.valueOf(pos.getZ())).append("]"));
            }
        }
    }
}
