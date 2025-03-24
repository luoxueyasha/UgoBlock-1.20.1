package com.iwaliner.ugoblock.object.moving_block;

import com.iwaliner.ugoblock.object.controllable_block.ControllableEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ControllableEntityRenderer/*<T extends ControllableEntity> extends EntityRenderer<T>*/ {
   /* private final BlockRenderDispatcher blockRenderDispatcher;
    public ControllableEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
       blockRenderDispatcher= context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ControllableEntity entity, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int i1) {
        super.render((T) entity, p_114486_, p_114487_, poseStack, bufferSource, i1);
        poseStack.translate(-0.5D,-0.5D,-0.5D);
        poseStack.scale(1.1f,1.1f,1.1f);
      blockRenderDispatcher.renderSingleBlock(entity.getBlockState(),poseStack,bufferSource,i1,OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return null;
    }*/
}


