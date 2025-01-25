package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.controller.VectorCardItem;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.phys.shapes.Shapes.box;

@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, value = Dist.CLIENT)
public class ClientNonBusSetUp {

    @SubscribeEvent
    public static void RenderLineEvent(RenderHighlightEvent.Block event) {
        PoseStack poseStack = event.getPoseStack();
       MultiBufferSource multiBufferSource = event.getMultiBufferSource();
        Entity entity=event.getCamera().getEntity();
        if(entity instanceof Player player) {
            BlockPos pos = new BlockPos((int) event.getTarget().getLocation().x, (int) event.getTarget().getLocation().y, (int) event.getTarget().getLocation().z);
            ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            Level level = player.level();
            BlockState state = level.getBlockState(pos);
            HitResult hitResult = Minecraft.getInstance().hitResult;
            BlockPos hitPos = ((BlockHitResult) Objects.requireNonNull(hitResult)).getBlockPos();
            BlockState hitState = level.getBlockState(hitPos);


            if (heldStack.getItem() == Register.vector_card.get()) {
                CompoundTag tag=heldStack.getTag();
                /*if (tag!=null&&tag.contains("endPosition")) {
                    BlockPos endLocation= NbtUtils.readBlockPos(tag.getCompound("endPosition"));
                          ClientNonBusSetUp.renderGreenOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), event.getCamera().getEntity(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, endLocation);
                }*/
                if (tag!=null&&tag.contains("originPosition")) {
                    BlockPos startLocation= NbtUtils.readBlockPos(tag.getCompound("originPosition"));
                        ClientNonBusSetUp.renderGreenOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), event.getCamera().getEntity(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, startLocation);
                }
                if(tag!=null&&tag.contains("originPosition")&&tag.contains("endPosition")){
                    BlockPos startLocation= NbtUtils.readBlockPos(tag.getCompound("originPosition"));
                    BlockPos endLocation= NbtUtils.readBlockPos(tag.getCompound("endPosition"));
                    VertexConsumer vertexConsumer=multiBufferSource.getBuffer(RenderType.lines());
                    double dx=startLocation.getX()-event.getCamera().getPosition().x+0.5D;
                    double dy=startLocation.getY()-event.getCamera().getPosition().y+0.5D;
                    double dz=startLocation.getZ()-event.getCamera().getPosition().z+0.5D;
                    double transitionX=endLocation.getX()-startLocation.getX();
                    double transitionY=endLocation.getY()-startLocation.getY();
                    double transitionZ=endLocation.getZ()-startLocation.getZ();



                    double arrowWithAngle=Mth.PI/6D;
                    double length=0.5D;
                    double angleXZ=Math.atan(transitionZ/transitionX);
                    double arrowPointXZ_A_x=transitionX>=0? transitionX-length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleXZ) : transitionX+length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleXZ);
                    double arrowPointXZ_A_z=transitionX>=0? transitionZ-length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleXZ) : transitionZ+length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleXZ);
                    double arrowPointXZ_B_x=transitionX>=0? transitionX-length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleXZ) : transitionX+length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleXZ);
                    double arrowPointXZ_B_z=transitionX>=0? transitionZ-length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleXZ) : transitionZ+length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleXZ);

                    double angleXY=Math.atan(transitionY/transitionX);
                    double arrowPointXY_A_x=transitionX>=0? transitionX-length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleXY) : transitionX+length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleXY);
                    double arrowPointXY_A_y=transitionX>=0? transitionY-length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleXY) : transitionY+length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleXY);
                    double arrowPointXY_B_x=transitionX>=0? transitionX-length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleXY) : transitionX+length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleXY);
                    double arrowPointXY_B_y=transitionX>=0? transitionY-length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleXY) : transitionY+length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleXY);

                    double angleZY=Math.atan(transitionY/transitionZ);
                    double arrowPointZY_A_z=transitionZ>=0? transitionZ-length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleZY) : transitionZ+length*Math.sin((Mth.PI/2D)-arrowWithAngle-angleZY);
                    double arrowPointZY_A_y=transitionZ>=0? transitionY-length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleZY) : transitionY+length*Math.cos((Mth.PI/2D)-arrowWithAngle-angleZY);
                    double arrowPointZY_B_z=transitionZ>=0? transitionZ-length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleZY) : transitionZ+length*Math.sin((Mth.PI/2D)+arrowWithAngle-angleZY);
                    double arrowPointZY_B_y=transitionZ>=0? transitionY-length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleZY) : transitionY+length*Math.cos((Mth.PI/2D)+arrowWithAngle-angleZY);

                    for(int j=0;j<6;j++) {
                        float fx1 = 0F;
                        float fy1 = 0F;
                        float fz1 = 0F;
                        if(j==0){
                            fx1=1F;
                        }else if(j==1){
                            fx1=-1F;
                        }else if(j==2){
                            fy1=1F;
                        }else if(j==3){
                            fy1=-1F;
                        }else if(j==4){
                            fz1=1F;
                        }else if(j==5){
                            fz1=-1F;
                        }

                        for (int i = 0; i < 6; i++) {
                            double dx1 = 0D;
                            double dy1 = 0D;
                            double dz1 = 0D;

                            if (i == 0) {
                                dx1 += 0.52D;
                            } else if (i == 1) {
                                dx1 -= 0.52D;
                            } else if (i == 2) {
                                dy1 += 0.52D;
                            } else if (i == 3) {
                                dy1 -= 0.52D;
                            } else if (i == 4) {
                                dz1 += 0.52D;
                            } else if (i == 5) {
                                dz1 -= 0.52D;
                            }
                            float[] endColor = {0F, 1F, 0F, 1F};
                            float[] originColor = {1F, 1F, 1F, 1F};
                            poseStack.pushPose();
                            //vertexConsumer.vertex(poseStack.last().pose(), (float) (0 + dx+dx1), (float) (0 + dy+dy1), (float) (0 + dz+dz1)).color(originColor[0], originColor[1], originColor[2], originColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                            //vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx+dx1), (float) (transitionY + dy+dy1), (float) (transitionZ + dz+dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();


                            vertexConsumer.vertex(poseStack.last().pose(), (float) (0 + dx + dx1), (float) (0 + dy + dy1), (float) (0 + dz + dz1)).color(originColor[0], originColor[1], originColor[2], originColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                            vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();

                            poseStack.popPose();

                            if ((i == 2 || i == 3)) {
                                poseStack.pushPose();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (arrowPointXZ_A_x + dx + dx1), (float) (transitionY + dy + dy1), (float) (arrowPointXZ_A_z + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (arrowPointXZ_B_x + dx + dx1), (float) (transitionY + dy + dy1), (float) (arrowPointXZ_B_z + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                poseStack.popPose();
                            } else if ((i == 4 || i == 5)) {
                                poseStack.pushPose();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (arrowPointXY_A_x + dx + dx1), (float) (arrowPointXY_A_y + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (arrowPointXY_B_x + dx + dx1), (float) (arrowPointXY_B_y + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                poseStack.popPose();
                            } else if ((i == 0 || i == 1)) {
                                poseStack.pushPose();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (arrowPointZY_A_y + dy + dy1), (float) (arrowPointZY_A_z + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (arrowPointZY_B_y + dy + dy1), (float) (arrowPointZY_B_z + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                vertexConsumer.vertex(poseStack.last().pose(), (float) (transitionX + dx + dx1), (float) (transitionY + dy + dy1), (float) (transitionZ + dz + dz1)).color(endColor[0], endColor[1], endColor[2], endColor[3]).normal(poseStack.last().normal(), fx1, fy1, fz1).endVertex();
                                poseStack.popPose();
                            }
                        }
                    }
                 }
            }else if (heldStack.getItem() == Register.shape_card.get()) {
                CompoundTag tag=heldStack.getTag();
                List<BlockPos> list=new ArrayList<>();
                if(tag!=null){
                    if(!tag.contains("positionList")){
                        tag.put("positionList",new CompoundTag());
                    }
                    CompoundTag posTag=tag.getCompound("positionList");
                    int ii=-1;
                    for(int i = 0; i< Utils.getMaxSize(); i++) {
                        if(!posTag.contains("location_"+String.valueOf(i))){
                            ii=i-1;
                            break;
                        }else{
                            list.add(NbtUtils.readBlockPos(posTag.getCompound("location_"+String.valueOf(i))));
                        }
                    }
                    if (ii!=-1) {
                        int range=5;
                        loopA:  for(int i=-range;i<=range;i++) {
                            for (int j = -range; j <= range; j++) {
                                for (int k = -range; k <= range; k++) {
                                    BlockPos offsetPos = hitPos.offset(i, j, k);
                                    if(list.contains(offsetPos)&& !offsetPos.equals(Utils.errorPos())){
                                        ClientNonBusSetUp.renderYellowOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), event.getCamera().getEntity(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, offsetPos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static void renderShape(PoseStack poseStack, VertexConsumer vertexConsumer, double d1, double d2, double d3, float ff1, float ff2, float ff3, float ff4) {
        PoseStack.Pose posestack$pose = poseStack.last();
        VoxelShape voxelShape2=box(0.125D, -0.01D, 0.125D, 0.875D, 1.01D, 0.875D);
        VoxelShape voxelShape3=box(0.125D, 0.125D, -0.01D, 0.875D, 0.875D, 1.01D);
        VoxelShape voxelShape4=box(-0.01D, 0.125D, 0.125D, 1.01D, 0.875D, 0.875D);
        VoxelShape shapes= Shapes.or(voxelShape2,voxelShape3,voxelShape4);
        shapes.forAllEdges((d01, d02, d03, d04, d05, d06) -> {
            float f = (float)(d04 - d01);
            float f1 = (float)(d05 - d02);
            float f2 = (float)(d06 - d03);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            vertexConsumer.vertex(posestack$pose.pose(), (float)(d04 + d1), (float)(d05 + d2), (float)(d06 + d3)).color(ff1, ff2, ff3, ff4).normal(posestack$pose.normal(), f, f1, f2).endVertex();
            vertexConsumer.vertex(posestack$pose.pose(), (float)(d01 + d1), (float)(d02 + d2), (float)(d03 + d3)).color(ff1, ff2, ff3, ff4).normal(posestack$pose.normal(), f, f1, f2).endVertex();
        });

    }

    public static void renderYellowOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,1f,1f,0f,1f);
    }
    public static void renderRedOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,1f,0f,0f,1f);
    }
    public static void renderWhiteOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,1f,1f,1f,1f);
    }
    public static void renderGreenOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,0f,1f,0f,1f);
    }
    public static void renderCyanOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,0f,1f,0.7f,1f);
    }
    @SubscribeEvent
    public static void RenderGUIEvent(RenderGuiOverlayEvent event) {
        if(event.getOverlay()== VanillaGuiOverlay.HOTBAR.type()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null&&!Minecraft.getInstance().options.hideGui) {
                ItemStack stack = player.getMainHandItem();
                Block heldBlock=Block.byItem(stack.getItem());
                GuiGraphics guiGraphics=event.getGuiGraphics();

                Window window=event.getWindow();
                int width=window.getGuiScaledWidth();
                int height=window.getGuiScaledHeight();
                int centerWidth = width / 2;
                Font font=Minecraft.getInstance().font;
                CompoundTag tag=stack.getTag();
                PoseStack poseStack=guiGraphics.pose();
                MultiBufferSource multiBufferSource=event.getGuiGraphics().bufferSource();


                if(stack.is(Register.vector_card.get())){
                    if((!VectorCardItem.isSelectionFinished(stack)&&!VectorCardItem.isDuringSelection(stack))) {
                        guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_origin_pos").getString(),centerWidth,height-75, 7208704);
                    }else if(VectorCardItem.isDuringSelection(stack)){
                        guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_end_pos").getString(),centerWidth,height-75, 7208704);
                    }else if(VectorCardItem.isSelectionFinished(stack)){
                        guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.vector_card_select_finish").getString(),centerWidth,height-75, 7208704);
                    }
                }else if(stack.is(Register.shape_card.get())){
                       guiGraphics.drawCenteredString(font, Component.translatable("info.ugoblock.shape_card_text").getString(),centerWidth,height-75, 16776960);

                }

            }
        }

    }
}