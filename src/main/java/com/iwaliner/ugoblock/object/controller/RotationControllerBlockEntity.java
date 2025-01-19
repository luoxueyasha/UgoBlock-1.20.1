package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.block_imitation_wand.ImitatableBlockEntity;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

public class RotationControllerBlockEntity extends AbstractControllerBlockEntity implements ImitatableBlockEntity {
    private int degreeAngle=-30;
    private int duration=3*20;
    private int visualDegree;
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    /**ContainerDataを1つにまとめるとGUI内のボタンを推した時に連動しちゃったから分けてる*/
    protected final ContainerData degreeAngleDataAccess = new ContainerData() {
        public int get(int i) {
            if (i == 0) {
                return RotationControllerBlockEntity.this.getDegreeAngleForMenu();
            }
            return 0;
        }

        public void set(int i, int j) {
            if (i == 0&&!isMoving&&(getBlockState().getBlock() instanceof RotationControllerBlock && !getBlockState().getValue(RotationControllerBlock.POWERED))) {
                if(RotationControllerBlockEntity.this.degreeAngle>=0&&j<0){
                    setTurnDirection(false);
                }else if(RotationControllerBlockEntity.this.degreeAngle<0&&j>=0){
                    setTurnDirection(true);
                }
                RotationControllerBlockEntity.this.degreeAngle =  j;
            }

        }

        public int getCount() {
            return 1;
        }
    };

