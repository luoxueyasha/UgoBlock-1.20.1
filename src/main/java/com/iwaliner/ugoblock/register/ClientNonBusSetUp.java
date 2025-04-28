package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.controller.VectorCardItem;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sun.jna.platform.win32.WinNT;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.*;

@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, value = Dist.CLIENT)
public class ClientNonBusSetUp {

    // @debug, only render one box for performance
    private static final double[] box = {
        0,0,0,1,1,1
    };
    private static final double minX = box[0];
    private static final double minY = box[1];
    private static final double minZ = box[2];
    private static final double maxX = box[3];
    private static final double maxY = box[4];
    private static final double maxZ = box[5];

    private static final double two_third_PI = 2.09439510239319549231;// PI*2/3
    private static final double one_third_PI = 1.04719755119659774615;// PI/3


    public static void renderShape(PoseStack poseStack, VertexConsumer vertexConsumer, double d1, double d2, double d3, float ff1, float ff2, float ff3, float ff4) {
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();

        // front
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, minY, minZ, maxX, minY, minZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, minY, minZ, minX, minY, maxZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, maxX, minY, minZ, maxX, minY, maxZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, minY, maxZ, maxX, minY, maxZ, ff1, ff2, ff3, ff4);

        // top
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, maxY, minZ, maxX, maxY, minZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, maxY, minZ, minX, maxY, maxZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, maxX, maxY, minZ, maxX, maxY, maxZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, maxY, maxZ, maxX, maxY, maxZ, ff1, ff2, ff3, ff4);

        // side
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, minY, minZ, minX, maxY, minZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, maxX, minY, minZ, maxX, maxY, minZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, minX, minY, maxZ, minX, maxY, maxZ, ff1, ff2, ff3, ff4);
        renderLine(matrix4f, matrix3f, vertexConsumer, d1, d2, d3, maxX, minY, maxZ, maxX, maxY, maxZ, ff1, ff2, ff3, ff4);

    }

    private static void renderLine(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, 
                                 double d1, double d2, double d3,
                                 double x1, double y1, double z1, 
                                 double x2, double y2, double z2,
                                 float r, float g, float b, float a) {
        float f = (float)(x2 - x1);
        float f1 = (float)(y2 - y1);
        float f2 = (float)(z2 - z1);
        // @debug, f3 is for normalizing the vector. maybe useless.
        // float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
        // f = f / f3;
        // f1 = f1 / f3;
        // f2 = f2 / f3;

        vertexConsumer.vertex(matrix4f, (float)(x1 + d1), (float)(y1 + d2), (float)(z1 + d3))
            .color(r, g, b, a).normal(matrix3f, f, f1, f2).endVertex();
        vertexConsumer.vertex(matrix4f, (float)(x2 + d1), (float)(y2 + d2), (float)(z2 + d3))
            .color(r, g, b, a).normal(matrix3f, f, f1, f2).endVertex();

    }

    public static void renderYellowOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity p_109640_, double x, double y, double z, BlockPos blockPos) {
        renderShape(poseStack, vertexConsumer, (double) blockPos.getX() - x, (double) blockPos.getY() - y, (double) blockPos.getZ() - z, 1f, 1f, 0f, 1f);
    }

    public static void renderGreenOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double) p_109644_.getX() - p_109641_, (double) p_109644_.getY() - p_109642_, (double) p_109644_.getZ() - p_109643_, 0f, 1f, 0f, 1f);
    }


    @SubscribeEvent
    public static void RenderLevelEvent(RenderLevelStageEvent event) throws Throwable {
        RenderLevelStageEvent.Stage stage = RenderLevelStageEvent.Stage.AFTER_SKY;
        Camera camera = event.getCamera();
        Entity cameraEntity = camera.getEntity();
        Vec3 cameraPos = camera.getPosition();
        if (event.getStage() == stage && cameraEntity instanceof Player player) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer lineBuffer = multiBufferSource.getBuffer(RenderType.lines());
            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();
            Item vectorCard = Register.vector_card.get();
            Item shapeCard = Register.shape_card.get();

            if (mainStack.is(vectorCard) || offStack.is(vectorCard)) {
                ItemStack stack = mainStack.is(vectorCard) ? mainStack : offStack;
                CompoundTag tag = stack.getTag();
                if (tag == null) {
                    return;
                }
                BlockPos startLocation;
                if (tag.contains("originPosition")) {
                    startLocation = NbtUtils.readBlockPos(tag.getCompound("originPosition"));

                    ClientNonBusSetUp.renderGreenOutline(poseStack, lineBuffer, player, cameraPos.x, cameraPos.y, cameraPos.z, startLocation);

                    if (tag.contains("endPosition")) {
                        BlockPos endLocation = NbtUtils.readBlockPos(tag.getCompound("endPosition"));
                        double startLocationX = startLocation.getX();
                        double startLocationY = startLocation.getY();
                        double startLocationZ = startLocation.getZ();
                        double dx=startLocationX-cameraPos.x+0.5D;
                        double dy=startLocationY-cameraPos.y+0.5D;
                        double dz=startLocationZ-cameraPos.z+0.5D;
                        double transitionX=endLocation.getX()-startLocationX;
                        double transitionY=endLocation.getY()-startLocationY;
                        double transitionZ=endLocation.getZ()-startLocationZ;

                        double length=0.5D;
                        double angleXZ=Math.atan(transitionZ/transitionX);
                        double arrowPointXZ_A_x=transitionX>=0? transitionX-length*Math.sin(one_third_PI-angleXZ) : transitionX+length*Math.sin(one_third_PI-angleXZ);
                        double arrowPointXZ_A_z=transitionX>=0? transitionZ-length*Math.cos(one_third_PI-angleXZ) : transitionZ+length*Math.cos(one_third_PI-angleXZ);
                        double arrowPointXZ_B_x=transitionX>=0? transitionX-length*Math.sin(two_third_PI-angleXZ) : transitionX+length*Math.sin(two_third_PI-angleXZ);
                        double arrowPointXZ_B_z=transitionX>=0? transitionZ-length*Math.cos(two_third_PI-angleXZ) : transitionZ+length*Math.cos(two_third_PI-angleXZ);

                        double angleXY=Math.atan(transitionY/transitionX);
                        double arrowPointXY_A_x=transitionX>=0? transitionX-length*Math.sin(one_third_PI-angleXY) : transitionX+length*Math.sin(one_third_PI-angleXY);
                        double arrowPointXY_A_y=transitionX>=0? transitionY-length*Math.cos(one_third_PI-angleXY) : transitionY+length*Math.cos(one_third_PI-angleXY);
                        double arrowPointXY_B_x=transitionX>=0? transitionX-length*Math.sin(two_third_PI-angleXY) : transitionX+length*Math.sin(two_third_PI-angleXY);
                        double arrowPointXY_B_y=transitionX>=0? transitionY-length*Math.cos(two_third_PI-angleXY) : transitionY+length*Math.cos(two_third_PI-angleXY);

                        double angleZY=Math.atan(transitionY/transitionZ);
                        double arrowPointZY_A_z=transitionZ>=0? transitionZ-length*Math.sin(one_third_PI-angleZY) : transitionZ+length*Math.sin(one_third_PI-angleZY);
                        double arrowPointZY_A_y=transitionZ>=0? transitionY-length*Math.cos(one_third_PI-angleZY) : transitionY+length*Math.cos(one_third_PI-angleZY);
                        double arrowPointZY_B_z=transitionZ>=0? transitionZ-length*Math.sin(two_third_PI-angleZY) : transitionZ+length*Math.sin(two_third_PI-angleZY);
                        double arrowPointZY_B_y=transitionZ>=0? transitionY-length*Math.cos(two_third_PI-angleZY) : transitionY+length*Math.cos(two_third_PI-angleZY);

                        float[] endColor = {0F, 1F, 0F, 1F};
                        float[] originColor = {1F, 1F, 1F, 1F};
                        for (int j = 0; j < 6; j++) {
                            float fx1 = 0F;
                            float fy1 = 0F;
                            float fz1 = 0F;
                            float b = 1F;
                            switch (j) {
                                case 0 -> fx1 = b;
                                case 1 -> fx1 = -b;
                                case 2 -> fy1 = b;
                                case 3 -> fy1 = -b;
                                case 4 -> fz1 = b;
                                case 5 -> fz1 = -b;
                            }
                            for (int i = 0; i < 6; i++) {
                                double dx1 = 0D;
                                double dy1 = 0D;
                                double dz1 = 0D;

                                switch (i) {
                                    case 0 -> dx1 += 0.52D;
                                    case 1 -> dx1 -= 0.52D;
                                    case 2 -> dy1 += 0.52D;
                                    case 3 -> dy1 -= 0.52D;
                                    case 4 -> dz1 += 0.52D;
                                    case 5 -> dz1 -= 0.52D;
                                }

                                double dx_total = dx + dx1, dy_total = dy + dy1, dz_total = dz + dz1;
                                poseStack.pushPose();
                                lineBuffer.vertex(poseStack.last().pose(), (float) (0 + dx_total), (float) (0 + dy_total), (float) (0 + dz_total)).color(originColor[0], originColor[1], originColor[2], originColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                switch (i) {
                                    case 0:
                                    case 1:
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (arrowPointZY_A_y + dy_total), (float) (arrowPointZY_A_z + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (arrowPointZY_B_y + dy_total), (float) (arrowPointZY_B_z + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        break;
                                    case 2:
                                    case 3:
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (arrowPointXZ_A_x + dx_total), (float) (transitionY + dy_total), (float) (arrowPointXZ_A_z + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (arrowPointXZ_B_x + dx_total), (float) (transitionY + dy_total), (float) (arrowPointXZ_B_z + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        break;
                                    case 4:
                                    case 5:
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (arrowPointXY_A_x + dx_total), (float) (arrowPointXY_A_y + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (arrowPointXY_B_x + dx_total), (float) (arrowPointXY_B_y + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        lineBuffer.vertex(poseStack.last().pose(), (float) (transitionX + dx_total), (float) (transitionY + dy_total), (float) (transitionZ + dz_total)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                        break;

                                }
                                poseStack.popPose();
                            }
                        }
                    }
                }
            } else if (mainStack.is(shapeCard) || offStack.is(shapeCard)) {
                CompoundTag tag = (mainStack.is(shapeCard) ? mainStack : offStack).getTag();
                if (tag == null) {
                    return;
                }
                HitResult hitResult = Minecraft.getInstance().hitResult;
                if (!(hitResult instanceof BlockHitResult)) {
                    return;
                }
                BlockPos hitPos = ((BlockHitResult) Objects.requireNonNull(hitResult)).getBlockPos();
                int range = 6;

                // init frustum. maybe no use
                // BlockPos camBlockPos = camera.getBlockPosition();
                Frustum frustum = new Frustum(new Matrix4f(), new Matrix4f());
                frustum.prepare(cameraPos.x, cameraPos.y, cameraPos.z);

                if (!tag.contains("positionList")) {
                    tag.put("positionList", new CompoundTag());
                }
                CompoundTag posTag = tag.getCompound("positionList");
                List<BlockPos> blockPosList = new ArrayList<>();
                int ii = -1;
                for (int i = 0; i < Utils.maxSize; i++) {
                    if (!posTag.contains("location_" + i)) {
                        ii = i - 1;
                        break;
                    }

                    BlockPos pos = NbtUtils.readBlockPos(posTag.getCompound("location_" + i));
                    if (!pos.equals(Utils.errorPos())) {
                        int dx = Math.abs(pos.getX() - hitPos.getX());
                        int dy = Math.abs(pos.getY() - hitPos.getY());
                        int dz = Math.abs(pos.getZ() - hitPos.getZ());

                        if (dx <= range && dy <= range && dz <= range) {
                            AABB aabb = new AABB(pos).inflate(range);
                            if (frustum.isVisible(aabb)) {
                                blockPosList.add(pos);
                            }
                        }
                    }
                }

                if(ii == -1 || blockPosList.isEmpty()){
                    return;
                }

                for (BlockPos pos : blockPosList) {
                    renderYellowOutline(poseStack, lineBuffer, cameraEntity,
                        cameraPos.x, cameraPos.y, cameraPos.z, pos);
                }


            }
        }
    }

    @SubscribeEvent
    public static void RenderGUIEvent(RenderGuiOverlayEvent event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null || Minecraft.getInstance().options.hideGui){
            return;
        }

        ItemStack stack = player.getMainHandItem();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Window window = event.getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        int centerWidth = width / 2;
        int centerHeight = height / 2;
        Font font = Minecraft.getInstance().font;
        PoseStack poseStack = guiGraphics.pose();
        MultiBufferSource multiBufferSource = event.getGuiGraphics().bufferSource();
        if (stack.is(Register.vector_card.get()) && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag == null){
                return;
            }
            BlockPos startLocation = NbtUtils.readBlockPos(tag.getCompound("originPosition"));
            ClientNonBusSetUp.renderGreenOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), player, player.position().x, player.position().y, player.position().z, startLocation);
            if ((!VectorCardItem.isSelectionFinished(stack) && !VectorCardItem.isDuringSelection(stack))) {
                guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_origin_pos").getString(), centerWidth, height - 75, 7208704);
            } else if (VectorCardItem.isDuringSelection(stack)) {
                guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_end_pos").getString(), centerWidth, height - 75, 7208704);
            } else if (VectorCardItem.isSelectionFinished(stack)) {
                guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_finish").getString(), centerWidth, height - 75, 7208704);
            }
        } else if (stack.is(Register.shape_card.get())) {
            guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.shape_card_text").getString(), centerWidth, height - 88, 16776960);
            guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.shape_card_text2").getString(), centerWidth, height - 78, 16776960);
            guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.reset_setting_text").getString(), centerWidth, height - 68, 16776960);
        } else if (stack.is(Register.basket_maker_blockitem.get())) {
            guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.basket_maker_text").getString(), centerWidth, height - 85, 58584);
            guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.basket_maker_text2").getString(), centerWidth, height - 75, 58584);
        } else if (stack.is(Register.slide_controller_blockitem.get())) {
            Utils.displayImage(guiGraphics, "slide_controller_display", centerWidth - 200, centerHeight - 90, 0.8F);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.shape_card_display_text"), centerWidth - 130, centerHeight - 55, 0.8F, 16766976);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.vector_card_display_text"), centerWidth - 135, centerHeight + 30, 0.8F, 4123655);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obserber_display_text1"), centerWidth - 160, centerHeight + 78, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obserber_display_text2"), centerWidth - 160, centerHeight + 90, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.seat_display_text"), centerWidth - 160, centerHeight + 119, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.oneway_display_text"), centerWidth - 192, centerHeight + 145, 0.8F, 8587492);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text1"), centerWidth - 80, centerHeight - 78, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text2"), centerWidth - 80, centerHeight - 68, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text1"), centerWidth - 80, centerHeight + 3, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text2"), centerWidth - 80, centerHeight + 13, 0.8F, 16777215);
        } else if (stack.is(Register.rotation_controller_blockitem.get())) {
            Utils.displayImage(guiGraphics, "rotation_controller_display", centerWidth - 200, centerHeight - 90, 0.8F);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.shape_card_display_text"), centerWidth - 130, centerHeight - 55, 0.8F, 16766976);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.rotation_direction_display_text1"), centerWidth - 130, centerHeight - 35, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.rotation_direction_display_text2"), centerWidth - 130, centerHeight - 25, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.basket_maker_display_text"), centerWidth - 197, centerHeight + 70, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obserber_display_text1"), centerWidth - 160, centerHeight + 93, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obserber_display_text2"), centerWidth - 160, centerHeight + 103, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.seat_display_text"), centerWidth - 160, centerHeight + 134, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text1"), centerWidth - 80, centerHeight - 78, 0.8F, 16777215);
            Utils.displayString(guiGraphics, Component.translatable("info.ugoblock.obtain_card_display_text2"), centerWidth - 80, centerHeight - 68, 0.8F, 16777215);
        }


    }
}