package com.iwaliner.ugoblock.object.basket_maker;

import com.iwaliner.ugoblock.object.controller.SlideControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasketMakerRenderer implements BlockEntityRenderer<BasketMakerBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    public BasketMakerRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher=context.getBlockRenderDispatcher();
    }

    public void render(BasketMakerBlockEntity blockEntity, float f1, PoseStack poseStack, MultiBufferSource bufferSource, int i1, int i2) {
      BlockState imitatingState=blockEntity.getImitatingState();
        if(!imitatingState.isAir()){
            poseStack.pushPose();
            poseStack.scale(1.005F, 1.005F, 1.005F);
            poseStack.translate(-0.0025F,-0.0025F,-0.0025F);
            this.blockRenderDispatcher.renderSingleBlock(imitatingState,poseStack,bufferSource,16777215, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}