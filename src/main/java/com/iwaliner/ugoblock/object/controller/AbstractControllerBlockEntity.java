package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractControllerBlockEntity extends BaseContainerBlockEntity {
    protected boolean isNotFirstTime;
    protected boolean isMoving;
    protected int tickCount;
    protected int startTime;
    protected BlockState imitatingState;
    protected AbstractControllerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }
    public boolean isMoving(){
        return isMoving;
    }
    public void setMoving(boolean b){
        this.isMoving=b;
    }
    public int getMoveTick(){
        return startTime+getDuration();
    }
    public int getTickCount(){
        return tickCount;
    }
    public void setTickCount(int i){
        this.tickCount=i;
    }
    public void increaseTickCount(int i){
        this.tickCount+=i;
    }
    public List<BlockPos> getPositionList() {
        if(getItem(0).getItem()== Register.shape_card.get()&&getItem(0).getTag()!=null){
            return Utils.getPositionList(getItem(0).getTag());
        }
        return new ArrayList<>();
    }
    public void clearPositionList(){
        getItem(0).setTag(null);
    }
    public boolean isNotFirstTime(){
        return isNotFirstTime;
    }
    public void setNotFirstTime(boolean b){
        isNotFirstTime=b;
    }

    public void setPositionList(List<BlockPos> positionList) {
        if(getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null){
            Utils.setPositionList(getItem(0),positionList);
        }
    }


    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    protected int getDuration(){
        return 0;
    }

    public BlockState getImitatingState() {
        setChanged();
        return imitatingState==null? Blocks.AIR.defaultBlockState() : imitatingState;
    }

    public void setImitatingState(BlockState imitatingState) {
        this.imitatingState = imitatingState;
        setChanged();
    }


    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
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
    public boolean isLoop(){
        return false;
    }

    public void setVisualDegree(int degree) {

    }


    public void load(CompoundTag tag) {
        super.load(tag);
        this.isNotFirstTime = tag.getBoolean("isNotFirstTime");
        this.isMoving = tag.getBoolean("isMoving");
        this.tickCount = tag.getInt("tickCount");
        this.startTime = tag.getInt("startTime");
        if (tag.contains("imitatingState")) {
            HolderGetter<Block> holdergetter = (HolderGetter<Block>)(this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup());
            this.imitatingState = NbtUtils.readBlockState(holdergetter, tag.getCompound("imitatingState"));
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isNotFirstTime",isNotFirstTime);
        tag.putBoolean("isMoving",isMoving);
        tag.putInt("tickCount",tickCount);
        tag.putInt("startTime",startTime);
        if(imitatingState!=null) {
            tag.put("imitatingState", NbtUtils.writeBlockState(imitatingState));
        }
    }

}
