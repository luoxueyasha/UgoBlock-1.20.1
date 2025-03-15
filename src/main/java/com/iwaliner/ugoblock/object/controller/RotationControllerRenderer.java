package com.iwaliner.ugoblock.object.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RotationControllerRenderer implements BlockEntityRenderer<RotationControllerBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    public RotationControllerRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher=context.getBlockRenderDispatcher();
    }

    public void render(RotationControllerBlockEntity blockEntity, float f1, PoseStack poseStack, MultiBufferSource bufferSource, int i1, int i2) {
      BlockState imitatingState=blockEntity.getImitatingState();
        if(!imitatingState.isAir()){
            poseStack.pushPose();
            poseStack.scale(1.005F, 1.005F, 1.005F);
            poseStack.translate(-0.0025F,-0.0025F,-0.0025F);
            this.blockRenderDispatcher.renderSingleBlock(imitatingState,poseStack,bufferSource,i1, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

    }


}