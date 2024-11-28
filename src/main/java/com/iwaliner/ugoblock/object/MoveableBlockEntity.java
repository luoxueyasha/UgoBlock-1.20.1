package com.iwaliner.ugoblock.object;


import com.iwaliner.ugoblock.mixin.BlockDisplayMixin;
import com.iwaliner.ugoblock.mixin.DisplayMixin;
import com.iwaliner.ugoblock.register.EntityRegister;
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

public class MoveableBlockEntity extends Display.BlockDisplay /*implements MenuProvider*/ {
    /**移動量を座標で指定。変位なので始点座標でも終点座標でもない。*/
    public static final EntityDataAccessor<BlockPos> DATA_TRANSITION_LOCATION_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   /**始点座標*/
    public static final EntityDataAccessor<BlockPos> DATA_START_LOCATION_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.BLOCK_POS);
   /**動いているブロックがブロックエンティティを所持していた場合、ここにブロックエンティティのデータが格納される。所持していなかった場合は空のNBTタグが格納される。*/
    public static final EntityDataAccessor<CompoundTag> DATA_BLOCKENTITY_CONTENTS_ID = SynchedEntityData.defineId(MoveableBlockEntity.class, EntityDataSerializers.COMPOUND_TAG);

    public MoveableBlockEntity(EntityType<?> p_271022_, Level p_270442_) {
        super(EntityRegister.MoveableBlock.get(), p_270442_);

    }
    public MoveableBlockEntity(Level level,BlockPos startPos, BlockState state, int startTick, int duration, BlockPos endPos, BlockEntity blockEntity) {
        super(EntityRegister.MoveableBlock.get(), level);
        this.setPos(startPos.getX(),startPos.getY(),startPos.getZ());
        this.entityData.set(BlockDisplayMixin.getData(),state);
        this.entityData.set(DATA_TRANSITION_LOCATION_ID,endPos);
        this.entityData.set(DATA_START_LOCATION_ID,startPos);
        this.entityData.set(DisplayMixin.getDataStartTick(),startTick);
        this.entityData.set(DisplayMixin.getDataDuration(),duration);
        if(blockEntity!=null) {
            this.entityData.set(DATA_BLOCKENTITY_CONTENTS_ID,blockEntity.saveWithoutMetadata());
        }
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
                    if (entity.getType() != EntityRegister.MoveableBlock.get()) {
                        if (entity instanceof Player) {
                            double y = 0D;
                            if (transition.getY() > 0D) {
                                y = 0.3D;
                            } else if (transition.getY() < 0D) {
                                y = -0.3D;
                            }
                            Vec3 entityPos = new Vec3((double) entity.position().x + (double) transition.getX() / (double) duration, (double) entity.position().y +y + (double) transition.getY() / (double) duration, (double) entity.position().z + (double) transition.getZ() / (double) duration);
                            entity.setPos(entityPos);
                           // entity.addDeltaMovement(new Vec3(0D,y,0D));
                        } else {
                            double y = 0D;
                            if (transition.getY() > 0D) {
                                y = 0.08D;
                            } else if (transition.getY() < 0D) {
                                y = -0.08D;
                            }
                            if (tickCount == startTick + duration - 1) {
                                entity.setDeltaMovement(Vec3.ZERO);
                            } else {
                                if (tickCount == startTick && y != 0) {
                                    entity.setPos(entity.position().add(0D, 0.0D/*+(double) y * 0.8D*/, 0D));
                                }
                                Vec3 speed = new Vec3((double) transition.getX() / (double) duration, 1.2D*(((double) transition.getY()) / (double) duration), (double) transition.getZ() / (double) duration);
                                entity.setDeltaMovement(speed);

                            }
                        }
                    }
                }
            } else if (duration > 0 && tickCount >= startTick + duration + 1) {
                int y = 0;
                if (transition.getY() > 0D) {
                    y = 1;
                } else if (transition.getY() < 0D) {
                    y = -1;
                }

                for (Entity entity : level().getEntities((Entity) null, getBoundingBoxForCulling().move(0.5D, 0.5D, 0.5D).inflate(0d, 0.5d, 0d), (o) -> {
                    return !(o instanceof MoveableBlockEntity);
                })) {
                 //   entity.setPos(entity.position().add(0D, (double) y, 0D));
                }
                makeBlock();
            }


    }
    private void makeBlock(){
        if(!level().isClientSide) {
            BlockPos pos = new BlockPos(getStartLocation().getX() + getTransition().getX(), getStartLocation().getY() + getTransition().getY(), getStartLocation().getZ() + getTransition().getZ());
            if (level().getBlockState(pos).canBeReplaced()) {
                BlockState movingState = getState();

                level().setBlockAndUpdate(pos, movingState);
                if (!getBlockEntityData().isEmpty() && movingState.hasBlockEntity()) {
                    CompoundTag compoundtag = getBlockEntityData();
                    if (compoundtag != null) {
                        BlockEntity blockentity = level().getBlockEntity(pos);

                        if (blockentity != null) {
                            blockentity.load(getBlockEntityData());

                        }
                    }
                }
                discard();
            } else {
                ItemEntity itemEntity = new ItemEntity(level(), position().x, position().y, position().z, new ItemStack(entityData.get(BlockDisplayMixin.getData()).getBlock()));
                if (!level().isClientSide) {
                    level().addFreshEntity(itemEntity);
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
   /* @Nullable
    public Display.BlockDisplay.BlockRenderState blockRenderState() {
        if(getState().getBlock() instanceof AbstractChestBlock) {
            return null;
        }
        return this.blockRenderState();
    }*/


   /* @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        BlockState state=this.entityData.get(BlockDisplayMixin.getData());
        if(state.getBlock() instanceof BarrelBlock) {
            this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            player.openMenu(this);
            level().addParticle(ParticleTypes.FLAME, position().x, position().y+2D, position().z, 0.0D, 0.0D, 0.0D);

            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }*/


   /* @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        BlockState state=this.entityData.get(BlockDisplayMixin.getData());
        if(state.hasBlockEntity()&&state.getBlock() instanceof BaseEntityBlock baseEntityBlock) {
            BlockEntity blockEntity = baseEntityBlock.newBlockEntity(blockPosition(),state);
            if(blockEntity instanceof BaseContainerBlockEntity baseContainerBlockEntity) {
                return ChestMenu.threeRows(i, inventory, baseContainerBlockEntity);
            }
        }
        return null;
    }*/
}
