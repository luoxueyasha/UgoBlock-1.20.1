package com.iwaliner.ugoblock.object.seat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InvisibleEntityRenderer<T extends Entity, S> extends EntityRenderer<T> {
    public InvisibleEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }
    public void render(T floorMaker, float f1, float f2, PoseStack poseStack, MultiBufferSource bufferSource, int i0) {
    }
    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
