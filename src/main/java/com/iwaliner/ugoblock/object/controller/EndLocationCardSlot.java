package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EndLocationCardSlot extends Slot {
    public EndLocationCardSlot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
        super(p_40223_, p_40224_, p_40225_, p_40226_);
    }

    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.getItem()== Register.end_location_card.get();
    }

    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

}
