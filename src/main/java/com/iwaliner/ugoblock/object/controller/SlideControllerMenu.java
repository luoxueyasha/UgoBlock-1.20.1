package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class SlideControllerMenu extends AbstractContainerMenu {
    public final Container container;
    private final ContainerData startTickData;
    private final ContainerData speedData;
    private final ContainerData onewayData;


    public SlideControllerMenu(int s, Inventory inventory) {
        this( s, inventory, new SimpleContainer(2), new SimpleContainerData(1), new SimpleContainerData(1), new SimpleContainerData(1));
    }

    public SlideControllerMenu(int s, Inventory inventory,Container c,ContainerData startTickData,ContainerData speedData,ContainerData onewayData) {
        super(Register.SlideControllerMenu.get(), s);
        checkContainerSize(c, 2);
        checkContainerDataCount(startTickData, 1);
        checkContainerDataCount(speedData, 1);
        this.container=c;
        this.startTickData=startTickData;
        this.speedData=speedData;
        this.onewayData=onewayData;
        container.startOpen(inventory.player);
        this.addSlot(new ShapeCardSlot(c, 0, 18, 41));
        this.addSlot(new VectorCardSlot(c,  1, 36, 41));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(startTickData);
        this.addDataSlots(speedData);
        this.addDataSlots(onewayData);
    }

    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (i <2) {
                if (!this.moveItemStackTo(itemstack1, 2, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    public void removed(Player p_39251_) {
        super.removed(p_39251_);
        this.container.stopOpen(p_39251_);
    }
    public boolean stillValid(Player p_39242_) {
        return this.container.stillValid(p_39242_);
    }
    public int getStartTime(){
         return Mth.floor((double) startTickData.get(0)/20D);
    }


    public void setStartTime(int startTime) {
         startTickData.set(0,startTime*20);

    }
    public void setOneway(boolean b){
        if(b){
            onewayData.set(0,1);
        }else{
            onewayData.set(0,0);
        }
    }
    public boolean isOneway(){
        return onewayData.get(0)==1;
    }


    public int getSpeed(){
        return Math.round((float) speedData.get(0)/10F);
    }
    public void setSpeed(double speed){
        speedData.set(0,Mth.floor(speed*10D));
    }
    public void addStartTime(int startTime) {
        startTickData.set(0,startTickData.get(0)+startTime*20);
    }

    public void addSpeed(double speed) {
        speedData.set(0,speedData.get(0)+Mth.floor(speed*10D));
    }
    public boolean clickMenuButton(Player player, int variable) {
        Level level=player.level();
        BlockPos playerPos=player.blockPosition();

        int startTime=getStartTime();
        int speed=getSpeed();
        if(variable==0){
            if(startTime<5){
                setStartTime(0);
            }else{
                addStartTime(-5);
            }
            return true;
        }else if(variable==1){
            if(startTime>0){
                addStartTime(-1);
            }
            return true;
        }else if(variable==2){
            if(startTime<30) {
                addStartTime(1);
            }
            return true;
        }else if(variable==3){
            if(startTime<25) {
                addStartTime(5);
            }else{
                setStartTime(30);
            }
            return true;
        }else if(variable==4){
            if(speed<=5){
                setSpeed(1);
            }else{
                addSpeed(-5);
            }
            return true;
        }else if(variable==5){
            if(speed>1){
                addSpeed(-1);
            }
            return true;
        }else if(variable==6){
            if(speed<8){
                addSpeed(1);
            }
            return true;
        }else if(variable==7){
            if(speed<=8-5){
                addSpeed(5);
            }else{
                setSpeed(8);
            }
            return true;
        }else if(variable==8){
            setOneway(!isOneway());
            return true;
        }
        return false;
    }

}
