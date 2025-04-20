package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.object.basket_maker.BasketMakerBlock;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerBlockEntity;
import com.iwaliner.ugoblock.object.controller.*;
import com.iwaliner.ugoblock.object.moving_block.CollisionEntity;
import com.iwaliner.ugoblock.object.moving_block.DoorEntity;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.object.seat.SeatBlock;
import com.iwaliner.ugoblock.object.seat.SeatEntity;
import com.iwaliner.ugoblock.object.seat.StandingSeatEntity;
import com.iwaliner.ugoblock.register.Register;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public class Utils {
    public static int getMaxSize(){
        return 4801;
    }
    public static BlockPos errorPos(){
        return new BlockPos(0,-999999999,0);
    }
    public static List<BlockPos> getPositionList(CompoundTag tag){

        List<BlockPos> list=new ArrayList<>();
        if(tag!=null) {
            if(!tag.contains("positionList")){
                tag.put("positionList",new CompoundTag());
            }
            CompoundTag posTag=tag.getCompound("positionList");
            for (int i = 0; i < posTag.size(); i++) {
                if (posTag.contains("location_" + String.valueOf(i))) {
                    list.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
                }
            }
        }
        return list;
    }
    public static void setPositionList(ItemStack stack,List<BlockPos> list){
        CompoundTag tag=stack.getTag();
        if(tag!=null) {
            if(!tag.contains("positionList")){
                tag.put("positionList",new CompoundTag());
            }
            CompoundTag posTag=tag.getCompound("positionList");
            for (int i = 0; i < list.size(); i++) {
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(list.get(i)));
            }
        }
    }
    public static boolean isBlockSolid(BlockState state){
        BakedModel bakedmodel =  Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42),net.minecraftforge.client.model.data.ModelData.EMPTY)) {
            return rt==RenderType.solid();
        }
       return false;
    }
   public static void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_,boolean renderDown,boolean renderUp,boolean renderNorth,boolean renderSouth,boolean renderWest,boolean renderEast) {
        renderSingleBlock(p_110913_, p_110914_, p_110915_, p_110916_, OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.ModelData.EMPTY, null,renderDown,renderUp,renderNorth,renderSouth,renderWest,renderEast);
    }
    public static void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType,boolean renderDown,boolean renderUp,boolean renderNorth,boolean renderSouth,boolean renderWest,boolean renderEast) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL:
                    BakedModel bakedmodel =  Minecraft.getInstance().getBlockRenderer().getBlockModel(p_110913_);
                    int i = Minecraft.getInstance().getBlockColors().getColor(p_110913_, (BlockAndTintGetter)null, (BlockPos)null, 0);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    for (net.minecraft.client.renderer.RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt,renderDown,renderUp,renderNorth,renderSouth,renderWest,renderEast);
                    break;
                case ENTITYBLOCK_ANIMATED:
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
            }

        }
    }
    public static void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_, net.minecraftforge.client.model.data.ModelData modelData, net.minecraft.client.renderer.RenderType renderType,boolean renderDown,boolean renderUp,boolean renderNorth,boolean renderSouth,boolean renderWest,boolean renderEast) {
        RandomSource randomsource = RandomSource.create();
        if(renderDown){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.DOWN, randomsource, modelData, renderType), p_111075_, p_111076_);
        }
        if(renderUp){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.UP, randomsource, modelData, renderType), p_111075_, p_111076_);
        }
        if(renderNorth){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.NORTH, randomsource, modelData, renderType), p_111075_, p_111076_);
        }
        if(renderSouth){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.SOUTH, randomsource, modelData, renderType), p_111075_, p_111076_);
        }
        if(renderWest){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.WEST, randomsource, modelData, renderType), p_111075_, p_111076_);
        }
        if(renderEast){
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, Direction.EAST, randomsource, modelData, renderType), p_111075_, p_111076_);
        }

        randomsource.setSeed(42L);
        renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, (Direction)null, randomsource, modelData, renderType), p_111075_, p_111076_);
    }
    private static void renderQuadList(PoseStack.Pose p_111059_, VertexConsumer p_111060_, float p_111061_, float p_111062_, float p_111063_, List<BakedQuad> p_111064_, int p_111065_, int p_111066_) {
        for(BakedQuad bakedquad : p_111064_) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = Mth.clamp(p_111061_, 0.0F, 1.0F);
                f1 = Mth.clamp(p_111062_, 0.0F, 1.0F);
                f2 = Mth.clamp(p_111063_, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            p_111060_.putBulkData(p_111059_, bakedquad, f, f1, f2, p_111065_, p_111066_);
        }

    }
    public static Vec3 getRotatedEntityPosition(Vec3 currentPos,Vec3 originPos, float degree,int duration, Direction.Axis axis){
        Vec3 eachOffsetPos=currentPos.add(-originPos.x,-originPos.y,-originPos.z);
        Vector3f transitionRotated;
        Vector3f transition = new Vector3f((float) eachOffsetPos.x, (float) eachOffsetPos.y, (float) eachOffsetPos.z);
        float degreeAngle=degree/(float) duration;
        if(axis== Direction.Axis.X){
            transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
        }else if(axis== Direction.Axis.Z){
            transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
        }else{
            transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
        }
        return new Vec3(transitionRotated).add(originPos)/*.add(-transition.x,-transition.y,-transition.z)*/;
    }
    public static Vec3 getRotatedEntitySpeed(Vec3 currentPos,Vec3 originPos, float degree,int duration, Direction.Axis axis){
        Vec3 eachOffsetPos=currentPos.add(-originPos.x,-originPos.y,-originPos.z);
        Vector3f transitionRotated;
        Vector3f transition = new Vector3f((float) eachOffsetPos.x, (float) eachOffsetPos.y, (float) eachOffsetPos.z);
        float degreeAngle=degree/(float) duration;
        if(axis== Direction.Axis.X){
            transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
        }else if(axis== Direction.Axis.Z){
            transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
        }else{
            transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
        }
        return new Vec3(transitionRotated)/*.add(originPos)*/.add(-transition.x,-transition.y,-transition.z);
    }
    public static List<BlockPos> rotatePosList(List<BlockPos> oldList, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,int degreeAngle) {
        List<BlockPos> newList=oldList;

        for (int i = 0; i < oldList.size(); i++) {
            BlockPos eachPos = oldList.get(i).offset(-oldOriginPos.getX(),-oldOriginPos.getY(),-oldOriginPos.getZ());
               Vector3f origin = newOriginPos.getCenter().toVector3f();
                Vector3f transition = new Vector3f(eachPos.getX(), eachPos.getY(), eachPos.getZ());
            Vector3f transitionRotated;
            if(axis== Direction.Axis.X){
                transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
            }else if(axis== Direction.Axis.Z){
                transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
            }else{
                transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
            }
                Vector3f positionRotated = origin.add(transitionRotated);
               BlockPos rotatedPos = new BlockPos(Mth.floor(positionRotated.x), Mth.floor(positionRotated.y), Mth.floor(positionRotated.z));
               newList.set(i,rotatedPos);
        }
        return newList;
    }
    public static Vec3 rotateVec3SeatPos(BlockPos oldPos, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,float degreeAngle,BlockPos seatOriginPos,boolean isInBasket) {
            BlockPos eachOffsetPos=oldPos.offset(-seatOriginPos.getX(),-seatOriginPos.getY(),-seatOriginPos.getZ());
            BlockPos eachBasketOriginPos = seatOriginPos.offset(-oldOriginPos.getX(),-oldOriginPos.getY(),-oldOriginPos.getZ());
            Vector3f origin = newOriginPos.getCenter().toVector3f();
            Vector3f transition = new Vector3f(eachBasketOriginPos.getX(), eachBasketOriginPos.getY(), eachBasketOriginPos.getZ());
            if(!isInBasket){
                transition=new Vector3f(oldPos.getX(),oldPos.getY(),oldPos.getZ());
            }
            Vector3f transitionRotated;
            if(axis== Direction.Axis.X){
                transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
            }else if(axis== Direction.Axis.Z){
                transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
            }else{
                transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
            }
        if(!isInBasket){
            Vector3f positionRotated=origin.add(transitionRotated);
            return new Vec3(positionRotated);
        }else{
            Vector3f positionRotated = origin.add(transitionRotated).add(eachOffsetPos.getX(),eachOffsetPos.getY(),eachOffsetPos.getZ());
            return new Vec3(positionRotated);
        }
    }
    public static List<Vec3> rotateVec3PosList(List<BlockPos> oldList, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,float degreeAngle) {
        List<Vec3> newList=new ArrayList<>();
        for (int i = 0; i < oldList.size(); i++) {
            newList.add(oldList.get(i).getCenter());
            BlockPos eachPos = oldList.get(i).offset(-oldOriginPos.getX(),-oldOriginPos.getY(),-oldOriginPos.getZ());
            Vector3f origin = newOriginPos.getCenter().toVector3f();
            Vector3f transition = new Vector3f(eachPos.getX(), eachPos.getY(), eachPos.getZ());
            Vector3f transitionRotated;
            if(axis== Direction.Axis.X){
                transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
            }else if(axis== Direction.Axis.Z){
                transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
            }else{
                transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
            }
            Vector3f positionRotated = origin.add(transitionRotated);
            newList.set(i,new Vec3(positionRotated));
        }
        return newList;
    }
    public static List<BlockPos> rotateBasketPosList(List<BlockPos> oldBasketList, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,int degreeAngle,List<BlockPos> basketOriginPosList) {
        List<BlockPos> newList=oldBasketList;

        for (int i = 0; i < oldBasketList.size(); i++) {
            BlockPos eachOffsetPos=oldBasketList.get(i).offset(-basketOriginPosList.get(i).getX(),-basketOriginPosList.get(i).getY(),-basketOriginPosList.get(i).getZ());
            BlockPos eachBasketOriginPos = basketOriginPosList.get(i).offset(-oldOriginPos.getX(),-oldOriginPos.getY(),-oldOriginPos.getZ());
            Vector3f origin = newOriginPos.getCenter().toVector3f();
            Vector3f transition = new Vector3f(eachBasketOriginPos.getX(), eachBasketOriginPos.getY(), eachBasketOriginPos.getZ());
            Vector3f transitionRotated;
            if(axis== Direction.Axis.X){
                transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
            }else if(axis== Direction.Axis.Z){
                transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
            }else{
                transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
            }
            Vector3f positionRotated = origin.add(transitionRotated);
            BlockPos rotatedPos = new BlockPos(Mth.floor(positionRotated.x), Mth.floor(positionRotated.y), Mth.floor(positionRotated.z));
            newList.set(i,rotatedPos.offset(eachOffsetPos.getX(),eachOffsetPos.getY(),eachOffsetPos.getZ()));
        }
        return newList;
    }
    public static List<Vec3> rotateVec3BasketPosList(List<BlockPos> oldBasketList, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,float degreeAngle,List<BlockPos> basketOriginPosList) {
        List<Vec3> newList=new ArrayList<>();
        for (int i = 0; i < oldBasketList.size(); i++) {
            newList.add(oldBasketList.get(i).getCenter());
            BlockPos eachOffsetPos=oldBasketList.get(i).offset(-basketOriginPosList.get(i).getX(),-basketOriginPosList.get(i).getY(),-basketOriginPosList.get(i).getZ());
            BlockPos eachBasketOriginPos = basketOriginPosList.get(i).offset(-oldOriginPos.getX(),-oldOriginPos.getY(),-oldOriginPos.getZ());
            Vector3f origin = newOriginPos.getCenter().toVector3f();
            Vector3f transition = new Vector3f(eachBasketOriginPos.getX(), eachBasketOriginPos.getY(), eachBasketOriginPos.getZ());
            Vector3f transitionRotated;
            if(axis== Direction.Axis.X){
                transitionRotated = transition.rotateX(Mth.PI * (degreeAngle) / 180f);
            }else if(axis== Direction.Axis.Z){
                transitionRotated = transition.rotateZ(Mth.PI * (degreeAngle) / 180f);
            }else{
                transitionRotated = transition.rotateY(Mth.PI * (degreeAngle) / 180f);
            }
            Vector3f positionRotated = origin.add(transitionRotated).add(eachOffsetPos.getX(),eachOffsetPos.getY(),eachOffsetPos.getZ());
            newList.set(i,new Vec3(positionRotated));
        }
        return newList;
    }
    public static boolean makeMoveableBlock(Level level, BlockPos controllerPos, BlockPos startPos, int start, int duration, Direction.Axis axis, int degree, List<BlockPos> positionList,int visualDegree,boolean rotateState,BlockPos transitionPos,int startRotation){
        BlockState controllerState=level.getBlockState(controllerPos);
        if((controllerState.getBlock() instanceof RotationControllerBlock ||controllerState.getBlock() instanceof SlideControllerBlock)&&level.getBlockEntity(controllerPos) instanceof AbstractControllerBlockEntity controllerBlockEntity&&controllerBlockEntity.getItem(0).getItem()== Register.shape_card.get()&&controllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            CompoundTag entityTag=new CompoundTag();
            CompoundTag posTag=new CompoundTag();
            List<BlockPos> posList=controllerBlockEntity.getPositionList();
            CompoundTag stateTag=new CompoundTag();
            CompoundTag blockEntityTag=new CompoundTag();
            CompoundTag basketPosTag=new CompoundTag();
            CompoundTag basketOriginPosTag=new CompoundTag();
            CompoundTag basketStateTag=new CompoundTag();
            CompoundTag basketBlockEntityTag=new CompoundTag();
            CompoundTag basketMakerIndexTag=new CompoundTag();
            List<BlockState> stateList=new ArrayList<>();
            List<BlockState> basketStateList=new ArrayList<>();
            List<BlockPos> basketPosListAll=new ArrayList<>();
            CompoundTag seatPosTag=new CompoundTag();
            CompoundTag seatOriginPosTag=new CompoundTag();
            CompoundTag seatIsInBasketTag=new CompoundTag();
            List<BlockPos> seatPosList=new ArrayList<>();
            List<BlockPos> fullPosList=new ArrayList<>();
            int basketPosAmount=0;
            int seatPosAmount=0;
            Map<BlockPos, Entity> containedEntity = new HashMap<>();
            List<UUID> entityUUIDList=new ArrayList<>();
            Direction controllerDirection=controllerState.getValue(BlockStateProperties.FACING);
            for(int i=0; i<posList.size();i++) {
                BlockPos eachPos = positionList.get(i);
                BlockState eachState = level.getBlockState(eachPos);
                BlockEntity eachBlockEntity = level.getBlockEntity(eachPos);
                List<Entity> entityList = level.getEntitiesOfClass(Entity.class, new AABB(eachPos).inflate(0.1D));
                CompoundTag posNBT = NbtUtils.writeBlockPos(new BlockPos(posList.get(i).getX() - startPos.getX(), posList.get(i).getY() - startPos.getY(), posList.get(i).getZ() - startPos.getZ()));
                for(Entity entity : entityList){
                    if(!entityUUIDList.contains(entity.getUUID())&&!isUnableToMove(entity)&& !eachState.getCollisionShape(level,eachPos).isEmpty()&&!isDisableForStandingSeat(eachState)) {
                        containedEntity.put(eachPos, entity);
                        seatPosList.add(posList.get(i));
                        seatIsInBasketTag.putBoolean("isInBasket_" + String.valueOf(seatPosAmount), false);
                        seatPosTag.put("location_" + String.valueOf(seatPosAmount), posNBT);
                        seatOriginPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(BlockPos.ZERO));
                        ++seatPosAmount;
                        entityUUIDList.add(entity.getUUID());
                    }
                }
                if (eachBlockEntity != null && i != positionList.indexOf(controllerPos) && !(eachBlockEntity instanceof PistonMovingBlockEntity)) {
                    if (eachBlockEntity instanceof SlideControllerBlockEntity slideControllerBlockEntity2 && !slideControllerBlockEntity2.getPositionList().isEmpty() && VectorCardItem.isSelectionFinished(slideControllerBlockEntity2.getItem(1))) {

                        List<BlockPos> newPos = new ArrayList<>();
                        for (int ii = 0; ii < ((SlideControllerBlockEntity) eachBlockEntity).getPositionList().size(); ii++) {
                            newPos.add(slideControllerBlockEntity2.getPositionList().get(ii).offset(transitionPos.getX(), transitionPos.getY(), transitionPos.getZ()));
                        }
                        slideControllerBlockEntity2.setPositionList(newPos);
                        VectorCardItem.offsetTransition(slideControllerBlockEntity2.getItem(1), transitionPos);
                        fullPosList.addAll(slideControllerBlockEntity2.getPositionList());

                    } else if (eachBlockEntity instanceof RotationControllerBlockEntity rotationControllerBlockEntity && !rotationControllerBlockEntity.getPositionList().isEmpty()) {

                        List<BlockPos> newPos = new ArrayList<>();
                        for (int ii = 0; ii < ((RotationControllerBlockEntity) eachBlockEntity).getPositionList().size(); ii++) {
                            newPos.add(rotationControllerBlockEntity.getPositionList().get(ii).offset(transitionPos.getX(), transitionPos.getY(), transitionPos.getZ()));
                        }
                        rotationControllerBlockEntity.setPositionList(newPos);
                        fullPosList.addAll(rotationControllerBlockEntity.getPositionList());
                    } else if (axis != null && eachBlockEntity instanceof BasketMakerBlockEntity basketMakerBlockEntity && eachState.is(Register.basket_maker_block.get())) {
                        List<BlockPos> basketPosList = basketMakerBlockEntity.getPositionList();
                        BlockPos basketMakerPos = eachPos;
                        BlockPos basketOriginPos = basketMakerPos.relative(eachState.getValue(BasketMakerBlock.FACING));
                        for (int j = basketPosAmount; j < basketPosAmount + basketPosList.size(); j++) {
                            BlockPos eachBasketComponentPos = basketPosList.get(j - basketPosAmount);
                            BlockState eachBasketComponentState = level.getBlockState(eachBasketComponentPos);
                            BlockEntity eachBasketComponentBlockEntity = level.getBlockEntity(eachBasketComponentPos);
                            List<Entity> entityList2 = level.getEntitiesOfClass(Entity.class, new AABB(eachBasketComponentPos).inflate(0.1D));
                             for(Entity entity : entityList2){
                                if(!entityUUIDList.contains(entity.getUUID())&&!isUnableToMove(entity)&& !eachBasketComponentState.getCollisionShape(level,eachBasketComponentPos).isEmpty()&&!isDisableForStandingSeat(eachBasketComponentState)) {
                                    containedEntity.put(eachBasketComponentPos, entity);
                                    seatPosList.add(posList.get(i));
                                    seatIsInBasketTag.putBoolean("isInBasket_" + String.valueOf(seatPosAmount), true);
                                    seatOriginPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(new BlockPos(basketOriginPos.getX() - startPos.getX(), basketOriginPos.getY() - startPos.getY(), basketOriginPos.getZ() - startPos.getZ())));
                                    seatPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(new BlockPos(eachBasketComponentPos.getX() - startPos.getX(), eachBasketComponentPos.getY() - startPos.getY(), eachBasketComponentPos.getZ() - startPos.getZ())));
                                    ++seatPosAmount;
                                    entityUUIDList.add(entity.getUUID());
                                }
                            }
                                if (eachBasketComponentBlockEntity != null) {
                                    if (eachBasketComponentBlockEntity instanceof PistonMovingBlockEntity pistonMovingBlockEntity) {
                                        eachBasketComponentState = pistonMovingBlockEntity.getMovedState();
                                        basketBlockEntityTag.put("blockEntity_" + String.valueOf(j), new CompoundTag());
                                    } else if(fullPosList.contains(eachBasketComponentPos)){
                                        basketBlockEntityTag.put("blockEntity_" + String.valueOf(j), new CompoundTag());
                                    }else {
                                        basketBlockEntityTag.put("blockEntity_" + String.valueOf(j), eachBasketComponentBlockEntity.saveWithFullMetadata());
                                    }
                                } else {
                                    basketBlockEntityTag.put("blockEntity_" + String.valueOf(j), new CompoundTag());
                                }
                                if (eachBasketComponentState.getBlock() instanceof SeatBlock || eachBasketComponentState.getBlock() instanceof DoorBlock || eachBasketComponentState.getBlock() instanceof FenceGateBlock) {
                                    seatPosList.add(eachBasketComponentPos);
                                    seatIsInBasketTag.putBoolean("isInBasket_" + String.valueOf(seatPosAmount), true);
                                    seatOriginPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(new BlockPos(basketOriginPos.getX() - startPos.getX(), basketOriginPos.getY() - startPos.getY(), basketOriginPos.getZ() - startPos.getZ())));
                                    seatPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(new BlockPos(eachBasketComponentPos.getX() - startPos.getX(), eachBasketComponentPos.getY() - startPos.getY(), eachBasketComponentPos.getZ() - startPos.getZ())));
                                    ++seatPosAmount;
                                }
                                eachBasketComponentState = eachBasketComponentState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachBasketComponentState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachBasketComponentPos).isEmpty() ? eachBasketComponentState : Blocks.AIR.defaultBlockState();
                                 if(fullPosList.contains(eachBasketComponentPos)){
                                     eachBasketComponentState=Blocks.AIR.defaultBlockState();
                                }
                                basketOriginPosTag.put("location_" + String.valueOf(j), NbtUtils.writeBlockPos(new BlockPos(basketOriginPos.getX() - startPos.getX(), basketOriginPos.getY() - startPos.getY(), basketOriginPos.getZ() - startPos.getZ())));
                                basketPosTag.put("location_" + String.valueOf(j), NbtUtils.writeBlockPos(new BlockPos(eachBasketComponentPos.getX() - startPos.getX(), eachBasketComponentPos.getY() - startPos.getY(), eachBasketComponentPos.getZ() - startPos.getZ())));
                                basketStateTag.put("state_" + String.valueOf(j), NbtUtils.writeBlockState(eachBasketComponentState));
                                basketMakerIndexTag.putInt("index_" + String.valueOf(j), i);
                                basketPosListAll.add(eachBasketComponentPos);
                                basketStateList.add(eachBasketComponentState);
                                fullPosList.add(eachBasketComponentPos);
                        }
                        if (controllerBlockEntity instanceof RotationControllerBlockEntity rotationControllerBlockEntity) {
                            rotationControllerBlockEntity.setBasketPosList(basketPosTag);
                            rotationControllerBlockEntity.setBasketOriginPosList(basketOriginPosTag);
                            if (degree % 90 == 0 && !controllerBlockEntity.isLoop()) {
                                basketMakerBlockEntity.rotateBasketPosList(eachPos, eachState, degree, controllerPos.relative(controllerDirection));
                            }
                        }
                        basketPosAmount += basketPosList.size();
                    }
                    if(fullPosList.contains(eachPos)){
                        blockEntityTag.put("blockEntity_" + String.valueOf(i), new CompoundTag());
                    }else{
                        blockEntityTag.put("blockEntity_" + String.valueOf(i), eachBlockEntity.saveWithFullMetadata());
                    }
                } else if (eachBlockEntity instanceof PistonMovingBlockEntity pistonMovingBlockEntity) {
                    eachState = pistonMovingBlockEntity.getMovedState();
                    blockEntityTag.put("blockEntity_" + String.valueOf(i), new CompoundTag());
                } else if (eachState.getBlock() instanceof PressurePlateBlock || eachState.getBlock() instanceof ButtonBlock || eachState.getBlock() instanceof TripWireBlock) {
                    eachState = eachState.setValue(ButtonBlock.POWERED, false);
                    blockEntityTag.put("blockEntity_" + String.valueOf(i), new CompoundTag());
                } else {
                    blockEntityTag.put("blockEntity_" + String.valueOf(i), new CompoundTag());
                }
                if (eachState.is(Register.TAG_DISABLE_MOVING) || i == positionList.indexOf(controllerPos)) {
                    eachState = Blocks.AIR.defaultBlockState();
                }
                if (axis != null) {
                    if (rotateState && eachState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && axis == Direction.Axis.Y) {
                        Direction oldDirection = eachState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                        Direction newDirection = oldDirection;
                        if (degree == 90) {
                            newDirection = oldDirection.getCounterClockWise();
                        } else if (degree == -90) {
                            newDirection = oldDirection.getClockWise();
                        } else if (degree == 180 || degree == -180) {
                            newDirection = oldDirection.getOpposite();
                        }
                        eachState = eachState.setValue(BlockStateProperties.HORIZONTAL_FACING, newDirection);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.AXIS)) {
                        Direction.Axis oldAxis = eachState.getValue(BlockStateProperties.AXIS);
                        Direction.Axis newAxis = oldAxis;
                        if (degree == 90 || degree == -90) {
                            if (axis == Direction.Axis.X) {
                                newAxis = oldAxis == Direction.Axis.Y ? Direction.Axis.Z : oldAxis == Direction.Axis.X ? Direction.Axis.X : Direction.Axis.Y;
                            } else if (axis == Direction.Axis.Y) {
                                newAxis = oldAxis == Direction.Axis.X ? Direction.Axis.Z : oldAxis == Direction.Axis.Y ? Direction.Axis.Y : Direction.Axis.X;
                            } else if (axis == Direction.Axis.Z) {
                                newAxis = oldAxis == Direction.Axis.X ? Direction.Axis.Y : oldAxis == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X;
                            }
                        }
                        eachState = eachState.setValue(BlockStateProperties.AXIS, newAxis);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.FACING)) {
                        Direction oldDirection = eachState.getValue(BlockStateProperties.FACING);
                        Direction newDirection = oldDirection;
                        if (degree == 90) {
                            newDirection = oldDirection.getCounterClockWise(controllerState.getValue(BlockStateProperties.FACING).getAxis());
                        } else if (degree == -90) {
                            newDirection = oldDirection.getClockWise(controllerState.getValue(BlockStateProperties.FACING).getAxis());
                        } else if (degree == 180 || degree == -180) {
                            newDirection = oldDirection.getClockWise(controllerState.getValue(BlockStateProperties.FACING).getAxis()).getClockWise(controllerState.getValue(BlockStateProperties.FACING).getAxis());
                        }
                        eachState = eachState.setValue(BlockStateProperties.FACING, newDirection);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.HALF) && controllerState.getValue(BlockStateProperties.FACING).getAxis() != Direction.Axis.Y) {
                        Half oldHalf = eachState.getValue(BlockStateProperties.HALF);
                        Half newHalf = oldHalf;
                        if (degree == 180 || degree == -180) {
                            if (oldHalf == Half.BOTTOM) {
                                newHalf = Half.TOP;
                            } else if (oldHalf == Half.TOP) {
                                newHalf = Half.BOTTOM;
                            }
                        }
                        eachState = eachState.setValue(BlockStateProperties.HALF, newHalf);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.SLAB_TYPE) && controllerState.getValue(BlockStateProperties.FACING).getAxis() != Direction.Axis.Y) {
                        SlabType oldHalf = eachState.getValue(BlockStateProperties.SLAB_TYPE);
                        SlabType newHalf = oldHalf;
                        if (degree == 180 || degree == -180) {
                            if (oldHalf == SlabType.BOTTOM) {
                                newHalf = SlabType.TOP;
                            } else if (oldHalf == SlabType.TOP) {
                                newHalf = SlabType.BOTTOM;
                            }
                        }
                        eachState = eachState.setValue(BlockStateProperties.SLAB_TYPE, newHalf);
                    }
                    if (rotateState && eachState.getBlock() instanceof CrossCollisionBlock) {
                        boolean north = eachState.getValue(CrossCollisionBlock.NORTH);
                        boolean east = eachState.getValue(CrossCollisionBlock.EAST);
                        boolean south = eachState.getValue(CrossCollisionBlock.SOUTH);
                        boolean west = eachState.getValue(CrossCollisionBlock.WEST);
                        boolean north2 = north;
                        boolean east2 = east;
                        boolean south2 = south;
                        boolean west2 = west;

                        if (axis == Direction.Axis.X) {
                            if (degree == 180 || degree == -180) {
                                north2 = south;
                                south2 = north;
                            }
                        } else if (axis == Direction.Axis.Y) {
                            if (degree == 90) {
                                north2 = east;
                                west2 = north;
                                south2 = west;
                                east2 = south;
                            } else if (degree == -90) {
                                north2 = west;
                                west2 = south;
                                south2 = east;
                                east2 = north;
                            } else if (degree == 180 || degree == -180) {
                                east2 = west;
                                west2 = east;
                                north2 = south;
                                south2 = north;
                            }
                        } else if (axis == Direction.Axis.Z) {
                            if (degree == 180 || degree == -180) {
                                east2 = west;
                                west2 = east;
                            }
                        }
                        eachState = eachState.setValue(CrossCollisionBlock.NORTH, north2).setValue(CrossCollisionBlock.EAST, east2).setValue(CrossCollisionBlock.SOUTH, south2).setValue(CrossCollisionBlock.WEST, west2);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                        DoubleBlockHalf oldHalf = eachState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
                        DoubleBlockHalf newHalf = oldHalf;
                        if (degree == 180 || degree == -180) {
                            if (axis == Direction.Axis.X || axis == Direction.Axis.Z) {
                                newHalf = oldHalf == DoubleBlockHalf.LOWER ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER;
                            }
                        }
                        eachState = eachState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, newHalf);
                    }
                    if (rotateState && eachState.hasProperty(BlockStateProperties.BED_PART)) {
                        BedPart oldPart = eachState.getValue(BlockStateProperties.BED_PART);
                        BedPart newPart = oldPart;
                        if (degree == 180 || degree == -180) {
                            if (axis == Direction.Axis.X || axis == Direction.Axis.Z) {
                                newPart = oldPart == BedPart.FOOT ? BedPart.HEAD : BedPart.FOOT;
                            }
                        }
                        eachState = eachState.setValue(BlockStateProperties.BED_PART, newPart);
                    }
                }

                eachState = eachState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachPos).isEmpty() ? eachState : Blocks.AIR.defaultBlockState();
                 if (eachState.getBlock() instanceof SeatBlock || eachState.getBlock() instanceof DoorBlock || eachState.getBlock() instanceof FenceGateBlock) {
                    seatPosList.add(posList.get(i));
                    seatIsInBasketTag.putBoolean("isInBasket_" + String.valueOf(seatPosAmount), false);
                    seatPosTag.put("location_" + String.valueOf(seatPosAmount), posNBT);
                    seatOriginPosTag.put("location_" + String.valueOf(seatPosAmount), NbtUtils.writeBlockPos(BlockPos.ZERO));
                    ++seatPosAmount;
                }
                if(fullPosList.contains(posList.get(i))){
                    eachState=Blocks.AIR.defaultBlockState();
                }
                posTag.put("location_" + String.valueOf(i), posNBT);
                CompoundTag stateNBT = NbtUtils.writeBlockState(eachState);
                stateTag.put("state_" + String.valueOf(i), stateNBT);
                stateList.add(eachState);
                fullPosList.add(posList.get(i));
            }

            /*entityTag.put("positionList",posTag);
            entityTag.put("stateList",stateTag);
            entityTag.put("blockEntityList",blockEntityTag);*/
            CompoundTag basketTag=new CompoundTag();
            if(axis!=null) {
                basketTag.put("indexList", basketMakerIndexTag);
                basketTag.put("positionList", basketPosTag);
                basketTag.put("originPositionList", basketOriginPosTag);
                basketTag.put("stateList", basketStateTag);
                basketTag.put("blockEntityList", basketBlockEntityTag);
            }
            CompoundTag seatTag=new CompoundTag();
            seatTag.put("positionList",seatPosTag);
            seatTag.put("originPositionList",seatOriginPosTag);
            seatTag.put("isInBasketList",seatIsInBasketTag);
            entityTag.put("basketData",basketTag);
            entityTag.put("seatData",seatTag);
            if(reachedNBTLimit(basketTag)||reachedNBTLimit(posTag)/*||reachedNBTLimit(stateTag)||*//*reachedNBTLimit(blockEntityTag)*/){
                if(level.getServer()!=null) {
                    PlayerList playerlist = level.getServer().getPlayerList();
                    Component component=Component.translatable("info.ugoblock.data_overflow").withStyle(ChatFormatting.RED);
                    playerlist.broadcastSystemMessage(Component.literal("[UgoBlock] ").append(component),false);
                }
                return false;
            }else {
                MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, axis, degree, posTag, stateTag, blockEntityTag, entityTag, visualDegree, controllerBlockEntity.isLoop(), !rotateState, transitionPos);
                moveableBlock.setStartRotation(startRotation);
                for(int s=0;s<seatPosList.size();s++){
                    BlockPos eachPos=seatPosList.get(s);
                    BlockState eachState=level.getBlockState(eachPos);
                    if(eachState.getBlock() instanceof SeatBlock){
                        SeatEntity seatEntity=new SeatEntity(level,eachPos,true);
                        if(!level.isClientSide()){
                            level.addFreshEntity(seatEntity);
                        }
                        seatEntity.startRiding(moveableBlock);
                        AABB aabb = new AABB(eachPos);
                        Entity passenger=null;
                        for (Entity entity : level.getEntities((Entity) null, aabb.move(0D, 0D, 0D).inflate(0d, 0d, 0d), (o) -> {
                            return (o instanceof LivingEntity);
                        })) {
                            passenger=entity;
                            break;
                        }
                        if(passenger!=null) {
                            passenger.startRiding(seatEntity);
                         }

                    }else if(eachState.getBlock() instanceof DoorBlock||eachState.getBlock() instanceof FenceGateBlock){
                        DoorEntity doorEntity=new DoorEntity(level,eachPos);
                        if(!level.isClientSide()){
                            level.addFreshEntity(doorEntity);
                        }
                        doorEntity.startRiding(moveableBlock);
                    }

                }
                for(Map.Entry<BlockPos, Entity> entry : containedEntity.entrySet()){
                    BlockPos entityPos=entry.getKey();
                    Entity entity=entry.getValue();
                    if(!entity.isPassenger()){
                        StandingSeatEntity standingSeatEntity=new StandingSeatEntity(level,entityPos,true);
                        if(!level.isClientSide()){
                            level.addFreshEntity(standingSeatEntity);
                        }
                        entity.startRiding(standingSeatEntity);
                        standingSeatEntity.startRiding(moveableBlock);
                    }


                }

                if (axis != null) {
                    controllerBlockEntity.setVisualDegree(degree);
                }
                if (!level.isClientSide) {
                    level.addFreshEntity(moveableBlock);
                }
            }
        }
        return true;
    }
    private static int calculateDataSize(CompoundTag tag){
        return tag.sizeInBytes();
    }
    public static boolean reachedNBTLimit(CompoundTag tag){
        return calculateDataSize(tag)>=2097152;
    }
    public static Component getComponentFrequencyColors(DyeColor color1,DyeColor color2,DyeColor color3){
        return   Component.translatable("info.ugoblock.frequency_colors",Component.translatable(getColorComponent(color1)).getString(),Component.translatable(getColorComponent(color2)),Component.translatable(getColorComponent(color3))).withStyle(ChatFormatting.WHITE);
    }
    public static Component getComponentFrequencyAlreadyExists(DyeColor color1,DyeColor color2,DyeColor color3){
      return   Component.translatable("info.ugoblock.frequency_already_exists",Component.translatable(getColorComponent(color1)).getString(),Component.translatable(getColorComponent(color2)),Component.translatable(getColorComponent(color3))).withStyle(ChatFormatting.YELLOW);
    }
    private static String getColorComponent(DyeColor color){
        switch (color){
            case WHITE : return "info.ugoblock.color_white";
            case ORANGE: return "info.ugoblock.color_orange";
            case MAGENTA: return "info.ugoblock.color_magenta";
            case LIGHT_BLUE: return "info.ugoblock.color_light_blue";
            case YELLOW: return "info.ugoblock.color_yellow";
            case LIME: return "info.ugoblock.color_lime";
            case PINK: return "info.ugoblock.color_pink";
            case GRAY: return "info.ugoblock.color_gray";
            case LIGHT_GRAY: return "info.ugoblock.color_light_gray";
            case CYAN: return "info.ugoblock.color_cyan";
            case PURPLE: return "info.ugoblock.color_purple";
            case BLUE: return "info.ugoblock.color_blue";
            case BROWN: return "info.ugoblock.color_brown";
            case GREEN: return "info.ugoblock.color_green";
            case RED: return "info.ugoblock.color_red";
            case BLACK: return "info.ugoblock.color_black";
        }
        return "info.ugoblock.color_white";
    }
    public static void displayImage(GuiGraphics guiGraphics, String textureName, int width, int height,float scale){
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.blit(new ResourceLocation(ModCoreUgoBlock.MODID,"textures/gui/"+textureName+".png"), width, height, 0, 0, 256, 256);
        guiGraphics.pose().popPose();
    }
    public static void displayCenteredString(GuiGraphics guiGraphics,Component component,int width,int height,float scale,int color){
        Font font=Minecraft.getInstance().font;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawCenteredString(font, component,width,height, color);
        guiGraphics.drawString(font, component.getString(),width, height, color, false);

        guiGraphics.pose().popPose();
    }
    public static void displayString(GuiGraphics guiGraphics,Component component,int width,int height,float scale,int color){
        Font font=Minecraft.getInstance().font;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale,scale,scale);
        guiGraphics.drawString(font, component.getString(),width, height, color, true);
        guiGraphics.pose().popPose();
    }
    public static boolean isUnableToMove(Entity entity){
        if(entity instanceof CollisionEntity||entity instanceof MovingBlockEntity||entity instanceof SeatEntity||entity instanceof StandingSeatEntity){
            return true;
        }
        return false;
    }
    public static boolean isDisableForStandingSeat(BlockState state){
        Block block=state.getBlock();
        if(block instanceof DoorBlock||block instanceof FenceGateBlock||block instanceof SeatBlock){
            return true;
        }
        return false;
    }


}
