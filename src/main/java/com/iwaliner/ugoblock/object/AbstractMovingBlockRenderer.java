package com.iwaliner.ugoblock.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


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
            List<BlockPos> posList=movingBlock.getPosList();
            List<BlockState> stateList=movingBlock.getStateList();
            poseStack.translate(movingBlock.getDisplacementVisual().x,movingBlock.getDisplacementVisual().y,movingBlock.getDisplacementVisual().z);
            this.blockRenderer.renderSingleBlock(renderState.blockState(), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
            for(int i=0;i<posList.size();i++){
                if(posList.get(i).getX()!=0||posList.get(i).getY()!=0||posList.get(i).getZ()!=0) {
                    poseStack.pushPose();
                    poseStack.translate(posList.get(i).getX(), posList.get(i).getY(), posList.get(i).getZ());
                    this.blockRenderer.renderSingleBlock(stateList.get(i), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            }
           /* poseStack.translate(movingBlock.getDisplacementVisual().x,movingBlock.getDisplacementVisual().y,movingBlock.getDisplacementVisual().z);
            this.blockRenderer.renderSingleBlock(renderState.blockState(), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
           poseStack.pushPose();
            poseStack.translate(0D,0D,1D);
            this.blockRenderer.renderSingleBlock(Blocks.DIAMOND_BLOCK.defaultBlockState(), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0D,0D,2D);
            this.blockRenderer.renderSingleBlock(Blocks.GOLD_BLOCK.defaultBlockState(), poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();*/
        }
    }

