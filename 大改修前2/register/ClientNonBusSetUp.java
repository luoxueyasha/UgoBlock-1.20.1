package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
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
        LevelRenderer levelRenderer = event.getLevelRenderer();
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


            if (heldStack.getItem() == Register.end_location_card.get()) {
                CompoundTag tag=heldStack.getTag();
                if (tag!=null&&tag.contains("end_location")) {
                    BlockPos endLocation= NbtUtils.readBlockPos(tag.getCompound("end_location"));
                    int range=20;
                    loopA: for(int i=-range;i<=range;i++){
                        for(int j=-range;j<=range;j++){
                            for(int k=-range;k<=range;k++){
                                BlockPos offsetPos=hitPos.offset(i,j,k);
                                if(offsetPos.getX()==endLocation.getX()&&offsetPos.getY()==endLocation.getY()&&offsetPos.getZ()==endLocation.getZ()){
                                    ClientNonBusSetUp.renderRedOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), event.getCamera().getEntity(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, offsetPos);
                                    break loopA;
                                }
                            }
                        }
                    }
                }
                if (tag!=null&&tag.contains("start_location")) {
                    BlockPos startLocation= NbtUtils.readBlockPos(tag.getCompound("start_location"));
                    int range=20;
                    loopA: for(int i=-range;i<=range;i++){
                        for(int j=-range;j<=range;j++){
                            for(int k=-range;k<=range;k++){
                                BlockPos offsetPos=hitPos.offset(i,j,k);
                                if(offsetPos.getX()==startLocation.getX()&&offsetPos.getY()==startLocation.getY()&&offsetPos.getZ()==startLocation.getZ()){
                                    ClientNonBusSetUp.renderGreenOutline(poseStack, multiBufferSource.getBuffer(RenderType.lines()), event.getCamera().getEntity(), event.getCamera().getPosition().x, event.getCamera().getPosition().y, event.getCamera().getPosition().z, offsetPos);
                                    break loopA;
                                }
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
    public static void renderGreenOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_) {
        renderShape(p_109638_, p_109639_, (double)p_109644_.getX() - p_109641_, (double)p_109644_.getY() - p_109642_, (double)p_109644_.getZ() - p_109643_,0f,1f,0f,1f);
    }
}