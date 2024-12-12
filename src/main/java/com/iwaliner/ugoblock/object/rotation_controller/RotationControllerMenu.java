package com.iwaliner.ugoblock.object.rotation_controller;

import com.iwaliner.ugoblock.object.ShapeCardSlot;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class RotationControllerMenu extends AbstractContainerMenu {
    public final Container container;
    private final ContainerData degreeAngleData;
    private final ContainerData speedData;



    public RotationControllerMenu(int s, Inventory inventory) {
        this( s, inventory, new SimpleContainer(1), new SimpleContainerData(1), new SimpleContainerData(1));
    }

    public RotationControllerMenu(int s, Inventory inventory, Container c, ContainerData degreeAngleData, ContainerData speedData) {
        super(Register.RotationControllerMenu.get(), s);
        checkContainerSize(c, 1);
        checkContainerDataCount(degreeAngleData, 1);
        checkContainerDataCount(speedData, 1);
        this.container=c;
        this.degreeAngleData=degreeAngleData;
        this.speedData=speedData;
        container.startOpen(inventory.player);
        this.addSlot(new ShapeCardSlot(c, 0, 18, 41));
         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(degreeAngleData);
        this.addDataSlots(speedData);
    }

    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (i <2) {
                if (!this.moveItemStackTo(itemstack1, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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
    public int getDegreeAngle(){
         return degreeAngleData.get(0);
    }
    public void setDegreeAngle(int degreeAngle) {
         degreeAngleData.set(0,degreeAngle);
    }
    public int getSpeed(){
        return Math.round((float) speedData.get(0)/10F);
    }
    public void setSpeed(double speed){
        speedData.set(0,Mth.floor(speed*10D));
    }
    public void addDegreeAngle(int angle) {
        degreeAngleData.set(0,degreeAngleData.get(0)+angle);
    }

    public void addSpeed(double speed) {
        speedData.set(0,speedData.get(0)+Mth.floor(speed*10D));
    }
    public boolean clickMenuButton(Player player, int variable) {
        int degreeAngle=getDegreeAngle();
        int speed=getSpeed();
        if(variable==0){
            if(degreeAngle<-350){
                setDegreeAngle(-360);
            }else{
                addDegreeAngle(-10);
            }
            return true;
        }else if(variable==1){
            if(degreeAngle>-360){
                addDegreeAngle(-1);
            }
            return true;
        }else if(variable==2){
            if(getDegreeAngle()<360) {
                addDegreeAngle(1);
            }
            return true;
        }else if(variable==3){
            if(degreeAngle<350) {
                addDegreeAngle(10);
            }else{
                setDegreeAngle(360);
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
            if(speed<50){
                addSpeed(1);
            }
            return true;
        }else if(variable==7){
            if(speed<=45){
                addSpeed(5);
            }else{
                setSpeed(50);
            }
            return true;
        }
        return false;
    }

}
