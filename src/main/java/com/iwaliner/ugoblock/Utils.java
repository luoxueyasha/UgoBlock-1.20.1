package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

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
    public static MovingBlockEntity.trigonometricFunctionType getReverseTrigonometricFunctionType(MovingBlockEntity.trigonometricFunctionType type){
        switch (type){
            case X_COUNTERCLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.X_CLOCKWISE;
            case X_CLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.X_COUNTERCLOCKWISE;
            case Y_COUNTERCLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.Y_CLOCKWISE;
            case Y_CLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.Y_COUNTERCLOCKWISE;
            case Z_COUNTERCLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.Z_CLOCKWISE;
            case Z_CLOCKWISE: return MovingBlockEntity.trigonometricFunctionType.Z_COUNTERCLOCKWISE;
            default: return MovingBlockEntity.trigonometricFunctionType.NONE;
        }
    }
    public static Direction.Axis getAxis(MovingBlockEntity.trigonometricFunctionType type) {
        switch (type){
            case X_CLOCKWISE,X_COUNTERCLOCKWISE: return Direction.Axis.X;
            case Z_CLOCKWISE,Z_COUNTERCLOCKWISE: return Direction.Axis.Z;
            default: return Direction.Axis.Y;
        }
    }
    }
