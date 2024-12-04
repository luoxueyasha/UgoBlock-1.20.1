package com.iwaliner.ugoblock.object;


import com.iwaliner.ugoblock.mixin.BlockDisplayMixin;
import com.iwaliner.ugoblock.mixin.DisplayMixin;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoveableBlockEntity extends Display.BlockDisplay {
    /**移動量を座標で指定。変位なので始点座標でも終点座標でもない。*/
    public static final EntityDataAccessor<BlockPos> DATA_TRANSITION_LOCATION_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   /**始点座標*/
    public static final EntityDataAccessor<BlockPos> DATA_START_LOCATION_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   /**動いているブロックがブロックエンティティを所持していた場合、ここにブロックエンティティのデータが格納される。所持していなかった場合は空のNBTタグが格納される。*/
    public static final EntityDataAccessor<CompoundTag> DATA_BLOCKENTITY_CONTENTS_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

    public MoveableBlockEntity(EntityType<?> p_271022_, Level p_270442_) {
        super(Register.MoveableBlock.get(), p_270442_);
        this.noPhysics = false;
        this.noCulling = false;
    }
    public MoveableBlockEntity(Level level,BlockPos startPos, BlockState state, int startTick, int duration, BlockPos endPos, BlockEntity blockEntity) {
        super(Register.MoveableBlock.get(), level);
        this.setPos(startPos.getX(),startPos.getY(),startPos.getZ());
        this.entityData.set(BlockDisplayMixin.getData(),state);
        this.entityData.set(DATA_TRANSITION_LOCATION_ID,endPos);
        this.entityData.set(DATA_START_LOCATION_ID,startPos);
        this.entityData.set(DisplayMixin.getDataStartTick(),startTick);
        this.entityData.set(DisplayMixin.getDataDuration(),duration);
        if(blockEntity!=null) {
            this.entityData.set(DATA_BLOCKENTITY_CONTENTS_ID,blockEntity.saveWithoutMetadata());
        }
        this.noPhysics = false;
        this.noCulling = false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRANSITION_LOCATION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_START_LOCATION_ID, BlockPos.ZERO);
        this.entityData.define(DATA_BLOCKENTITY_CONTENTS_ID, new CompoundTag());
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
            this.entityData.set(DATA_TRANSITION_LOCATION_ID, NbtUtils.readBlockPos(tag.getCompound("transition")));
        }
        if (tag.contains("start_location")) {
            this.entityData.set(DATA_START_LOCATION_ID, NbtUtils.readBlockPos(tag.getCompound("start_location")));
        }
      if (tag.contains("BlockEntityTag")) {
            this.entityData.set(DATA_BLOCKENTITY_CONTENTS_ID,tag.getCompound("BlockEntityTag"));
        }
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("transition",NbtUtils.writeBlockPos(entityData.get(DATA_TRANSITION_LOCATION_ID)));
        tag.put("start_location",NbtUtils.writeBlockPos(entityData.get(DATA_START_LOCATION_ID)));
         if(!entityData.get(DATA_BLOCKENTITY_CONTENTS_ID).isEmpty()) {
            tag.put("BlockEntityTag",entityData.get(DATA_BLOCKENTITY_CONTENTS_ID));
        }

    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return true;
    }
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.makeBoundingBox();
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        super.makeBoundingBox();
     BlockState state=entityData.get(BlockDisplayMixin.getData());
     VoxelShape shape=state.getCollisionShape(level(),this.blockPosition(), CollisionContext.of(this));
     AABB aabb=new AABB(position().x+shape.min(Direction.Axis.X),position().y+shape.min(Direction.Axis.Y),position().z+shape.min(Direction.Axis.Z),position().x+shape.max(Direction.Axis.X),position().y+shape.max(Direction.Axis.Y),position().z+shape.max(Direction.Axis.Z));
        return aabb;

    }
    @Override
    public void tick() {
        if(getState().isAir()){
            discard();
        }
        super.tick();
        setBoundingBox(makeBoundingBox());
        BlockPos transition= getTransition();
        int duration=getDuration();
        int startTick=getStartTick();

            if (duration > 0 && tickCount >= startTick && tickCount < startTick + duration) {
                Vec3 pos = new Vec3((double) position().x + (double) transition.getX() / (double) duration, (double) position().y + (double) transition.getY() / (double) duration, (double) position().z + (double) transition.getZ() / (double) duration);
                setPos(pos);
                for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return !(o instanceof MoveableBlockEntity);
                })) {
                       if (entity instanceof Player) { /**移動中のブロックに乗っているプレイヤーが動けなくなるのはまずいので、他エンティティと処理を分けてる*/
                            double endY=0D;
                            if (transition.getY() > 0D) {
                                endY=0.3D;
                            } else if (transition.getY() < 0D) {
                            }
                            List<MoveableBlockEntity> colliedEntityList=level().getEntitiesOfClass(MoveableBlockEntity.class,entity.getBoundingBox().inflate(0D,0.1D,0D));
                            int collidedAmount=colliedEntityList.size();
                            if(collidedAmount!=0) {
                                Vec3 entityPos = new Vec3((double) entity.position().x + ((double) transition.getX() / (double) duration) / (double) collidedAmount, this.position().y+1D, (double) entity.position().z + ((double) transition.getZ() / (double) duration) / (double) collidedAmount);
                                entity.setPos(entityPos);
                            }
                            if(tickCount==startTick+duration-1){
                                entity.setPos(entity.position().add(0D,endY,0D));
                            }
                        } else { /**プレイヤーは上のテレポートによる移動でかくつかなかったが、他のエンティティはこれではかくついてしまうので、エンティティに速度を持たせて移動させてる。ただし身動きがとれなくなる。*/
                               Vec3 entityPos = new Vec3((double) entity.position().x, this.position().y+1D, entity.position().z);
                                    entity.setPos(entityPos);
                                Vec3 speed = new Vec3((double) transition.getX() / (double) duration, 0D, (double) transition.getZ() / (double) duration);
                                entity.setDeltaMovement(speed);
                                entity.setOnGround(true);
                        }
                }
            } else if (duration > 0 && tickCount == startTick + duration +0) {
                makeBlock();
            }else if(duration > 0 && tickCount == startTick + duration + 1){
                discard();
            }
    }
    public boolean shouldFixFighting(){ /**このエンティティとブロックが完全に重なりZ-fightingを起こす可能性があるかどうか*/
        return (getDuration()>0&&tickCount>getStartTick()+getDuration()-0)||tickCount<2;
    }
    private void makeBlock(){ /**移動し終わってブロック化する*/
        if(!level().isClientSide) {
            BlockPos pos = new BlockPos(getStartLocation().getX() + getTransition().getX(), getStartLocation().getY() + getTransition().getY(), getStartLocation().getZ() + getTransition().getZ());
            BlockState movingState = getState();
            if (level().getBlockState(pos).canBeReplaced()) {
                level().setBlock(pos, movingState,82);
                if (!getBlockEntityData().isEmpty() && movingState.hasBlockEntity()) {
                    CompoundTag compoundtag = getBlockEntityData();
                    if (compoundtag != null) {
                        BlockEntity blockentity = level().getBlockEntity(pos);

                        if (blockentity != null) {
                            blockentity.load(getBlockEntityData());
                        }
                    }
                }
            } else { /**移動してきた場所が他のブロックで埋まっていた場合。アイテム化する。*/
                if (!level().isClientSide&&!movingState.is(Register.TAG_DISABLE_ITEM_DROP)) { /**通常*/
                    ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(movingState.getBlock()));
                    level().addFreshEntity(itemEntity);
                }else if(!level().getBlockState(pos).is(Register.TAG_DISABLE_ITEM_DROP)){ /**アイテムをドロップしたくないブロックが移動してきたがその場所が埋まっていた場合。もともとあったブロックをアイテム化したうえでドロップしたくないブロックを設置する。*/
                    ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(level().getBlockState(pos).getBlock()));
                    level().addFreshEntity(itemEntity);
                    level().setBlock(pos,movingState,82);
                }
                discard();
            }
        }
    }


    private BlockState getState(){
        return entityData.get(BlockDisplayMixin.getData());
    }
    private int getDuration(){
        return entityData.get(DisplayMixin.getDataDuration());
    }
    private int getStartTick(){
        return entityData.get(DisplayMixin.getDataStartTick());
    }
    private BlockPos getTransition(){
        return entityData.get(DATA_TRANSITION_LOCATION_ID);
    }
    private BlockPos getStartLocation(){
        return entityData.get(DATA_START_LOCATION_ID);
    }
    private CompoundTag getBlockEntityData(){
        return entityData.get(DATA_BLOCKENTITY_CONTENTS_ID);
    }
}