    protected final ContainerData durationDataAccess = new ContainerData() {
        public int get(int i) {
            if (i == 0) {
                return Mth.floor(RotationControllerBlockEntity.this.getDuration() / 20f);
            }
            return 0;
        }

        public void set(int i, int j) {
            if (i == 0&&!isMoving) {
                RotationControllerBlockEntity.this.duration = j * 20;
            }

        }

        public int getCount() {
            return 1;
        }
    };
    public RotationControllerBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(Register.RotationController.get(), p_155077_, p_155078_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ugoblock.rotation_controller");
    }
    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new RotationControllerMenu(i,inventory,this,degreeAngleDataAccess,durationDataAccess);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return  items.get(slot);
    }
    public ItemStack removeItem(int ii, int jj) {
        return ContainerHelper.removeItem(this.items, ii, jj);
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        return ContainerHelper.takeItem(this.items, p_58387_);
    }
    public void setItem(int slot, ItemStack stack) {
            ItemStack itemstack = this.items.get(slot);
            boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, stack);
            this.items.set(slot, stack);
            if (stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }

            if (slot == 0 && !flag) {
                this.setChanged();
            }
            if(slot==0&&stack.getItem()==Register.shape_card.get()){
                this.setPositionList(Utils.getPositionList(stack.getTag()));
            }

    }

    public boolean stillValid(Player p_70300_1_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void load(CompoundTag tag) {
        super.load(tag);
       /* List<BlockPos> list=new ArrayList<>();
        CompoundTag posTag=tag.getCompound("positionList");
        for(String s : posTag.getAllKeys()){
                list.add(NbtUtils.readBlockPos(posTag.getCompound(s)));
            }*/
        this.degreeAngle=tag.getInt("degreeAngle");
        if(tag.contains("duration")) {
            this.duration = tag.getInt("duration");
        }else{
            this.duration = 5*20;
        }
        this.visualDegree=tag.getInt("visualDegree");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("degreeAngle",degreeAngle);
        tag.putInt("duration",duration);
        tag.putInt("visualDegree",visualDegree);
        ContainerHelper.saveAllItems(tag, this.items);
    }

    public BlockPos getStartPos(){
        if(level.getBlockState(getBlockPos()).getBlock()instanceof RotationControllerBlock){
            return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING));
        }
        return getBlockPos();
    }


    private void invertBlockRotationDirection(){
        if(getBlockState().getBlock() instanceof RotationControllerBlock){
            setBlockState(getBlockState().cycle(RotationControllerBlock.COUNTER_CLOCKWISE));
        }
    }
    public int getDegreeAngleForMenu(){
        return degreeAngle;
    }
    public int getDegreeAngle(){
        if(degreeAngle>=180){
            return 180;
        }else if(degreeAngle<=-180){
            return -180;
        }else{
            return degreeAngle;
        }
    }
    public boolean isLoop(){
        return degreeAngle==-181||degreeAngle==181;
    }
    public void setDegreeAngle(int degree){
        degreeAngle=degree;
    }

    public void setDuration(int d){
        duration=d;
    }

    public int getDuration() {
        return duration;
    }


    public boolean hasCards(){
        return getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null&&getItem(0).getTag().contains("positionList");
    }
    private boolean isCounterClockwise(){
        return getBlockState().getBlock() instanceof RotationControllerBlock&&getBlockState().getValue(RotationControllerBlock.COUNTER_CLOCKWISE);
    }
    private void setTurnDirection(boolean isCounterClockwise) {
        if (getBlockState().getBlock() instanceof RotationControllerBlock) {
            level.setBlock(getBlockPos(), getBlockState().setValue(RotationControllerBlock.COUNTER_CLOCKWISE, isCounterClockwise), 3);
        }
    }
    public int getVisualDegree(){
        return visualDegree;
    }
    public void setVisualDegree(int degree){
        visualDegree=degree;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RotationControllerBlockEntity blockEntity) {
        if(state.getBlock() instanceof RotationControllerBlock rotationControllerBlock) {
            if(blockEntity.isMoving()&&!state.getValue(RotationControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(RotationControllerBlock.MOVING,true),2);
            }else if(!blockEntity.isMoving()&&state.getValue(RotationControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(RotationControllerBlock.MOVING,false),2);
            }

            if(blockEntity.isLoop()&&!state.getValue(RotationControllerBlock.POWERED)){
                /*boolean flag=false;
                for (Entity entity : level.getEntities((Entity) null, new AABB(pos.relative(state.getValue(RotationControllerBlock.FACING))).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return (o instanceof MovingBlockEntity);
                })) {
                    MovingBlockEntity movingBlock = (MovingBlockEntity) entity;
                    movingBlock.setDiscardTime(-1);
                }*/
            }
            if (blockEntity.getMoveTick() > 0) {
                if (blockEntity.isMoving()) {
                  if (blockEntity.getTickCount() > blockEntity.getMoveTick()+2) {
                        if(!blockEntity.isLoop()) {
                            blockEntity.setMoving(false);
                            blockEntity.setTickCount(0);
                        }
                    }else if (blockEntity.getTickCount() == blockEntity.getMoveTick()) {


                    }/*else if (blockEntity.getTickCount() == blockEntity.getMoveTick() && blockEntity.hasCards()) {
                        BlockPos startPos = blockEntity.getStartPos();
                        List<BlockPos> posList = blockEntity.getPositionList();
                        if ((blockEntity.getDegreeAngle()+blockEntity.getVisualDegree())%90==0){
                            if (blockEntity.isNotFirstTime()) {
                                List<BlockPos> posList0 = blockEntity.getPositionList();
                                for (int i = 0; i < posList0.size(); i++) {
                                    BlockPos eachPos = posList0.get(i);
                                    Vector3f origin = eachPos.getCenter().toVector3f();
                                    Vector3f transition = new Vector3f(eachPos.getX(), eachPos.getY(), eachPos.getZ());

                                    Vector3f transitionRotated = transition.rotateY(Mth.PI * (blockEntity.getVisualDegree()==0? blockEntity.getDegreeAngle() : -blockEntity.getDegreeAngle()) / 180f);
                                    Vector3f positionRotated = origin.add(transitionRotated);
                                    BlockPos rotatedPos = new BlockPos(Mth.floor(positionRotated.x), Mth.floor(positionRotated.y), Mth.floor(positionRotated.z));
                                    posList.set(i, rotatedPos);
                                }
                            }
                    }
                        blockEntity.setPositionList(posList);
                    }*/

                    blockEntity.increaseTickCount(1);
                }

            }

         //   level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }

       }


    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
            return stack.getItem()==Register.shape_card.get();
    }

}
