package com.iwaliner.ugoblock.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Display;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


@OnlyIn(Dist.CLIENT)
    public class AbstractMovingBlockRenderer extends DisplayRenderer<MovingBlockEntity, MovingBlockEntity.BlockRenderState> {
        private final BlockRenderDispatcher blockRenderer;

        protected AbstractMovingBlockRenderer(EntityRendererProvider.Context p_270283_) {
            super(p_270283_);
            this.blockRenderer = p_270283_.getBlockRenderDispatcher();
        }

        @Nullable
        protected Display.BlockDisplay.BlockRenderState getSubState(MovingBlockEntity p_277721_) {
            return p_277721_.blockRenderState();
        }

        public void renderInner(MovingBlockEntity movingBlock, MovingBlockEntity.BlockRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i0, float f0) {
            poseStack.translate(movingBlock.getDisplacementVisual().x,movingBlock.getDisplacementVisual().y,movingBlock.getDisplacementVisual().z);
            this.blockRenderer.renderSingleBlock(renderState.blockState(), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
        }
    }

