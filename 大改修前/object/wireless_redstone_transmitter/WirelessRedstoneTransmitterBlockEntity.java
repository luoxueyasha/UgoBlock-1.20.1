package com.iwaliner.ugoblock.object.wireless_redstone_transmitter;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.object.controller.RotationControllerBlock;
import com.iwaliner.ugoblock.object.controller.RotationControllerMenu;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WirelessRedstoneTransmitterBlockEntity extends BlockEntity {
    private DyeColor color1;
    private DyeColor color2;
    private DyeColor color3;

    public WirelessRedstoneTransmitterBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(Register.WirelessRedstoneTransmitterBlockEntity.get(), p_155077_, p_155078_);
    }
    public void load(CompoundTag tag) {
        super.load(tag);
        if(!tag.contains("color1")){
            this.color1=DyeColor.byId(0);
        }else{
            this.color1=DyeColor.byId(tag.getByte("color1"));
        }
        if(!tag.contains("color2")){
            this.color2=DyeColor.byId(0);
        }else{
            this.color2=DyeColor.byId(tag.getByte("color2"));
        }
        if(!tag.contains("color3")){
            this.color3=DyeColor.byId(0);
        }else{
            this.color3=DyeColor.byId(tag.getByte("color3"));
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("color1", color1==null? 0 : (byte) color1.getId());
        tag.putByte("color2", color2==null? 0 : (byte) color2.getId());
        tag.putByte("color3", color3==null? 0 : (byte) color3.getId());
    }

    public void setColor1(DyeColor color1) {
        this.color1 = color1;
    }

    public void setColor2(DyeColor color2) {
        this.color2 = color2;
    }

    public void setColor3(DyeColor color3) {
        this.color3 = color3;
    }

    public DyeColor getColor1() {
        return color1==null? DyeColor.byId(0):color1;
    }

    public DyeColor getColor2() {
        return color2 == null ? DyeColor.byId(0) : color2;
    }
    public DyeColor getColor3() {
        return color3==null? DyeColor.byId(0):color3;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag= super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }


}
