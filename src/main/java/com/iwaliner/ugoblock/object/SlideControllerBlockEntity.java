package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.register.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SlideControllerBlockEntity extends BaseContainerBlockEntity {
    private List<BlockPos> positionList;
    private BlockPos endPos;
    public SlideControllerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }
    public SlideControllerBlockEntity( BlockPos p_155077_, BlockState p_155078_) {
        super(BlockEntityRegister.SlideController.get(), p_155077_, p_155078_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("ugoblock.container.slide_controller");
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {

    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return false;
    }

    @Override
    public void clearContent() {

    }
    public void load(CompoundTag tag) {
        super.load(tag);
        List<BlockPos> list=new ArrayList<>();
        CompoundTag posTag=tag.getCompound("positionList");
        for(String s : posTag.getAllKeys()){
                list.add(NbtUtils.readBlockPos(posTag.getCompound(s)));
            }
        this.positionList = list;
        this.endPos = NbtUtils.readBlockPos(posTag.getCompound("endPos"));
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(endPos!=null) {
            tag.put("endPos", NbtUtils.writeBlockPos(endPos));
        }
        if(positionList!=null) {
            CompoundTag posTag = new CompoundTag();
            for (int i = 0; i < positionList.size(); i++) {
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(positionList.get(i)));
            }
            tag.put("positionList", posTag);
        }
    }

    public BlockPos getEndPos() {
        return endPos;
    }

    public void setEndPos(BlockPos endPos) {
        this.endPos = endPos;
    }

    public List<BlockPos> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<BlockPos> positionList) {
        this.positionList = positionList;
    }
    public void addPositionToList(BlockPos pos){
        positionList.add(pos);
    }


    public static void tick(Level level, BlockPos pos, BlockState bs, SlideControllerBlockEntity blockEntity) {
       }

}
