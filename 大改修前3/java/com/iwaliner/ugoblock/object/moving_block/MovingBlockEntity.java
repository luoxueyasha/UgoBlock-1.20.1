package com.iwaliner.ugoblock.object.moving_block;


import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.mixin.BlockDisplayMixin;
import com.iwaliner.ugoblock.mixin.DisplayMixin;
import com.iwaliner.ugoblock.object.seat.SeatBlock;
import com.iwaliner.ugoblock.object.seat.SeatEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovingBlockEntity extends Display.BlockDisplay {

    /**移動量を座標で指定。変位なので始点座標でも終点座標でもない。*/
    public static final EntityDataAccessor<BlockPos> DATA_TRANSITION_POSITION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BLOCK_POS);

    /**始点座標*/
    public static final EntityDataAccessor<BlockPos> DATA_START_LOCATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BLOCK_POS);


    /**tureなら回転制御機から生まれた個体。falseならスライド制御機から生まれた個体。*/
    public static final EntityDataAccessor<Boolean> DATA_ROTATABLE_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BOOLEAN);

    /**回転時の回転軸*/
    public static final EntityDataAccessor<Byte> DATA_AXIS_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BYTE);

    /**移動している最中の各座標、ブロック、ブロックエンティティのデータを格納*/
    public static final EntityDataAccessor<CompoundTag> DATA_COMPOUND_TAG_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

    /**動かす角度。左回りが正。(度数法)*/
    public static final EntityDataAccessor<Integer> DATA_DEGREE_ANGLE_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);

    /**動かす角度ではなくて、表示をどれだけ遷移させるか。つまり主にコントローラーの動力OFFで動きを元に戻すとき用。(度数法)*/
    public static final EntityDataAccessor<Integer> DATA_VISUAL_ROTATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);

    /**指定したtick後にこのエンティティを削除する。*/
    public static final EntityDataAccessor<Integer> DATA_DISCARD_TIME_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);

    /**周回の回転であればtrue、でなければfalse*/
    public static final EntityDataAccessor<Boolean> DATA_IS_LOOP_ROTATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BOOLEAN);

    /**向きのあるブロックを回転して表示するかどうか*/
    public static final EntityDataAccessor<Boolean> DATA_SHOULD_ROTATE_STATE_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.BOOLEAN);

    /**1tickごとにカウントが増えていく*/
    public static final EntityDataAccessor<Integer> DATA_TIME_COUNT_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);

    /**回転させたり移動させたりしたときに表示上の補完を行うが、その補完を行う時間。*/
    private static final EntityDataAccessor<Integer> DATA_POS_ROT_INTERPOLATION_DURATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);
    /**動き始めた瞬間の向き(Yrot)*/
    public static final EntityDataAccessor<Integer> DATA_STAT_ROTATION_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<CompoundTag> DATA_POSITION_TAG_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<CompoundTag> DATA_STATE_TAG_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public static final EntityDataAccessor<CompoundTag> DATA_BLOCKENTITY_TAG_ID = SynchedEntityData.defineId(MovingBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

     @Nullable
    private MovingBlockEntity.PosRotInterpolationTarget posRotInterpolationTarget;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    /**絶対座標ではなく、制御機の正面からの相対座標。*/
    private List<BlockPos> posList=new ArrayList<>();

    /**posListの各位置におけるブロックのデータ*/
    private List<BlockState> stateList=new ArrayList<>();

    /**posListの各位置におけるブロックエンティティのデータ*/
    private List<CompoundTag> blockEntityDataList =new ArrayList<>();

    /**バスケットとなる座標範囲。絶対座標ではなく、制御機の正面からの相対座標。バスケット作成機からの相対座標ではない。*/
    private List<BlockPos> basketPosList=new ArrayList<>();

    /**バスケットとなる各座標において、どこが回転中心か。バスケット作成機の正面の座標が該当。絶対座標ではなく、制御機の正面からの相対座標。*/
    private List<BlockPos> basketOriginPosList=new ArrayList<>();

    /**basketPosListの各位置におけるブロックのデータ*/
    private List<BlockState> basketStateList=new ArrayList<>();

    /**basketPosListの各位置におけるブロックエンティティのデータ*/
    private List<CompoundTag> basketBlockEntityDataList =new ArrayList<>();

    /**このbasketを作成しているバスケット作成機は大元のposListだとどの番号に相当するか*/
    private List<Integer> basketIndexList =new ArrayList<>();

    /**座席の初期位置の相対座標*/
    private List<BlockPos> seatPosList=new ArrayList<>();

    /**座席の回転中心の相対座標*/
    private List<BlockPos> seatOriginPosList=new ArrayList<>();

    /**座席がゴンドラ内かどうか*/
    private List<Boolean> seatIsInBasketList=new ArrayList<>();
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
        this.blocksBuilding=false;
    }

    public MovingBlockEntity(Level level, BlockPos startPos, BlockState state, int startTick, int duration, Direction.Axis axis, int degree, CompoundTag posNBT, CompoundTag stateNBT, CompoundTag blockentityNBT, CompoundTag tag, int visualDegree, boolean isLoop, boolean rotateState,BlockPos endPos) {
        super(Register.MoveableBlock.get(), level);
        this.setPos(startPos.getX()+0.5D,startPos.getY()+0.5D,startPos.getZ()+0.5D);
        this.entityData.set(BlockDisplayMixin.getData(),state);
        this.entityData.set(DATA_START_LOCATION_ID,startPos);
        this.entityData.set(DisplayMixin.getDataStartTick(),startTick);
        this.entityData.set(DisplayMixin.getDataDuration(),duration);
        this.entityData.set(DATA_COMPOUND_TAG_ID,tag);
        this.noPhysics = false;
        this.noCulling = false;
        this.entityData.set(DATA_TIME_COUNT_ID,0);
        if(axis==null){
            this.entityData.set(DATA_TRANSITION_POSITION_ID,endPos);
            this.entityData.set(DATA_AXIS_ID,AxisType.NONE.getID());
            this.entityData.set(DATA_ROTATABLE_ID,false);
            this.entityData.set(DATA_VISUAL_ROTATION_ID,0);
            this.entityData.set(DATA_STAT_ROTATION_ID,0);
        }else{
            this.entityData.set(DATA_ROTATABLE_ID,true);
            this.entityData.set(DATA_SHOULD_ROTATE_STATE_ID,rotateState);
            this.entityData.set(DATA_DEGREE_ANGLE_ID, degree);
            this.entityData.set(DATA_VISUAL_ROTATION_ID, visualDegree);
            this.entityData.set(DATA_IS_LOOP_ROTATION_ID,isLoop);
            this.entityData.set(DATA_AXIS_ID,AxisType.getType(axis).getID());
        }
        this.entityData.set(DATA_POSITION_TAG_ID,posNBT);
        this.entityData.set(DATA_STATE_TAG_ID,stateNBT);
        this.entityData.set(DATA_BLOCKENTITY_TAG_ID,blockentityNBT);
        this.blocksBuilding=false;
    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_POS_ROT_INTERPOLATION_DURATION_ID, 0);
        this.entityData.define(DATA_TRANSITION_POSITION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_START_LOCATION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_ROTATABLE_ID,false);
        this.entityData.define(DATA_AXIS_ID,AxisType.NONE.getID());
        this.entityData.define(DATA_COMPOUND_TAG_ID,new CompoundTag());
        this.entityData.define(DATA_DEGREE_ANGLE_ID,0);
        this.entityData.define(DATA_VISUAL_ROTATION_ID,0);
        this.entityData.define(DATA_DISCARD_TIME_ID,0);
        this.entityData.define(DATA_IS_LOOP_ROTATION_ID,false);
        this.entityData.define(DATA_SHOULD_ROTATE_STATE_ID,false);
        this.entityData.define(DATA_TIME_COUNT_ID,0);
        this.entityData.define(DATA_STAT_ROTATION_ID,0);
        this.entityData.define(DATA_POSITION_TAG_ID,new CompoundTag());
        this.entityData.define(DATA_STATE_TAG_ID,new CompoundTag());
        this.entityData.define(DATA_BLOCKENTITY_TAG_ID,new CompoundTag());
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
        if (tag.contains("rotatable")) {
            this.entityData.set(DATA_ROTATABLE_ID,tag.getBoolean("rotatable"));
        }
        if (tag.contains("axis")) {
            this.entityData.set(DATA_AXIS_ID,tag.getByte("axis"));
        }
        if (tag.contains("compoundTag")) {
            this.entityData.set(DATA_COMPOUND_TAG_ID,tag.getCompound("compoundTag"));
        }
        if (tag.contains("degreeAngle")) {
            this.entityData.set(DATA_DEGREE_ANGLE_ID,tag.getInt("degreeAngle"));
        }
        if (tag.contains("VisualRot")) {
            this.entityData.set(DATA_VISUAL_ROTATION_ID,tag.getInt("VisualRot"));
        }
        if (tag.contains("discardTime")) {
            this.entityData.set(DATA_DISCARD_TIME_ID,tag.getInt("discardTime"));
        }
        if (tag.contains("loopRotation")) {
            this.entityData.set(DATA_IS_LOOP_ROTATION_ID,tag.getBoolean("loopRotation"));
        }
        if (tag.contains("shouldRotateState")) {
            this.entityData.set(DATA_SHOULD_ROTATE_STATE_ID,tag.getBoolean("shouldRotateState"));
        }
        if (tag.contains("timeCount")) {
            this.entityData.set(DATA_TIME_COUNT_ID,tag.getInt("timeCount"));
        }
        if (tag.contains("teleport_duration", 99)) {
            int k = tag.getInt("teleport_duration");
            this.setPosRotInterpolationDuration(Mth.clamp(k, 0, 59));
        }
        if (tag.contains("startRotation")) {
            this.entityData.set(DATA_STAT_ROTATION_ID,tag.getInt("startRotation"));
        }
        if (tag.contains("positionList")) {
            this.entityData.set(DATA_POSITION_TAG_ID,tag.getCompound("positionList"));
        }
        if (tag.contains("stateList")) {
            this.entityData.set(DATA_STATE_TAG_ID,tag.getCompound("stateList"));
        }
        if (tag.contains("blockEntityList")) {
            this.entityData.set(DATA_BLOCKENTITY_TAG_ID,tag.getCompound("blockEntityList"));
        }
        if (tag.contains("teleport_duration")) {
            this.entityData.set(DATA_POS_ROT_INTERPOLATION_DURATION_ID,tag.getInt("teleport_duration"));
        }

    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("transition",NbtUtils.writeBlockPos(entityData.get(DATA_TRANSITION_POSITION_ID)));
        tag.put("start_location",NbtUtils.writeBlockPos(entityData.get(DATA_START_LOCATION_ID)));
        tag.putBoolean("rotatable",entityData.get(DATA_ROTATABLE_ID));
        tag.putByte("axis",entityData.get(DATA_AXIS_ID));
        tag.put("compoundTag",entityData.get(DATA_COMPOUND_TAG_ID));
        tag.putInt("degreeAngle",entityData.get(DATA_DEGREE_ANGLE_ID));
        tag.putInt("VisualRot",entityData.get(DATA_VISUAL_ROTATION_ID));
        tag.putInt("discardTime",entityData.get(DATA_DISCARD_TIME_ID));
        tag.putBoolean("loopRotation",entityData.get(DATA_IS_LOOP_ROTATION_ID));
        tag.putBoolean("shouldRotateState",entityData.get(DATA_SHOULD_ROTATE_STATE_ID));
        tag.putInt("timeCount", entityData.get(DATA_TIME_COUNT_ID));
        tag.putInt("teleport_duration", this.getPosRotInterpolationDuration());
        tag.putInt("startRotation",entityData.get(DATA_STAT_ROTATION_ID));
        tag.put("positionList",entityData.get(DATA_POSITION_TAG_ID));
        tag.put("stateList",entityData.get(DATA_STATE_TAG_ID));
        tag.put("blockEntityList",entityData.get(DATA_BLOCKENTITY_TAG_ID));
        tag.putInt("teleport_duration",entityData.get(DATA_POS_ROT_INTERPOLATION_DURATION_ID));
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !isLoopRotation();
    }


    /**trueにすると、当たり判定内に入ったときにはじき出される*/
    public boolean canBeCollidedWith() {
        return false;
    }


    public boolean isPickable() {
       // return true;
        return !isLoopRotation();
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        super.makeBoundingBox();
       /* if(!shouldRotate()) {
            if (minX == 0 && minY == 0 && minZ == 0 && maxX == 0 && maxY == 0 && maxZ == 0) {
                makeBoundingBoxFirst();
            }
            AABB aabb = new AABB(position().x - 0.5D + minX, position().y - 0.5D + minY, position().z - 0.5D + minZ, position().x - 0.5D + maxX + 1D, position().y - 0.5D + maxY + 1D, position().z - 0.5D + maxZ + 1D);
            return aabb;
        }else{*/
            return new AABB(getActualBlockPos());
       // }
    }
    private  void makeBoundingBoxFirst() {
        if(getPosList().size()==getStateList().size()) {
            boolean flag = true;
            for (int i = 0; i < getPosList().size(); i++) {
                BlockPos eachPos = getPosList().get(i);
                BlockState eachState = getStateList().get(i);
                VoxelShape shape = eachState.getCollisionShape(level(), this.blockPosition());
                if (flag && !shape.isEmpty()) {
                    minX = eachPos.getX();
                    minY = eachPos.getY();
                    minZ = eachPos.getZ();
                    maxX = eachPos.getX();
                    maxY = eachPos.getY();
                    maxZ = eachPos.getZ();
                    flag = false;
                    break;
                }
            }
            for (int i = 0; i < getPosList().size(); i++) {
                BlockPos eachPos = getPosList().get(i);
                BlockState eachState = getStateList().get(i);
                VoxelShape shape = eachState.getCollisionShape(level(), this.blockPosition());
                if (minX > eachPos.getX() && !shape.isEmpty()) {
                    minX = eachPos.getX();
                }
                if (maxX < eachPos.getX() && !shape.isEmpty()) {
                    maxX = eachPos.getX();
                }
                if (minY > eachPos.getY() && !shape.isEmpty()) {
                    minY = eachPos.getY();
                }
                if (maxY < eachPos.getY() && !shape.isEmpty()) {
                    maxY = eachPos.getY();
                }
                if (minZ > eachPos.getZ() && !shape.isEmpty()) {
                    minZ = eachPos.getZ();
                }
                if (maxZ < eachPos.getZ() && !shape.isEmpty()) {
                    maxZ = eachPos.getZ();
                }
            }
        }
    }
    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return makeBoundingBox();
    }
    @Override
    public void tick() {
        this.tickLerp();

        for (Entity entity : level().getEntities((Entity) null, new AABB(getActualBlockPos()).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
            return (o instanceof MovingBlockEntity);
        })) {
            MovingBlockEntity movingBlock = (MovingBlockEntity) entity;
            if(movingBlock!=this) {
                for(int i=0;i<this.posList.size();i++){
                    BlockPos eachPos=posList.get(i).offset(this.getStartLocation());
                    if(movingBlock.posList.contains(eachPos.offset(-movingBlock.getStartLocation().getX(),-movingBlock.getStartLocation().getY(),-movingBlock.getStartLocation().getZ()))){
                        movingBlock.discard();
                    }
                }
                for(int i=0;i<movingBlock.posList.size();i++){
                    BlockPos eachPos=movingBlock.posList.get(i).offset(movingBlock.getStartLocation());
                    if(this.posList.contains(eachPos.offset(-this.getStartLocation().getX(),-this.getStartLocation().getY(),-this.getStartLocation().getZ()))){
                        movingBlock.discard();
                    }
                }

                    //movingBlock.discard();
            }
        }
        super.tick();
        if (this.level().isClientSide) {
            if (this.posRotInterpolationTarget != null) {
                if (this.posRotInterpolationTarget.steps == 0) {
                    this.posRotInterpolationTarget.applyTargetPosAndRot(this);
                    this.setOldPosAndRot();
                    this.posRotInterpolationTarget = null;
                } else {
                    this.posRotInterpolationTarget.applyLerpStep(this);
                    --this.posRotInterpolationTarget.steps;
                    if (this.posRotInterpolationTarget.steps == 0) {
                        this.posRotInterpolationTarget = null;
                    }
                }
            }
        }
        setBoundingBox(makeBoundingBox());
        BlockPos transition= getTransition();
        int duration=getDuration();
        int startTick=getStartTick();
        if(getDiscardTime()>1){
            setDiscardTime(getDiscardTime()-1);
        }else if(getDiscardTime()==1){
            getPassengers().forEach(Entity::discard);
            discard();
        }else if(getDiscardTime()==-1){
            rotateAndMakeBlock(0);
            getPassengers().forEach(Entity::discard);
            discard();
        }
        if(!shouldRotate()) {
            if (duration > 0 && getTimeCount() >= startTick && getTimeCount() < startTick + duration) {
                Vec3 pos = new Vec3( getActualPos().x+(double) transition.getX() / (double) duration,  getActualPos().y+(double) transition.getY() / (double) duration, getActualPos().z+(double) transition.getZ() / (double) duration);
               setActualPos(pos);

                /*for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return !(o instanceof MovingBlockEntity);
                })) {

                        entity.fallDistance=0f;
                        Vec3 speed = new Vec3((double) transition.getX() / (double) duration, (double) transition.getY() / (double) duration, (double) transition.getZ() / (double) duration);
                        entity.setDeltaMovement(speed);
                        entity.setOnGround(true);
                }*/

            } else if (duration > 0 && getTimeCount() == startTick + duration + 0) {
                /*for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return !(o instanceof MovingBlockEntity);
                })) {
                    entity.fallDistance=0f;
                    entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x,0D,entity.getDeltaMovement().z));
                    entity.setOnGround(true);
                }*/
                if(getTimeCount()>3) {
                    makeBlock();
                }
            } else if (duration > 0 && getTimeCount() >= startTick + duration + 1) {
                getPassengers().forEach(Entity::discard);
                discard();
            }
        }else{
            rotate();
            //makeCollisionEntity();
        }
        makeCollisionEntity();
        addTimeCount(1);
    }

    private void makeBlock(){ /**移動し終わってブロック化する*/
        if(!level().isClientSide) {
           /* List<Entity> seatPassengerList=new ArrayList<>();
            for(int i=0;i<getPassengers().size();i++){
                Entity entity=getPassengers().get(i);
                Entity passenger=null;
                if(entity instanceof SeatEntity){
                    if(entity.getFirstPassenger()!=null) {
                        passenger = entity.getFirstPassenger();
                    }
                    seatPassengerList.add(passenger);
                }
               // seatPassengerList.add(passenger);
            }*/
            BlockPos originPos = new BlockPos(getStartLocation().getX() + getTransition().getX(), getStartLocation().getY() + getTransition().getY(), getStartLocation().getZ() + getTransition().getZ());
            for (int i=0;i<getPosList().size();i++) {
                BlockPos pos=originPos.offset(getPosList().get(i).getX(),getPosList().get(i).getY(),getPosList().get(i).getZ());
                BlockState movingState = getStateList().get(i);
                CompoundTag movingBlockEntityData = getBlockEntityDataList().get(i);
                if (level().getBlockState(pos).canBeReplaced()) {
                    if (movingState.getBlock() == Blocks.OBSERVER) {
                        level().setBlock(pos, movingState, 82);
                        level().scheduleTick(pos, movingState.getBlock(), 2);
                    } else if(movingState.getBlock() instanceof RedStoneWireBlock||movingState.getBlock() instanceof DiodeBlock||movingState.getBlock() instanceof RedstoneTorchBlock) {
                        level().setBlock(pos, movingState, 2);
                        level().scheduleTick(pos, movingState.getBlock(), 2);
                    }else {
                        level().setBlock(pos, movingState, 82);
                    }
                    if(movingState.getBlock()==Register.seat.get()){
                        AABB aabb = new AABB(pos);
                        Entity seatEntity0=null;
                        Entity passenger=null;
                        for (Entity entity : level().getEntities((Entity) null, aabb.move(0D, 0D, 0D).inflate(0d, 0d, 0d), (o) -> {
                            return (o instanceof SeatEntity);
                        })) {
                            seatEntity0=entity;
                            break;
                        }
                        if(seatEntity0!=null&&seatEntity0.getFirstPassenger()!=null){
                            passenger=seatEntity0.getFirstPassenger();
                        }
                        SeatEntity seatEntity=new SeatEntity(level(),pos,false);
                        if(!level().isClientSide()){
                            level().addFreshEntity(seatEntity);
                        }
                        if(passenger!=null) {
                            passenger.startRiding(seatEntity);
                        }
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

                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level())).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                            for (ItemStack itemStack : movingState.getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level().isClientSide) {
                                    level().addFreshEntity(itemEntity);
                                }
                        }

                    } else if (!level().getBlockState(pos).is(Register.TAG_DISABLE_ITEM_DROP)) { /**アイテムをドロップしたくないブロックが移動してきたがその場所が埋まっていた場合。もともとあったブロックをアイテム化したうえでドロップしたくないブロックを設置する。*/
                        LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level())).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                        for (ItemStack itemStack : level().getBlockState(pos).getDrops(lootparams$builder)) {
                            ItemEntity itemEntity = new ItemEntity(level(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                            if (!level().isClientSide) {
                                level().addFreshEntity(itemEntity);
                            }
                        }
                        level().setBlock(pos, movingState,82);
                    }
                    discard();
                }
            }
            for (int i=0;i<getPosList().size();i++) {
                BlockPos pos = originPos.offset(getPosList().get(i).getX(), getPosList().get(i).getY(), getPosList().get(i).getZ());
                BlockState movingState = getStateList().get(i);
                level().updateNeighborsAt(pos,movingState.getBlock());
            }
            setCompoundTag(new CompoundTag());
        }
    }
    public void rotateAndMakeBlock(int degree){ /**回転し終わってブロック化する*/
        int degree2= degree+getVisualRot()+getStartRotation();
        if(!level().isClientSide&&degree2%90==0) {
           List<BlockState> blockStateList=getStateList();
            List<BlockState> basketStateList2=getBasketStateList();
            List<CompoundTag> blockEntityList=getBlockEntityDataList();
            List<CompoundTag> basketBlockEntityList=getBasketBlockEntityDataList();
            List<BlockPos> rotatedPosList=Utils.rotatePosList(getPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),degree2);
            int basketAngle= degree2==0&&degree%90==0? degree: degree2;
            List<BlockPos> rotatedBasketPosList=Utils.rotateBasketPosList(getBasketPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),basketAngle,getBasketOriginPosList());
                for (int j = 0; j < rotatedBasketPosList.size(); j++) {
                   rotatedPosList.add(rotatedBasketPosList.get(j));
                   blockStateList.add(basketStateList2.get(j));
                   blockEntityList.add(basketBlockEntityList.get(j));
               }

            for (int i=0;i<rotatedPosList.size();i++) {
                BlockPos pos=rotatedPosList.get(i);
                    BlockState movingState = blockStateList.get(i);
                    CompoundTag movingBlockEntityData = blockEntityList.get(i);
                    if (level().getBlockState(pos).canBeReplaced()) {
                        BlockState newState=movingState;
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)&&getAxis()== Direction.Axis.Y){
                            Direction oldDirection=movingState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                            Direction newDirection=oldDirection;
                            if(degree==90){
                              newDirection=oldDirection.getCounterClockWise();
                            }else if(degree==-90){
                                newDirection=oldDirection.getClockWise();
                            }else if(degree==180||degree==-180){
                                newDirection=oldDirection.getOpposite();
                            }
                           newState= movingState.setValue(BlockStateProperties.HORIZONTAL_FACING,newDirection);
                        }else if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.AXIS)){
                            Direction.Axis oldAxis=movingState.getValue(BlockStateProperties.AXIS);
                            Direction.Axis newAxis=oldAxis;
                            if(degree==90||degree==-90){
                                if(getAxis()== Direction.Axis.X){
                                    newAxis= oldAxis== Direction.Axis.Y? Direction.Axis.Z : oldAxis== Direction.Axis.X? Direction.Axis.X : Direction.Axis.Y;
                                }else if(getAxis()== Direction.Axis.Y){
                                    newAxis= oldAxis== Direction.Axis.X? Direction.Axis.Z : oldAxis== Direction.Axis.Y? Direction.Axis.Y :Direction.Axis.X;
                                }else if(getAxis()== Direction.Axis.Z){
                                    newAxis= oldAxis== Direction.Axis.X? Direction.Axis.Y :oldAxis== Direction.Axis.Z? Direction.Axis.Z : Direction.Axis.X;
                                }
                            }
                           newState=movingState.setValue(BlockStateProperties.AXIS,newAxis);
                        }
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.FACING)){
                            Direction oldDirection=movingState.getValue(BlockStateProperties.FACING);
                            Direction newDirection=oldDirection;
                            if(degree==90){
                                newDirection=oldDirection.getCounterClockWise(getAxis());
                            }else if(degree==-90){
                                newDirection=oldDirection.getClockWise(getAxis());
                            }else if(degree==180||degree==-180){
                                newDirection=oldDirection.getClockWise(getAxis()).getClockWise(getAxis());
                            }
                           newState=movingState.setValue(BlockStateProperties.FACING,newDirection);

                        }
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.HALF)&&getAxis()!= Direction.Axis.Y){
                            Half oldHalf=movingState.getValue(BlockStateProperties.HALF);
                            Half newHalf=oldHalf;
                           if(degree==180||degree==-180){
                               if(oldHalf==Half.BOTTOM){
                                   newHalf=Half.TOP;
                               }else if(oldHalf==Half.TOP){
                                   newHalf=Half.BOTTOM;
                               }
                           }
                           newState= movingState.setValue(BlockStateProperties.HALF,newHalf);
                        }
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.SLAB_TYPE)&&getAxis()!= Direction.Axis.Y){
                            SlabType oldHalf=movingState.getValue(BlockStateProperties.SLAB_TYPE);
                            SlabType newHalf=oldHalf;
                            if(degree==180||degree==-180){
                                if(oldHalf==SlabType.BOTTOM){
                                    newHalf=SlabType.TOP;
                                }else if(oldHalf==SlabType.TOP){
                                    newHalf=SlabType.BOTTOM;
                                }
                            }
                           newState= movingState.setValue(BlockStateProperties.SLAB_TYPE,newHalf);
                        }
                        if (shouldRotateState()&&movingState.getBlock() instanceof CrossCollisionBlock) {
                            boolean north=movingState.getValue(CrossCollisionBlock.NORTH);
                            boolean east=movingState.getValue(CrossCollisionBlock.EAST);
                            boolean south=movingState.getValue(CrossCollisionBlock.SOUTH);
                            boolean west=movingState.getValue(CrossCollisionBlock.WEST);
                            boolean north2=north;
                            boolean east2=east;
                            boolean south2=south;
                            boolean west2=west;

                            if (getAxis() == Direction.Axis.X) {
                                if(degree==180||degree==-180){
                                    north2=south;
                                    south2=north;
                                }
                            } else if (getAxis() == Direction.Axis.Y) {
                                if(degree==90) {
                                    north2 = east;
                                    west2 = north;
                                    south2 = west;
                                    east2 = south;
                                }else if(degree==-90) {
                                    north2 = west;
                                    west2 = south;
                                    south2 = east;
                                    east2 = north;
                                }else if(degree==180||degree==-180) {
                                    east2=west;
                                    west2=east;
                                    north2=south;
                                    south2=north;
                                }
                            } else if (getAxis() == Direction.Axis.Z) {
                                if(degree==180||degree==-180){
                                    east2=west;
                                    west2=east;
                                }
                            }
                            newState = movingState.setValue(CrossCollisionBlock.NORTH,north2).setValue(CrossCollisionBlock.EAST,east2).setValue(CrossCollisionBlock.SOUTH,south2).setValue(CrossCollisionBlock.WEST,west2);
                        }
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)){
                            DoubleBlockHalf oldHalf = movingState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
                            DoubleBlockHalf newHalf = oldHalf;
                            if (degree == 180 || degree == -180) {
                                if (getAxis() == Direction.Axis.X||getAxis() == Direction.Axis.Z) {
                                    newHalf= oldHalf==DoubleBlockHalf.LOWER? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER;
                                }
                            }
                            newState=movingState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,newHalf);

                        }
                        if(shouldRotateState()&&movingState.hasProperty(BlockStateProperties.BED_PART)){
                            BedPart oldPart = movingState.getValue(BlockStateProperties.BED_PART);
                            BedPart newPart = oldPart;
                            if (degree == 180 || degree == -180) {
                                if (getAxis() == Direction.Axis.X||getAxis() == Direction.Axis.Z) {
                                    newPart= oldPart==BedPart.FOOT? BedPart.HEAD : BedPart.FOOT;
                                }
                            }
                            newState=movingState.setValue(BlockStateProperties.BED_PART,newPart);

                        }

                        if (movingState.getBlock() == Blocks.OBSERVER) {
                            level().setBlock(pos, newState, 82);
                            level().scheduleTick(pos, newState.getBlock(), 2);
                        } else {
                            level().setBlock(pos, newState, 82);

                        }
                        if(movingState.getBlock()==Register.seat.get()){
                            AABB aabb = new AABB(pos);
                            Entity seatEntity0=null;
                            Entity passenger=null;
                            for (Entity entity : level().getEntities((Entity) null, aabb.move(0D, 0D, 0D).inflate(0d, 0d, 0d), (o) -> {
                                return (o instanceof SeatEntity);
                            })) {
                                seatEntity0=entity;
                                break;
                            }
                            if(seatEntity0!=null&&seatEntity0.getFirstPassenger()!=null){
                                passenger=seatEntity0.getFirstPassenger();
                            }
                            SeatEntity seatEntity=new SeatEntity(level(),pos,false);
                            if(!level().isClientSide()){
                                level().addFreshEntity(seatEntity);
                            }
                            if(passenger!=null) {
                                passenger.startRiding(seatEntity);
                            }
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

                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level())).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                            for (ItemStack itemStack : movingState.getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level().isClientSide) {
                                    level().addFreshEntity(itemEntity);
                                }
                            }
                        } else if (!level().isClientSide &&!level().getBlockState(pos).is(Register.TAG_DISABLE_ITEM_DROP)) { /**アイテムをドロップしたくないブロックが移動してきたがその場所が埋まっていた場合。もともとあったブロックをアイテム化したうえでドロップしたくないブロックを設置する。*/
                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level())).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                            for (ItemStack itemStack : level().getBlockState(pos).getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level().isClientSide) {
                                    level().addFreshEntity(itemEntity);
                                }
                            }
                            level().setBlock(pos, movingState, 82);
                        }
                }
            }
            for (int i=0;i<rotatedPosList.size();i++) {
                BlockPos pos = rotatedPosList.get(i);
                BlockState movingState = blockStateList.get(i);
                level().updateNeighborsAt(pos,movingState.getBlock());
            }
        }
    }
    private void discardAfterRotate( int degree) { /**回転し終わってブロック化する*/
        if (!level().isClientSide) {
            int degree2= degree+getVisualRot()+getStartRotation();

            if ( degree2 % 90 == 0) {
                getPassengers().forEach(Entity::discard);
                discard();
            }
        }
    }
    private void makeCollisionEntity(){ /**当たり判定用のエンティティを召喚する*/
        int duration=getDuration();
        if(duration>0){
        int startTick=getStartTick();
        List<Vec3> entityOffsetPosList=new ArrayList<>();
        List<Vec3> originPosList=new ArrayList<>();
        for(int i=0;i<getPosList().size();i++){
            entityOffsetPosList.add(Vec3.ZERO);
            originPosList.add(getActualPos());
        }
        for(int i=0;i<getBasketOriginPosList().size();i++){
            BlockPos basketPos=getBasketPosList().get(i);
            BlockPos originPos=getBasketOriginPosList().get(i);
            BlockPos differ=originPos.offset(-basketPos.getX(),-basketPos.getY(),-basketPos.getZ());
            entityOffsetPosList.add(differ.getCenter());
            originPosList.add(originPos.getCenter().add(blockPosition().getX(),blockPosition().getY(),blockPosition().getZ()));
        }
        boolean flag= getTimeCount() >= startTick && getTimeCount() < startTick + duration;
        float thetaDegreeF= (flag||isLoopRotation())? (getTimeCount()-startTick)*getDegreeAngle()/(float)duration : getDegreeAngle();
        float degreeCombinedF=thetaDegreeF/*+getVisualRot()*/+getStartRotation();
        if(!shouldRotate()){
            degreeCombinedF=0f;
        }
        List<Vec3> rotatedVec3BasketList=Utils.rotateVec3BasketPosList(getBasketPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),degreeCombinedF,getBasketOriginPosList());
        List<Vec3> rotatedVec3List=Utils.rotateVec3PosList(getPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),degreeCombinedF);
        List<Vec3> rotatedOriginPosList=Utils.rotateVec3PosList(getPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),degreeCombinedF);
        rotatedOriginPosList.addAll(Utils.rotateVec3PosList(getBasketOriginPosList(),BlockPos.ZERO,getActualBlockPos(),getAxis(),degreeCombinedF));
        rotatedVec3List.addAll(rotatedVec3BasketList);
        List<BlockState> blockStateList=getStateList();
        blockStateList.addAll(getBasketStateList());
        //posList2.addAll(getBasketPosList());
            /*for(int i=0;i<rotatedVec3ListFloat.size();i++) {
                Vec3 eachVec3 = rotatedVec3ListFloat.get(i);
                double s = 0.4D;
                AABB aabb2 = new AABB(eachVec3.x - s, eachVec3.y - s, eachVec3.z - s, eachVec3.x +s, eachVec3.y +s, eachVec3.z +s);
                for (Entity entity : level().getEntities((Entity) null, aabb2.move(0D,0D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof SeatEntity);
                })) {
                    entity.moveTo(eachVec3);
                }
            }*/
            /*for(int i=0;i<getPosList().size();i++) {
                Vec3 eachVec3= getPosList().get(i).getCenter();
                //s=0d;
                AABB aabb = new AABB(eachVec3.x - 0.5D, eachVec3.y - 0.5D, eachVec3.z - 0.5D, eachVec3.x - 0.5D+ 1D, eachVec3.y - 0.5D + 1D, eachVec3.z - 0.5D  + 1D);
                for (Entity entity : level().getEntities((Entity) null, aabb.move(0D,1D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof LivingEntity);
                })) {
                    if(!entity.isPassenger()) {
                      //  CollisionEntity collisionEntity = new CollisionEntity(level(), eachVec3.x + 0.5D, eachVec3.y - 0.5D, eachVec3.z + 0.5D, Blocks.AIR.defaultBlockState(), new CompoundTag());
                       // level().addFreshEntity(collisionEntity);

                        if (entity.getY() != eachVec3.y + 0.55D) {
                            //    entity.setPos(entity.getX(), eachVec3.y + 0.55D, entity.getZ());
                        }

                        entity.fallDistance = 0f;

                        Vec3 aimedPos=eachVec3.add(0D,0.5D,0D);
                        Vec3 currentEntityPos=entity.position();
                        Vec3 differ=aimedPos.add(-currentEntityPos.x,-currentEntityPos.y,-currentEntityPos.z);
                         //Vec3 speeds=new Vec3();
                         entity.setDeltaMovement(differ);
                        float dd=getDegreeAngle();
                        Vec3 speeds=Utils.getSpeedsRotation(eachVec3,Vec3.ZERO,dd,duration,getAxis());
                        //entity.setDeltaMovement(speeds);
                        entity.setOnGround(true);

                    }
                }

            }*/
            List< UUID> uuidList=new ArrayList<>();
           for(int i=0;i<rotatedVec3List.size();i++) {
            Vec3 eachVec3=rotatedVec3List.get(i);
            BlockState eachState=blockStateList.get(i);
            boolean floorMakeFlag=false;
            if(!isExpectedState(eachState)) { //床が空気などではない場合
                if (rotatedVec3List.contains(eachVec3.add(0D, 1D, 0D))) {
                    int upperIndex = rotatedVec3List.indexOf(eachVec3.add(0D, 1D, 0D));
                    BlockState upperState = blockStateList.get(upperIndex);
                    if (isExpectedState(upperState)) { //床のひとつ上のブロックが空気など
                        floorMakeFlag=true;
                    }
                }else{
                    floorMakeFlag=true;
                }
            }
            if(floorMakeFlag){
                double s = 0.2D;
                AABB aabb = new AABB(eachVec3.x - 0.5D, eachVec3.y - 0.5D, eachVec3.z - 0.5D, eachVec3.x +0.5D, eachVec3.y +0.5D, eachVec3.z +0.5D);
                AABB aabb2 = new AABB(eachVec3.x - s, eachVec3.y - s, eachVec3.z - s, eachVec3.x + s, eachVec3.y + s, eachVec3.z + s);
               /* for (Entity entity : level().getEntities((Entity) null, aabb2.move(0D,0D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof SeatEntity);
                })) {
                    entity.moveTo(eachVec3);
                }*/
                double bigger=0.125D;
                for (Entity entity : level().getEntities((Entity) null, aabb.move(0D, 0.25D, 0D).inflate(bigger, bigger, bigger), (o) -> {
                    return (o instanceof LivingEntity);
                })) {
                    if (!entity.isPassenger()&&!uuidList.contains(entity.getUUID())) {
                        entity.fallDistance = 0f;
                        Vec3 newPos2 = eachVec3;
                        if (shouldRotate()) {
                            Vec3 vec3 = originPosList.get(i) == getActualPos() ? entity.position() : rotatedOriginPosList.get(i);
                            Vec3 differ = originPosList.get(i) == getActualPos() ? Vec3.ZERO : entity.position().add(-vec3.x, -vec3.y, -vec3.z);
                            Vec3 vec31 = vec3;
                            if (isLoopRotation()||(getTimeCount() <= (duration + startTick))) {
                                vec31 = Utils.getRotatedEntityPosition(vec3, getActualPos(), getDegreeAngle(), duration, getAxis());
                            }
                            Vec3 newPos = vec31.add(differ);
                            double entityPosX = entity.getX();
                            double entityPosZ = entity.getZ();
                            double entitySpeedX = entity.getDeltaMovement().x;
                            double entitySpeedZ = entity.getDeltaMovement().z;
                            double d = 0.000125D;
                            double x = newPos.x;
                            double y = newPos.y;
                            double z = newPos.z;
                            Vec3 vec32 = entity.getDeltaMovement();
                            double d9 = vec32.horizontalDistanceSqr();
                            double d11 = this.getDeltaMovement().horizontalDistanceSqr();
                            if (d9 > 1.0E-4D && d11 < 0.01D) {
                                //if (Mth.abs((float) entitySpeedX) > d) {
                                x = entityPosX;
                                //}
                                //if (Mth.abs((float) entitySpeedZ) > d) {
                                z = entityPosZ;
                                // }
                            }
                            newPos2 = new Vec3(x, y, z);
                        } else {
                            BlockPos transition = getTransition();
                            Vec3 offset = new Vec3((double) transition.getX() / (double) duration, (double) transition.getY() / (double) duration, (double) transition.getZ() / (double) duration);
                            newPos2 = entity.position().add(offset);
                            /*if (duration > 0 && getTimeCount() == startTick + duration + 0) {
                              newPos2=newPos2.add(0,0.05D,0D);
                            }*/
                        }
                        /*if (duration > 0 && getTimeCount() >= startTick && getTimeCount() < startTick + duration) {
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

                        } else if (duration > 0 && getTimeCount() == startTick + duration + 0) {
                            for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                                return !(o instanceof MovingBlockEntity);
                            })) {
                                entity.fallDistance=0f;
                                entity.setDeltaMovement(new Vec3(entity.getDeltaMovement().x,0D,entity.getDeltaMovement().z));
                                entity.setOnGround(true);
                            }
                            if(getTimeCount()>3) {
                                makeBlock();
                            }
                        }*/
                        double collisionYOffset=-1.001D;
                        if(!isLoopRotation()&&(getTimeCount()>(duration+startTick))) {
                            collisionYOffset=-1D;
                            if(getTimeCount()==duration+startTick+1&&shouldRotate()&&getAxis()!= Direction.Axis.Y){
                                newPos2=newPos2.add(0D,0.5D,0D);
                            }
                        }
                        if(!shouldRotate()){
                            collisionYOffset=-1D;
                        }
                        if(isLoopRotation()||(getTimeCount()<=(duration+startTick)+1)) {
                            entity.setPos(newPos2);
                        }

                        entity.setOnGround(true);
                        CollisionEntity collisionEntity = new CollisionEntity(level(), newPos2.x + 0D, newPos2.y +collisionYOffset, newPos2.z + 0D, Blocks.AIR.defaultBlockState(), new CompoundTag());
                        if(!level().isClientSide()) {
                            level().addFreshEntity(collisionEntity);
                        }
                        uuidList.add(entity.getUUID());
                    }
                }
            }

            }

        }

    }
    private boolean isExpectedState(BlockState state){
        if(state.isAir()){
            return true;
        }else if(state.getBlock() instanceof DoorBlock||state.getBlock() instanceof FenceGateBlock){
            return state.getValue(DoorBlock.OPEN);
        }else if(state.getBlock() instanceof SeatBlock){
            return true;
        }else if(!state.isSolid()){
            return true;
        }
        return false;
    }
    public void setTimeCount(int tick){
        entityData.set(DATA_TIME_COUNT_ID,tick);
    }
    public void addTimeCount(int tick){
        entityData.set(DATA_TIME_COUNT_ID,getTimeCount()+tick);
    }

    public int getTimeCount(){
      return   entityData.get(DATA_TIME_COUNT_ID);
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

    public BlockState getState(){
        return entityData.get(BlockDisplayMixin.getData());
    }
    public void setState(BlockState state){
         entityData.set(BlockDisplayMixin.getData(),state);
    }
    public int getStartTick(){
        return entityData.get(DisplayMixin.getDataStartTick());
    }
    private BlockPos getTransition(){
        return entityData.get(DATA_TRANSITION_POSITION_ID);
    }
    private BlockPos getStartLocation(){
        return entityData.get(DATA_START_LOCATION_ID);
    }

    public void setDuration(int duration){
        entityData.set(DisplayMixin.getDataDuration(),duration);
    }
    public int getDuration(){
        return entityData.get(DisplayMixin.getDataDuration());
    }
    public boolean shouldRotate(){
        return entityData.get(DATA_ROTATABLE_ID);
    }
    public boolean shouldRotateState(){
        return entityData.get(DATA_SHOULD_ROTATE_STATE_ID);
    }
    public void setShouldRotateState(boolean flag){
        entityData.set(DATA_SHOULD_ROTATE_STATE_ID,flag);
    }

    public AxisType getAxisType(){
       return AxisType.getType(entityData.get(DATA_AXIS_ID));
    }
    private void setAxisType(AxisType type){
        entityData.set(DATA_AXIS_ID,type.getID());
    }
    public Direction.Axis getAxis(){
       return getAxisType().axis;
    }
    public int getStartRotation(){
        return entityData.get(DATA_STAT_ROTATION_ID);
    }
    public void setStartRotation(int degree){
        entityData.set(DATA_STAT_ROTATION_ID,degree);
    }


    private void setPosRotInterpolationDuration(int p_300107_) {
        this.entityData.set(DATA_POS_ROT_INTERPOLATION_DURATION_ID, p_300107_);
    }

    private int getPosRotInterpolationDuration() {
        return this.entityData.get(DATA_POS_ROT_INTERPOLATION_DURATION_ID);
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
    public boolean isLoopRotation(){
        return entityData.get(DATA_IS_LOOP_ROTATION_ID);
    }
    public void setLoopRotation(boolean isLoop){
        entityData.set(DATA_IS_LOOP_ROTATION_ID,isLoop);
    }

    private List<BlockPos> getPosListFirst(){
        CompoundTag posTag=entityData.get(DATA_POSITION_TAG_ID);
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

    private List<BlockState> getStateListFirst(){
        CompoundTag stateTag=entityData.get(DATA_STATE_TAG_ID);
        List<BlockState> stateList=new ArrayList<>();
        for(int i=0; i<getPosList().size();i++){
            if (stateTag.contains("state_" + String.valueOf(i))) {
                stateList.add(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),stateTag.getCompound("state_" + String.valueOf(i))));
            }
        }
        return stateList;
    }
    public void setStateList(){
        stateList=new ArrayList<>();
    }
    public void setState(int i,BlockState state){
        CompoundTag tag=entityData.get(DATA_STATE_TAG_ID);
        if (tag.contains("state_" + String.valueOf(i))) {
            tag.put("state_" + String.valueOf(i),NbtUtils.writeBlockState(state));
            entityData.set(DATA_STATE_TAG_ID,tag);
            setStateList();
        }
    }

    public List<CompoundTag> getBlockEntityDataList() {
        if(blockEntityDataList ==null|| blockEntityDataList.isEmpty()){
            blockEntityDataList =getBlockEntityDataListFirst();
        }
        return blockEntityDataList;

    }

    private List<CompoundTag> getBlockEntityDataListFirst(){
        CompoundTag blockEntityTag=entityData.get(DATA_BLOCKENTITY_TAG_ID);
        List<CompoundTag> blockEntityList=new ArrayList<>();
        for(int i=0; i<getPosList().size();i++){
            if (blockEntityTag.contains("blockEntity_" + String.valueOf(i))) {
                blockEntityList.add(blockEntityTag.getCompound("blockEntity_" + String.valueOf(i)));
            }
        }
        return blockEntityList;
    }
    private List<Integer> getBasketIndexListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("basketData")){
            entityTag.put("basketData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("basketData");
        if(!basketTag.contains("indexList")){
            basketTag.put("indexList",new CompoundTag());
        }
        CompoundTag indexTag=basketTag.getCompound("indexList");
        List<Integer> indexList=new ArrayList<>();
        for(int i=0; i< indexTag.size();i++){
            if (indexTag.contains("index_" + String.valueOf(i))) {
                indexList.add(indexTag.getInt("index_" + String.valueOf(i)));
            }
        }
        return indexList;
    }

    public List<Integer> getBasketIndexList() {
        if(basketIndexList==null||basketIndexList.isEmpty()){
            basketIndexList=getBasketIndexListFirst();
        }
        return basketIndexList;
    }
    private List<BlockPos> getBasketPosListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("basketData")){
            entityTag.put("basketData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("basketData");
        if(!basketTag.contains("positionList")){
            basketTag.put("positionList",new CompoundTag());
        }
        CompoundTag posTag=basketTag.getCompound("positionList");
        List<BlockPos> posList=new ArrayList<>();
        for(int i=0; i< posTag.size();i++){
            if (posTag.contains("location_" + String.valueOf(i))) {
                posList.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
            }
        }
        return posList;
    }

    public List<BlockPos> getBasketPosList() {
        if(basketPosList==null||basketPosList.isEmpty()){
            basketPosList=getBasketPosListFirst();
        }
        return basketPosList;
    }
    private List<BlockPos> getBasketOriginPosListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("basketData")){
            entityTag.put("basketData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("basketData");
        if(!basketTag.contains("originPositionList")){
            basketTag.put("originPositionList",new CompoundTag());
        }
        CompoundTag posTag=basketTag.getCompound("originPositionList");
        List<BlockPos> posList=new ArrayList<>();
        for(int i=0; i< posTag.size();i++){
            if (posTag.contains("location_" + String.valueOf(i))) {
                posList.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
            }
        }
        return posList;
    }

    public List<BlockPos> getBasketOriginPosList() {
        if(basketOriginPosList==null||basketOriginPosList.isEmpty()){
            basketOriginPosList=getBasketOriginPosListFirst();
        }
        return basketOriginPosList;
    }
    public List<BlockState> getBasketStateList() {
        if(basketStateList==null||basketStateList.isEmpty()){
            basketStateList=getBasketStateListFirst();
        }
        return basketStateList;

    }

    private List<BlockState> getBasketStateListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("basketData")){
            entityTag.put("basketData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("basketData");
        if(!basketTag.contains("stateList")){
            basketTag.put("stateList",new CompoundTag());
        }
        CompoundTag stateTag=basketTag.getCompound("stateList");
        List<BlockState> stateList=new ArrayList<>();
        for(int i=0; i<getBasketIndexList().size();i++){
            if (stateTag.contains("state_" + String.valueOf(i))) {
                stateList.add(NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),stateTag.getCompound("state_" + String.valueOf(i))));
            }
        }
        return stateList;
    }
    public void setBasketStateList(){
        basketStateList=new ArrayList<>();
    }
    public void setBasketState(int i,BlockState state){
        CompoundTag entityTag=getCompoundTag();
        CompoundTag basketTag=entityTag.getCompound("basketData");
        CompoundTag stateTag=basketTag.getCompound("stateList");
        if (stateTag.contains("state_" + String.valueOf(i))) {
            stateTag.put("state_" + String.valueOf(i),NbtUtils.writeBlockState(state));
            basketTag.put("stateList",stateTag);
            entityTag.put("basketData",basketTag);
            setCompoundTag(entityTag);
            setBasketStateList();
        }
    }
    public List<CompoundTag> getBasketBlockEntityDataList() {
        if(basketBlockEntityDataList ==null|| basketBlockEntityDataList.isEmpty()){
            basketBlockEntityDataList =getBasketBlockEntityDataListFirst();
        }
        return basketBlockEntityDataList;

    }

    private List<CompoundTag> getBasketBlockEntityDataListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("basketData")){
            entityTag.put("basketData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("basketData");
        if(!basketTag.contains("blockEntityList")){
            basketTag.put("blockEntityList",new CompoundTag());
        }
        CompoundTag blockEntityTag=basketTag.getCompound("blockEntityList");
        List<CompoundTag> blockEntityList=new ArrayList<>();
        for(int i=0; i<getBasketIndexList().size();i++){
            if (blockEntityTag.contains("blockEntity_" + String.valueOf(i))) {
                blockEntityList.add(blockEntityTag.getCompound("blockEntity_" + String.valueOf(i)));
            }
        }
        return blockEntityList;
    }
    private List<BlockPos> getSeatPosListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("seatData")){
            entityTag.put("seatData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("seatData");
        if(!basketTag.contains("positionList")){
            basketTag.put("positionList",new CompoundTag());
        }
        CompoundTag posTag=basketTag.getCompound("positionList");
        List<BlockPos> posList=new ArrayList<>();
        for(int i=0; i< posTag.size();i++){
            if (posTag.contains("location_" + String.valueOf(i))) {
                posList.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
            }
        }
        return posList;
    }

    public List<BlockPos> getSeatPosList() {
        if(seatPosList==null||seatPosList.isEmpty()){
            seatPosList=getSeatPosListFirst();
        }
        return seatPosList;
    }
    private List<BlockPos> getSeatOriginPosListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("seatData")){
            entityTag.put("seatData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("seatData");
        if(!basketTag.contains("originPositionList")){
            basketTag.put("originPositionList",new CompoundTag());
        }
        CompoundTag posTag=basketTag.getCompound("originPositionList");
        List<BlockPos> posList=new ArrayList<>();
        for(int i=0; i< posTag.size();i++){
            if (posTag.contains("location_" + String.valueOf(i))) {
                posList.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
            }
        }
        return posList;
    }

    public List<BlockPos> getSeatOriginPosList() {
        if(seatOriginPosList==null||seatOriginPosList.isEmpty()){
            seatOriginPosList=getSeatOriginPosListFirst();
        }
        return seatOriginPosList;
    }
    private List<Boolean> getSeatIsInBasketListFirst(){
        CompoundTag entityTag=getCompoundTag();
        if(!entityTag.contains("seatData")){
            entityTag.put("seatData",new CompoundTag());
        }
        CompoundTag basketTag=entityTag.getCompound("seatData");
        if(!basketTag.contains("isInBasketList")){
            basketTag.put("isInBasketList",new CompoundTag());
        }
        CompoundTag tag=basketTag.getCompound("isInBasketList");
        List<Boolean> list=new ArrayList<>();
        for(int i=0; i< tag.size();i++){
            if (tag.contains("isInBasket_" + String.valueOf(i))) {
                list.add(tag.getBoolean("isInBasket_" + String.valueOf(i)));
            }
        }
        return list;
    }

    public List<Boolean> getSeatIsInBasketList() {
        if(seatIsInBasketList==null||seatIsInBasketList.isEmpty()){
            seatIsInBasketList=getSeatIsInBasketListFirst();
        }
        return seatIsInBasketList;
    }
    public int getVisualXRot(){
        return getAxis()== Direction.Axis.X? entityData.get(DATA_VISUAL_ROTATION_ID) : 0;
    }
    public int getVisualYRot(){

        return getAxis()== Direction.Axis.Y? entityData.get(DATA_VISUAL_ROTATION_ID) : 0;
    }
    public int getVisualZRot(){
        return getAxis()== Direction.Axis.Z? entityData.get(DATA_VISUAL_ROTATION_ID) : 0;
    }
    public int getVisualRot(){
        /**最初から見た目のみ回転させて配置することがある*/
        return entityData.get(DATA_VISUAL_ROTATION_ID);
    }
    public void setVisualRot(int degree){
           entityData.set(DATA_VISUAL_ROTATION_ID, degree);
    }

    public int getDiscardTime(){
        return entityData.get(DATA_DISCARD_TIME_ID);
    }
    public void setDiscardTime(int tick){
        entityData.set(DATA_DISCARD_TIME_ID,tick);
    }


    @Override
    public void lerpTo(double p_297677_, double p_301293_, double p_301384_, float p_300635_, float p_299108_, int p_299659_,boolean b) {
        int i = this.getPosRotInterpolationDuration();
        this.lerpX = p_297677_;
        this.lerpY = p_301293_;
        this.lerpZ = p_301384_;
        this.lerpYRot = (double)p_300635_;
        this.lerpXRot = (double)p_299108_;
        this.lerpSteps = 10;
        this.posRotInterpolationTarget = new MovingBlockEntity.PosRotInterpolationTarget(i, p_297677_, p_301293_, p_301384_, (double)p_300635_, (double)p_299108_);
    }


    private  void rotate(){
        setInvisible(false);
        int transitionDegree=getDegreeAngle();
        if(isLoopRotation()||getTimeCount()<=getDuration()/*+1*/){
           float angle= -(float) getDegreeAngle() /getDuration();
                setPosRotInterpolationDuration(getDuration()-20);
                    setYRot(getYRot() + angle);
        }
        if(!isLoopRotation()) {
            if (getTimeCount() == getDuration() + 1) {
                rotateAndMakeBlock(transitionDegree);
            } else if (getTimeCount() == getDuration() + 2) {
                discardAfterRotate( transitionDegree);
            }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset();
    }

    @Override
    protected void positionRider(Entity entity, MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        int passengerIndex = this.getPassengers().indexOf(entity);
        if(passengerIndex>=0) {
            if(!getSeatPosList().isEmpty()&&!getSeatOriginPosList().isEmpty()) {
                if (getSeatPosList().size() > passengerIndex&&getSeatOriginPosList().size()>passengerIndex) {
                    BlockPos seatOldPos = getSeatPosList().get(passengerIndex);
                    BlockPos seatOriginPos = getSeatOriginPosList().get(passengerIndex);
                    int startTick = getStartTick();
                    int duration = getDuration();
                    boolean flag = getTimeCount() >= startTick && getTimeCount() < startTick + duration;
                    if(shouldRotate()) {
                        float thetaDegree = (flag || isLoopRotation()) ? (getTimeCount() - startTick) * getDegreeAngle() / (float) duration : getDegreeAngle();
                        float degreeCombined = thetaDegree /*+ getVisualRot()*/ + getStartRotation();
                        if(!getSeatIsInBasketList().get(passengerIndex)){
                            Vec3 vec3 =  entity.position();
                            Vec3 differ = Vec3.ZERO;
                            Vec3 vec31=vec3;
                            if(isLoopRotation()||(getTimeCount()<(duration+startTick))) {
                                vec31 = Utils.getRotatedEntityPosition(vec3, getActualPos(), getDegreeAngle(), duration, getAxis());
                            }
                            Vec3 newPos = vec31.add(differ);
                            //moveFunction.accept(entity, newPos.x, newPos.y - 0.5D, newPos.z);
                           // moveFunction.accept(entity, position().x, position().y+8D, position().z);
                            Vec3 rotatedVec3 = Utils.rotateVec3SeatPos(seatOldPos, BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombined, BlockPos.ZERO,false);
                            moveFunction.accept(entity, rotatedVec3.x, rotatedVec3.y - 0.5D, rotatedVec3.z);

                        }else {
                            Vec3 rotatedVec3 = Utils.rotateVec3SeatPos(seatOldPos, BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombined, seatOriginPos,true);
                            moveFunction.accept(entity, rotatedVec3.x, rotatedVec3.y - 0.5D, rotatedVec3.z);

                        }
                    }else{
                        Vec3 vec3=seatOldPos.getCenter().add(this.getActualPos());
                        moveFunction.accept(entity, vec3.x-0.5D, vec3.y-1D, vec3.z-0.5D);
                    }
               }
            }
        }
    }
    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
           --this.lerpSteps;
            this.setPos(d0, d1, d2);

        }
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
    protected boolean canAddPassenger(Entity p_248594_) {
        return true;
    }

   /* @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand hand) {
        int duration=getDuration();
        if(duration>0) {
            int startTick = getStartTick();
            boolean flag = getTimeCount() >= startTick && getTimeCount() < startTick + duration;
            float thetaDegreeF = (flag || isLoopRotation()) ? (getTimeCount() - startTick) * getDegreeAngle() / (float) duration : getDegreeAngle();
            float degreeCombinedF = thetaDegreeF + getVisualRot() + getStartRotation();
            List<Vec3> rotatedVec3BasketList = Utils.rotateVec3BasketPosList(getBasketPosList(), BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombinedF, getBasketOriginPosList());
            List<Vec3> rotatedVec3List = Utils.rotateVec3PosList(getPosList(), BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombinedF);
            //rotatedVec3List.addAll(rotatedVec3BasketList);
            List<BlockState> blockStateList = getStateList();
            BlockPos pos = new BlockPos(Mth.floor(vec3.x), Mth.floor(vec3.y), Mth.floor(vec3.z));
            for (int i = 0; i < rotatedVec3List.size(); i++) {
                Vec3 eachVec3 = rotatedVec3List.get(i);
                BlockState eachState = blockStateList.get(i);
                double s = 0.2D;
                BlockPos eachPos = new BlockPos(Mth.floor(eachVec3.x), Mth.floor(eachVec3.y), Mth.floor(eachVec3.z));
                if (eachPos.equals(pos)) {
                    setState(i, Blocks.DIAMOND_ORE.defaultBlockState());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }*/

    @Override
    public boolean mayInteract(Level p_146843_, BlockPos p_146844_) {
        //setState(1, Blocks.DIAMOND_ORE.defaultBlockState());

        return false;
    }

   /* @Override
    public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        setState(1, Blocks.DIAMOND_ORE.defaultBlockState());
        return InteractionResult.SUCCESS;
    }*/
    /* @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        int duration=getDuration();
        if(duration>0) {
            int startTick = getStartTick();
            boolean flag = getTimeCount() >= startTick && getTimeCount() < startTick + duration;
            float thetaDegreeF = (flag || isLoopRotation()) ? (getTimeCount() - startTick) * getDegreeAngle() / (float) duration : getDegreeAngle();
            float degreeCombinedF = thetaDegreeF + getVisualRot() + getStartRotation();
            List<Vec3> rotatedVec3BasketList = Utils.rotateVec3BasketPosList(getBasketPosList(), BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombinedF, getBasketOriginPosList());
            List<Vec3> rotatedVec3List = Utils.rotateVec3PosList(getPosList(), BlockPos.ZERO, getActualBlockPos(), getAxis(), degreeCombinedF);
            //rotatedVec3List.addAll(rotatedVec3BasketList);
            List<BlockState> blockStateList = getStateList();
            //blockStateList.addAll(getBasketStateList());
            *//*for(int i=0;i<rotatedVec3ListFloat.size();i++) {
                Vec3 eachVec3 = rotatedVec3ListFloat.get(i);
                double s = 0.4D;
                AABB aabb2 = new AABB(eachVec3.x - s, eachVec3.y - s, eachVec3.z - s, eachVec3.x +s, eachVec3.y +s, eachVec3.z +s);
                for (Entity entity : level().getEntities((Entity) null, aabb2.move(0D,0D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof SeatEntity);
                })) {
                    entity.moveTo(eachVec3);
                }
            }*//*
            *//*for(int i=0;i<getPosList().size();i++) {
                Vec3 eachVec3= getPosList().get(i).getCenter();
                //s=0d;
                AABB aabb = new AABB(eachVec3.x - 0.5D, eachVec3.y - 0.5D, eachVec3.z - 0.5D, eachVec3.x - 0.5D+ 1D, eachVec3.y - 0.5D + 1D, eachVec3.z - 0.5D  + 1D);
                for (Entity entity : level().getEntities((Entity) null, aabb.move(0D,1D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof LivingEntity);
                })) {
                    if(!entity.isPassenger()) {
                        CollisionEntity collisionEntity = new CollisionEntity(level(), eachVec3.x + 0.5D, eachVec3.y - 0.5D, eachVec3.z + 0.5D, Blocks.AIR.defaultBlockState(), new CompoundTag());
                        level().addFreshEntity(collisionEntity);

                        if (entity.getY() != eachVec3.y + 0.55D) {
                            //    entity.setPos(entity.getX(), eachVec3.y + 0.55D, entity.getZ());
                        }

                        entity.fallDistance = 0f;

                        //Vec3 aimedPos=eachVec3.add(0D,0.5D,0D);
                        //Vec3 currentEntityPos=entity.position();
                        //Vec3 differ=aimedPos.add(-currentEntityPos.x,-currentEntityPos.y,-currentEntityPos.z);
                        // Vec3 speeds=new Vec3();
                        // entity.setDeltaMovement(differ);
                        float dd=getDegreeAngle();
                        Vec3 speeds=Utils.getSpeedsRotation(eachVec3,Vec3.ZERO,dd,duration,getAxis());
                        entity.setDeltaMovement(speeds);
                        entity.setOnGround(true);
                    }
                }

            }*//*
            for (int i = 0; i < rotatedVec3List.size(); i++) {
                Vec3 eachVec3 = rotatedVec3List.get(i);
                BlockState eachState = blockStateList.get(i);
                double s = 0.2D;
                BlockPos eachPos = new BlockPos(Mth.floor(eachVec3.x), Mth.floor(eachVec3.y), Mth.floor(eachVec3.z));
                if (eachPos == pos) {
                    setState(i, Blocks.DIAMOND_ORE.defaultBlockState());
                    return true;
                }
              *//*  //s=0d;
                AABB aabb = new AABB(eachVec3.x - 0.5D, eachVec3.y - 0.5D, eachVec3.z - 0.5D, eachVec3.x - 0.5D+ 1D, eachVec3.y - 0.5D + 1D, eachVec3.z - 0.5D  + 1D);
           *//**//* AABB aabb2 = new AABB(eachVec3.x - s, eachVec3.y - s, eachVec3.z - s, eachVec3.x +s, eachVec3.y +s, eachVec3.z +s);
                for (Entity entity : level().getEntities((Entity) null, aabb2.move(0D,0D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof SeatEntity);
                })) {
                    entity.moveTo(eachVec3);
                }*//**//*
                for (Entity entity : level().getEntities((Entity) null, aabb.move(0D,1D,0D).inflate(0d, 0d, 0d), (o) -> {
                    return (o instanceof LivingEntity);
                })) {
                    if(!entity.isPassenger()) {
                        CollisionEntity collisionEntity = new CollisionEntity(level(), eachVec3.x + 0.5D, eachVec3.y - 0.5D, eachVec3.z + 0.5D, Blocks.AIR.defaultBlockState(), new CompoundTag());
                        level().addFreshEntity(collisionEntity);

                        if (entity.getY() != eachVec3.y + 0.55D) {
                            entity.setPos(entity.getX(), eachVec3.y + 0.55D, entity.getZ());
                        }

                        entity.fallDistance = 0f;

                        //Vec3 aimedPos=eachVec3.add(0D,0.5D,0D);
                        //Vec3 currentEntityPos=entity.position();
                        //Vec3 differ=aimedPos.add(-currentEntityPos.x,-currentEntityPos.y,-currentEntityPos.z);
                        // Vec3 speeds=new Vec3();
                        // entity.setDeltaMovement(differ);
                        entity.setOnGround(true);
                    }
                }

            }
*//*
            }
        }
            return false;
    }*/

    public  enum AxisType {
        NONE((byte) 0, Direction.Axis.Y),
        X((byte) 1, Direction.Axis.X),
        Y((byte) 2, Direction.Axis.Y),

        Z((byte) 3, Direction.Axis.Z);

        private byte id;
        public Direction.Axis axis;

        private AxisType(byte id, Direction.Axis axis) {
            this.id = id;
            this.axis=axis;
        }
        public static AxisType getType(byte id){
            return switch (id) {
                case 0 -> NONE;
                case 1 -> X;
                case 2 -> Y;
                case 3 -> Z;
                default -> NONE;
            };

        }
        public byte getID()
        {
            return this.id;
        }
        public static AxisType getType(Direction.Axis axis){
            return switch (axis) {
                case X -> AxisType.X;
                case Y -> AxisType.Y;
                case Z -> AxisType.Z;
            };
        }
    }

    static class PosRotInterpolationTarget {
        int steps;
        final double targetX;
        final double targetY;
        final double targetZ;
        final double targetYRot;
        final double targetXRot;

        PosRotInterpolationTarget(int p_297638_, double p_297433_, double p_297414_, double p_300814_, double p_297927_, double p_297784_) {
            this.steps = p_297638_;
            this.targetX = p_297433_;
            this.targetY = p_297414_;
            this.targetZ = p_300814_;
            this.targetYRot = p_297927_;
            this.targetXRot = p_297784_;
        }
         void setRot(Entity entity,float p_19916_, float p_19917_) {
            entity.setYRot(p_19916_ % 360.0F);
            entity.setXRot(p_19917_ % 360.0F);
        }
        void applyTargetPosAndRot(Entity p_297540_) {
            p_297540_.setPos(this.targetX, this.targetY, this.targetZ);
           setRot(p_297540_,(float)this.targetYRot, (float)this.targetXRot);
        }

        void applyLerpStep(Entity p_300902_) {
            lerpPositionAndRotationStep(p_300902_,this.steps, this.targetX, this.targetY, this.targetZ, this.targetYRot, this.targetXRot);
        }
        protected void lerpPositionAndRotationStep(Entity entity,int p_298722_, double p_297490_, double p_300716_, double p_298684_, double p_300659_, double p_298926_) {
            double d0 = 1.0D / (double)p_298722_;
            double d1 = Mth.lerp(d0, entity.getX(), p_297490_);
            double d2 = Mth.lerp(d0, entity.getY(), p_300716_);
            double d3 = Mth.lerp(d0, entity.getZ(), p_298684_);
            float f = (float)Mth.rotLerp((float) d0, (float) entity.getYRot(), (float) p_300659_);
            float f1 = (float)Mth.lerp(d0, (double)entity.getXRot(), p_298926_);
            entity.setPos(d1, d2, d3);
            setRot(entity,f, f1);
        }
    }
}

