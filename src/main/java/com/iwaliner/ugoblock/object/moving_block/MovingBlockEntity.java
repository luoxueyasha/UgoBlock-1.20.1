package com.iwaliner.ugoblock.object.moving_block;


import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.mixin.BlockDisplayMixin;
import com.iwaliner.ugoblock.mixin.DisplayMixin;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class MovingBlockEntity extends Display.BlockDisplay {
    /**移動量を座標で指定。変位なので始点座標でも終点座標でもない。*/
    public static final EntityDataAccessor<BlockPos> DATA_TRANSITION_POSITION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   /**始点座標*/
    public static final EntityDataAccessor<BlockPos> DATA_START_LOCATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> DATA_ROTATABLE_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> DATA_TRIGNOMETRIC_FUNCTION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<CompoundTag> DATA_COMPOUND_TAG_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<Integer> DATA_DEGREE_ANGLE_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);
    List<BlockPos> posList=new ArrayList<>();
    List<BlockState> stateList=new ArrayList<>();
    List<CompoundTag> blockEntityDataList =new ArrayList<>();
    int minX=0;
    int minY=0;
    int minZ=0;
    int maxX=0;
    int maxY=0;
    int maxZ=0;



    public MovingBlockEntity(EntityType<?> p_271022_, Level p_270442_) {
        super(Register.MoveableBlock.get(), p_270442_);
        this.noPhysics = false;
        this.noCulling = false;
    }
    public MovingBlockEntity(Level level, BlockPos startPos, BlockState state, int startTick, int duration, BlockPos endPos,CompoundTag tag) {
        super(Register.MoveableBlock.get(), level);
        this.setPos(startPos.getX()+0.5D,startPos.getY()+0.5D,startPos.getZ()+0.5D);
        this.entityData.set(BlockDisplayMixin.getData(),state);
        this.entityData.set(DATA_TRANSITION_POSITION_ID,endPos);
        this.entityData.set(DATA_START_LOCATION_ID,startPos);
        this.entityData.set(DisplayMixin.getDataStartTick(),startTick);
        this.entityData.set(DisplayMixin.getDataDuration(),duration);
        this.entityData.set(DATA_TRIGNOMETRIC_FUNCTION_ID,trigonometricFunctionType.NONE.getID());
        this.entityData.set(DATA_ROTATABLE_ID,false);
        this.entityData.set(DATA_COMPOUND_TAG_ID,tag);

        this.noPhysics = false;
        this.noCulling = false;

    }
    public MovingBlockEntity(Level level, BlockPos startPos, BlockState state, int startTick, int duration,trigonometricFunctionType type,int degree,CompoundTag tag) {
        super(Register.MoveableBlock.get(), level);
        this.setPos(startPos.getX()+0.5D,startPos.getY()+0.5D,startPos.getZ()+0.5D);
        this.entityData.set(BlockDisplayMixin.getData(),state);
        this.entityData.set(DATA_START_LOCATION_ID,startPos);
        this.entityData.set(DisplayMixin.getDataStartTick(),startTick);
        this.entityData.set(DisplayMixin.getDataDuration(),duration);
        this.entityData.set(DATA_TRIGNOMETRIC_FUNCTION_ID,type.getID());
        this.entityData.set(DATA_ROTATABLE_ID,true);
        this.entityData.set(DATA_COMPOUND_TAG_ID,tag);
        this.entityData.set(DATA_DEGREE_ANGLE_ID,degree);
        this.noPhysics = false;
        this.noCulling = false;

    }

    /*public boolean shouldRenderAtSqrDistance(double p_31574_) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0) || d0 == 0.0D) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return p_31574_ < d0 * d0;
    }*/
    public boolean shouldRenderAtSqrDistance(double p_31769_) {
        double d0 = 64D/*16.0D*/;
        d0 *= 64.0D * getViewScale();
        return p_31769_ < d0 * d0;
    }
    /*@Override
    public boolean shouldRenderAtSqrDistance(double d) {
        return true;
    }

    @Override
    public boolean shouldRender(double p_20296_, double p_20297_, double p_20298_) {
        return true;
    }*/

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRANSITION_POSITION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_START_LOCATION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_ROTATABLE_ID,false);
        this.entityData.define(DATA_TRIGNOMETRIC_FUNCTION_ID,trigonometricFunctionType.NONE.getID());
        this.entityData.define(DATA_COMPOUND_TAG_ID,new CompoundTag());
        this.entityData.define(DATA_DEGREE_ANGLE_ID,0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_277476_) {
        super.onSyncedDataUpdated(p_277476_);
        if (p_277476_.equals(BlockDisplayMixin.getData())) {
            this.updateRenderState = true;
            this.setBoundingBox(this.makeBoundingBox());

        }
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("transition")) {
            this.entityData.set(DATA_TRANSITION_POSITION_ID, NbtUtils.readBlockPos(tag.getCompound("transition")));
        }
        if (tag.contains("start_location")) {
            this.entityData.set(DATA_START_LOCATION_ID, NbtUtils.readBlockPos(tag.getCompound("start_location")));
        }
        if (tag.contains("tickCount")) {
            this.tickCount=tag.getInt("tickCount");
        }
        if (tag.contains("rotatable")) {
            this.entityData.set(DATA_ROTATABLE_ID,tag.getBoolean("rotatable"));
        }
        if (tag.contains("trigonometric_function_type")) {
            this.entityData.set(DATA_TRIGNOMETRIC_FUNCTION_ID,tag.getByte("trigonometric_function_type"));
        }
        if (tag.contains("compoundTag")) {
            this.entityData.set(DATA_COMPOUND_TAG_ID,tag.getCompound("compoundTag"));
        }
        if (tag.contains("degreeAngle")) {
            this.entityData.set(DATA_DEGREE_ANGLE_ID,tag.getInt("degreeAngle"));
        }
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("transition",NbtUtils.writeBlockPos(entityData.get(DATA_TRANSITION_POSITION_ID)));
        tag.put("start_location",NbtUtils.writeBlockPos(entityData.get(DATA_START_LOCATION_ID)));
        tag.putInt("tickCount",tickCount);
        tag.putBoolean("rotatable",entityData.get(DATA_ROTATABLE_ID));
        tag.putByte("trigonometric_function_type",entityData.get(DATA_TRIGNOMETRIC_FUNCTION_ID));
        tag.put("compoundTag",entityData.get(DATA_COMPOUND_TAG_ID));
        tag.putInt("degreeAngle",entityData.get(DATA_DEGREE_ANGLE_ID));
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }


    /**trueにすると、当たり判定内に入ったときにはじき出される*/
    public boolean canBeCollidedWith() {
        return false;
    }


    public boolean isPickable() {
        return true;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        super.makeBoundingBox();
       if(minX==0&&minY==0&&minZ==0&&maxX==0&&maxY==0&&maxZ==0){
           makeBoundingBoxFirst();
       }
        AABB aabb=new AABB(position().x-0.5D+minX,position().y-0.5D+minY,position().z-0.5D+minZ,position().x-0.5D+maxX+1D,position().y-0.5D+maxY+1D,position().z-0.5D+maxZ+1D);
        return aabb;
    }
    private  void makeBoundingBoxFirst() {
         for(BlockPos eachPos : getPosList()){
             if(minX>eachPos.getX()){
                 minX=eachPos.getX();
             }
             if(maxX<eachPos.getX()){
                 maxX=eachPos.getX();
             }
             if(minY>eachPos.getY()){
                 minY=eachPos.getY();
             }
             if(maxY<eachPos.getY()){
                 maxY=eachPos.getY();
             }
             if(minZ>eachPos.getZ()){
                 minZ=eachPos.getZ();
             }
             if(maxZ<eachPos.getZ()){
                 maxZ=eachPos.getZ();
             }
         }
    }
    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return makeBoundingBox();
    }
    @Override
    public void tick() {
        super.tick();
        setBoundingBox(makeBoundingBox());
        BlockPos transition= getTransition();
        int duration=getDuration();
        int startTick=getStartTick();
        if(!shouldRotate()) {
            if (duration > 0 && tickCount >= startTick && tickCount < startTick + duration) {
                Vec3 pos = new Vec3( getActualPos().x+(double) transition.getX() / (double) duration,  getActualPos().y+(double) transition.getY() / (double) duration, getActualPos().z+(double) transition.getZ() / (double) duration);
               setActualPos(pos);

                for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return !(o instanceof MovingBlockEntity);
                })) {
                        entity.fallDistance=0f;
                        Vec3 speed = new Vec3((double) transition.getX() / (double) duration, (double) transition.getY() / (double) duration, (double) transition.getZ() / (double) duration);
                        entity.setDeltaMovement(speed);
                        entity.setOnGround(true);
                }

            } else if (duration > 0 && tickCount == startTick + duration + 0) {
                for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return !(o instanceof MovingBlockEntity);
                })) {
                    entity.fallDistance=0f;
                    entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x,0D,entity.getDeltaMovement().z));
                    entity.setOnGround(true);
                }
                makeBlock();
            } else if (duration > 0 && tickCount == startTick + duration + 1) {
                discard();
            }
        }else{
            rotate();
        }
    }
    public boolean shouldFixFighting(){ /**このエンティティとブロックが完全に重なりZ-fightingを起こす可能性があるかどうか*/
        return (getDuration()>0&&tickCount>getStartTick()+getDuration()-0)||tickCount<5;
    }
    private void makeBlock(){ /**移動し終わってブロック化する*/
        if(!level().isClientSide) {
            BlockPos originPos = new BlockPos(getStartLocation().getX() + getTransition().getX(), getStartLocation().getY() + getTransition().getY(), getStartLocation().getZ() + getTransition().getZ());
            for (int i=0;i<getPosList().size();i++) {
                BlockPos pos=originPos.offset(getPosList().get(i).getX(),getPosList().get(i).getY(),getPosList().get(i).getZ());
                BlockState movingState = getStateList().get(i);
                CompoundTag movingBlockEntityData = getBlockEntityDataList().get(i);
                if (level().getBlockState(pos).canBeReplaced()) {
                    if (movingState.getBlock() == Blocks.OBSERVER) {
                        level().setBlock(pos, movingState, 82);
                        level().scheduleTick(pos, movingState.getBlock(), 2);
                    } else {
                        level().setBlock(pos, movingState, 82);
                        level().scheduleTick(pos, movingState.getBlock(), 2);
                    }
                    if (!movingBlockEntityData.isEmpty() && movingState.hasBlockEntity()) {
                        if (movingBlockEntityData != null) {
                            BlockEntity blockentity = level().getBlockEntity(pos);

                            if (blockentity != null) {
                                blockentity.load(movingBlockEntityData);
                            }
                        }
                    }
                } else { /**移動してきた場所が他のブロックで埋まっていた場合。アイテム化する。*/
                    if (!level().isClientSide && !movingState.is(Register.TAG_DISABLE_ITEM_DROP)) { /**通常*/
                        ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(movingState.getBlock()));
                        level().addFreshEntity(itemEntity);
                    } else if (!level().getBlockState(pos).is(Register.TAG_DISABLE_ITEM_DROP)) { /**アイテムをドロップしたくないブロックが移動してきたがその場所が埋まっていた場合。もともとあったブロックをアイテム化したうえでドロップしたくないブロックを設置する。*/
                        ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(level().getBlockState(pos).getBlock()));
                        level().addFreshEntity(itemEntity);
                        level().setBlock(pos, movingState, 82);
                    }
                    discard();
                }
            }
            setCompoundTag(new CompoundTag());
        }
    }


    public Vec3 getActualPos(){
        return position();
    }
    public void setActualPos(Vec3 vec3){
        setPos(vec3.x,vec3.y,vec3.z);
    }

    public BlockPos getActualBlockPos(){
        return new BlockPos(Mth.floor(getActualPos().x),Mth.floor(getActualPos().y),Mth.floor(getActualPos().z));
    }
    public void setActualBlockPos(BlockPos blockPos){
        setPos(blockPos.getCenter());
    }


    public Vec3 getDisplacementVisualDuringRotation(){

        int time=tickCount-getStartTick();
        if(tickCount>getStartTick()+getDuration()){
            time=getStartTick()+getDuration();
        }
        float[] rotationPos;
        /*float firstAngle;
        switch (getTrigonometricFunctionType()){
            case Y_COUNTERCLOCKWISE ->firstAngle= (float) Math.atan2(getDisplacementVisual().x+0.5D,getDisplacementVisual().z+0.5D);
            default-> firstAngle= 0f;
        }*/
        float finalAngle=getLeftRotation().angle();
        float radian=/*firstAngle+*/time*(finalAngle)/(float)getDuration();
        switch (getTrigonometricFunctionType()){
            case Y_COUNTERCLOCKWISE -> rotationPos= new float[]{(float) Math.sin(radian), 0f, (float) Math.cos(radian)};
            default-> rotationPos= new float[]{0f, 0f, 0f};
        }


        return new Vec3(rotationPos[0]-0.5D,rotationPos[1]-0.5D,rotationPos[2]-0.5D);
    }
    public BlockState getState(){
        return entityData.get(BlockDisplayMixin.getData());
    }
    public void setState(BlockState state){
         entityData.set(BlockDisplayMixin.getData(),state);
    }
    private int getStartTick(){
        return entityData.get(DisplayMixin.getDataStartTick());
    }
    private BlockPos getTransition(){
        return entityData.get(DATA_TRANSITION_POSITION_ID);
    }
    private BlockPos getStartLocation(){
        return entityData.get(DATA_START_LOCATION_ID);
    }

    private Quaternionf getLeftRotation(){
        return entityData.get(DisplayMixin.getDataLeftRotation());
    }

    private Quaternionf getRightRotation(){
        return entityData.get(DisplayMixin.getDataRightRotation());
    }
    public void setLeftRotation(Quaternionf quaternionf){
        entityData.set(DisplayMixin.getDataLeftRotation(),quaternionf);
    }
    public void setRightRotation(Quaternionf quaternionf){
        entityData.set(DisplayMixin.getDataRightRotation(),quaternionf);
    }
    public void setDuration(int duration){
        entityData.set(DisplayMixin.getDataDuration(),duration);
    }
    private int getDuration(){
        return entityData.get(DisplayMixin.getDataDuration());
    }
    public boolean shouldRotate(){
        return entityData.get(DATA_ROTATABLE_ID);
    }

    private trigonometricFunctionType getTrigonometricFunctionType(){
       return trigonometricFunctionType.getType(entityData.get(DATA_TRIGNOMETRIC_FUNCTION_ID));
    }
    private void setTrigonometricFunctionType(trigonometricFunctionType type){
        entityData.set(DATA_TRIGNOMETRIC_FUNCTION_ID,type.getID());
    }
    public CompoundTag getCompoundTag(){
        return entityData.get(DATA_COMPOUND_TAG_ID);
    }
    private void setCompoundTag(CompoundTag tag){
        entityData.set(DATA_COMPOUND_TAG_ID,tag);
    }
    private float getRadianAngle(){
        return Mth.PI*(float) getDegreeAngle()/180f;
    }
    public void setDegreeAngle(int degreeAngle){
        entityData.set(DATA_DEGREE_ANGLE_ID,degreeAngle);
    }
    public int getDegreeAngle(){
        return entityData.get(DATA_DEGREE_ANGLE_ID);
    }

    public List<BlockPos> getPosListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("positionList")){
            entityTag.put("positionList",new CompoundTag());
        }
        CompoundTag posTag=entityTag.getCompound("positionList");
        List<BlockPos> posList=new ArrayList<>();
        for(int i=0; i< posTag.size();i++){
            if (posTag.contains("location_" + String.valueOf(i))) {
                posList.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
            }
        }
        return posList;
    }

    public List<BlockPos> getPosList() {
        if(posList==null||posList.isEmpty()){
            posList=getPosListFirst();
        }
            return posList;

    }
    public List<BlockState> getStateList() {
        if(stateList==null||stateList.isEmpty()){
            stateList=getStateListFirst();
        }
        return stateList;

    }

    public List<BlockState> getStateListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("stateList")){
            entityTag.put("stateList",new CompoundTag());
        }
        CompoundTag stateTag=entityTag.getCompound("stateList");
        List<BlockState> stateList=new ArrayList<>();
        for(int i=0; i<getPosList().size();i++){
            if (stateTag.contains("state_" + String.valueOf(i))) {
                stateList.add(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),stateTag.getCompound("state_" + String.valueOf(i))));
            }
        }
        return stateList;
    }

    public List<CompoundTag> getBlockEntityDataList() {
        if(blockEntityDataList ==null|| blockEntityDataList.isEmpty()){
            blockEntityDataList =getBlockEntityDataListFirst();
        }
        return blockEntityDataList;

    }

    public List<CompoundTag> getBlockEntityDataListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("blockEntityList")){
            entityTag.put("blockEntityList",new CompoundTag());
        }
        CompoundTag blockEntityTag=entityTag.getCompound("blockEntityList");
        List<CompoundTag> blockEntityList=new ArrayList<>();
        for(int i=0; i<getPosList().size();i++){
            if (blockEntityTag.contains("blockEntity_" + String.valueOf(i))) {
                blockEntityList.add(blockEntityTag.getCompound("blockEntity_" + String.valueOf(i)));
            }
        }
        return blockEntityList;
    }

    private boolean shouldRender(BlockState state){
        return state==null||(state.getShape(null,null)!= Shapes.block())||!Utils.isBlockSolid(state);
    }

    @Override
    public boolean mayInteract(Level level, BlockPos poa) {
        return true;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand hand) {
        BlockState state=getState();
        if(state.getBlock() instanceof FenceGateBlock||state.getBlock() instanceof TrapDoorBlock){
             level().playSound(player, blockPosition(), state.getValue(BlockStateProperties.OPEN) ? SoundEvents.FENCE_GATE_OPEN : SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0F, level().getRandom().nextFloat() * 0.1F + 0.9F);
            setState(state.cycle(BlockStateProperties.OPEN));

            return InteractionResult.SUCCESS;
        }

       return super.interactAt(player, vec3, hand);
    }

    private  void rotate(){
        MovingBlockEntity.trigonometricFunctionType type=getTrigonometricFunctionType();
        int transitionDegree=getDegreeAngle();
        if(tickCount<=getDuration()) {
            if (type == trigonometricFunctionType.Y_COUNTERCLOCKWISE) {

                setLeftRotation(new Quaternionf(new AxisAngle4d(getRadianAngle(), 0D, 1D, 0D)));
                setRightRotation(new Quaternionf(new AxisAngle4d(0D, 0D, 1D, 0D)));
            } else if (type == trigonometricFunctionType.Y_CLOCKWISE) {
                setLeftRotation(new Quaternionf(new AxisAngle4d(getRadianAngle(), 0D, 1D, 0D)));
                setRightRotation(new Quaternionf(new AxisAngle4d(0D, 0D, 1D, 0D)));

            }
        }
    }
    public  enum trigonometricFunctionType {
        NONE((byte) 0),
        X_COUNTERCLOCKWISE((byte) 1),
        X_CLOCKWISE((byte) 2),

        Y_COUNTERCLOCKWISE((byte) 3),
        Y_CLOCKWISE((byte) 4),
        Z_COUNTERCLOCKWISE((byte) 5),
        Z_CLOCKWISE((byte) 6);

        private byte id;

        private trigonometricFunctionType(byte id) {
            this.id = id;
        }
        public static trigonometricFunctionType getType(byte id){
            return switch (id) {
                case 0 -> NONE;
                case 1 -> X_COUNTERCLOCKWISE;
                case 2 -> X_CLOCKWISE;
                case 3 -> Y_COUNTERCLOCKWISE;
                case 4 -> Y_CLOCKWISE;
                case 5 -> Z_COUNTERCLOCKWISE;
                case 6 -> Z_CLOCKWISE;
                default -> NONE;
            };

        }
        public byte getID()
        {
            return this.id;
        }
    }
}
