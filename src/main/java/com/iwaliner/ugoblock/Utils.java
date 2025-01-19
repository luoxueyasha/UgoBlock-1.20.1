package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.object.controller.*;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static int getMaxSize(){
        return 25000;
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
            for (int i = 0; i < Utils.getMaxSize(); i++) {
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
    public static List<Vec3> rotateVec3PosList(List<BlockPos> oldList, BlockPos oldOriginPos, BlockPos newOriginPos, Direction.Axis axis,int degreeAngle) {
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
    public static void makeMoveableBlock(Level level, BlockPos controllerPos, BlockPos startPos, int start, int duration, Direction.Axis axis, int degree, List<BlockPos> positionList,int visualDegree,boolean rotateState,BlockPos transitionPos){
        BlockState controllerState=level.getBlockState(controllerPos);
        if((controllerState.getBlock() instanceof RotationControllerBlock ||controllerState.getBlock() instanceof SlideControllerBlock)&&level.getBlockEntity(controllerPos) instanceof AbstractControllerBlockEntity controllerBlockEntity&&controllerBlockEntity.getItem(0).getItem()== Register.shape_card.get()&&controllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            CompoundTag entityTag=new CompoundTag();
            CompoundTag posTag=new CompoundTag();
            List<BlockPos> posList=controllerBlockEntity.getPositionList();

            CompoundTag stateTag=new CompoundTag();
            CompoundTag blockEntityTag=new CompoundTag();
            Direction controllerDirection=controllerState.getValue(BlockStateProperties.FACING);
            boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
            if(invertRotation){
                degree=-degree;
                visualDegree=-visualDegree;
            }
            for(int i=0; i<posList.size();i++){
                BlockPos eachPos=positionList.get(i);
                BlockState eachState=level.getBlockState(eachPos);
                BlockEntity eachBlockEntity = level.getBlockEntity(eachPos);
                if (eachBlockEntity != null&&i!=positionList.indexOf(controllerPos)&&!(eachBlockEntity instanceof PistonMovingBlockEntity)) {
                    if (eachBlockEntity instanceof SlideControllerBlockEntity slideControllerBlockEntity2 && !slideControllerBlockEntity2.getPositionList().isEmpty() && !slideControllerBlockEntity2.getEndPos().equals(Utils.errorPos())) {

                        List<BlockPos> newPos = new ArrayList<>();
                        for (int ii = 0; ii < ((SlideControllerBlockEntity) eachBlockEntity).getPositionList().size(); ii++) {
                            newPos.add(slideControllerBlockEntity2.getPositionList().get(ii).offset(transitionPos.getX(), transitionPos.getY(), transitionPos.getZ()));
                        }
                        slideControllerBlockEntity2.setPositionList(newPos);
                        BlockPos newEndPos = slideControllerBlockEntity2.getEndPos().offset(transitionPos.getX(), transitionPos.getY(), transitionPos.getZ());
                        slideControllerBlockEntity2.setEndPos(newEndPos);

                    }else if (eachBlockEntity instanceof RotationControllerBlockEntity rotationControllerBlockEntity && !rotationControllerBlockEntity.getPositionList().isEmpty() ) {

                        List<BlockPos> newPos = new ArrayList<>();
                        for (int ii = 0; ii < ((RotationControllerBlockEntity) eachBlockEntity).getPositionList().size(); ii++) {
                            newPos.add(rotationControllerBlockEntity.getPositionList().get(ii).offset(transitionPos.getX(), transitionPos.getY(), transitionPos.getZ()));
                        }
                        rotationControllerBlockEntity.setPositionList(newPos);
                    }
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),eachBlockEntity.saveWithFullMetadata());
                }else if (eachBlockEntity instanceof PistonMovingBlockEntity pistonMovingBlockEntity) {
                    eachState=pistonMovingBlockEntity.getMovedState();
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }else if (eachState.getBlock() instanceof PressurePlateBlock ||eachState.getBlock() instanceof ButtonBlock ||eachState.getBlock() instanceof TripWireBlock) {
                    eachState=eachState.setValue(ButtonBlock.POWERED,false);
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }else{
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }
                if(eachState.is(Register.TAG_DISABLE_MOVING)||i==positionList.indexOf(controllerPos)){
                    eachState= Blocks.AIR.defaultBlockState();
                }
                if(axis!=null) {
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
                }
                eachState= eachState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachPos).isEmpty() ? eachState : Blocks.AIR.defaultBlockState();
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(new BlockPos(posList.get(i).getX() - startPos.getX(), posList.get(i).getY() - startPos.getY(), posList.get(i).getZ() - startPos.getZ())));
                stateTag.put("state_" + String.valueOf(i), NbtUtils.writeBlockState(eachState));

            }
            entityTag.put("positionList",posTag);
            entityTag.put("stateList",stateTag);
            entityTag.put("blockEntityList",blockEntityTag);

            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, axis,degree,entityTag,visualDegree,controllerBlockEntity.isLoop(),!rotateState,transitionPos);
            if(axis!=null) {
                controllerBlockEntity.setVisualDegree(degree);
            }
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }

}
