package com.iwaliner.ugoblock.object.block_imitation_wand;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemDecorator;

public class BlockImitationWandDecoration implements IItemDecorator {
    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if(stack.is(Register.block_imitation_wand.get())){
            CompoundTag tag=stack.getTag();
            if(tag!=null&&tag.contains("imitatingState")&&Minecraft.getInstance().level!=null){
                guiGraphics.pose().translate(0D,0D,-10D);
                BlockState state= NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), tag.getCompound("imitatingState"));
                guiGraphics.renderItem(new ItemStack(Item.byBlock(state.getBlock())),xOffset,yOffset);
                return true;
            }
        }
        return false;
    }
}
