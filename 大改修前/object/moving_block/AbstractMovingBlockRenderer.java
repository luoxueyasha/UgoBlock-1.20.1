package com.iwaliner.ugoblock.object.moving_block;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.util.List;


@OnlyIn(Dist.CLIENT)
    public class AbstractMovingBlockRenderer<T extends Display, S> extends EntityRenderer<T> {
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private final BlockRenderDispatcher blockRenderer;

    protected AbstractMovingBlockRenderer(EntityRendererProvider.Context context) {
            super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();

        ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
            this.bottom = modelpart.getChild("bottom");
            this.lid = modelpart.getChild("lid");
            this.lock = modelpart.getChild("lock");
            ModelPart modelpart1 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
            this.doubleLeftBottom = modelpart1.getChild("bottom");
            this.doubleLeftLid = modelpart1.getChild("lid");
            this.doubleLeftLock = modelpart1.getChild("lock");
            ModelPart modelpart2 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
            this.doubleRightBottom = modelpart2.getChild("bottom");
            this.doubleRightLid = modelpart2.getChild("lid");
            this.doubleRightLock = modelpart2.getChild("lock");
        }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
      return   TextureAtlas.LOCATION_BLOCKS;
    }

    @Nullable
        protected Display.BlockDisplay.BlockRenderState getSubState(MovingBlockEntity p_277721_) {
            return p_277721_.blockRenderState();
        }

    private static float cameraYrot(Camera p_299213_) {
        return p_299213_.getYRot() - 180.0F;
    }

    private static float cameraXRot(Camera p_297923_) {
        return -p_297923_.getXRot();
    }

    private static <T extends Display> float entityYRot(T p_297849_, float p_297686_) {
        return Mth.rotLerp(p_297686_, p_297849_.yRotO, p_297849_.getYRot());
    }

    private static <T extends Display> float entityXRot(T p_298651_, float p_297691_) {
        return Mth.lerp(p_297691_, p_298651_.xRotO, p_298651_.getXRot());
    }
    private Quaternionf calculateOrientation(Display.RenderState p_277846_, MovingBlockEntity p_271013_, float p_298568_, Quaternionf p_298476_) {
        Camera camera = this.entityRenderDispatcher.camera;
        Quaternionf quaternionf;
        switch (p_277846_.billboardConstraints()) {
            case FIXED:
                quaternionf = p_298476_.rotationYXZ(-0.017453292F * entityYRot(p_271013_, p_298568_), ((float)Math.PI / 180F) * entityXRot(p_271013_, p_298568_), 0.0F);
                break;
            case HORIZONTAL:
                quaternionf = p_298476_.rotationYXZ(-0.017453292F * entityYRot(p_271013_, p_298568_), ((float)Math.PI / 180F) * cameraXRot(camera), 0.0F);
                break;
            case VERTICAL:
                quaternionf = p_298476_.rotationYXZ(-0.017453292F * cameraYrot(camera), ((float)Math.PI / 180F) * entityXRot(p_271013_, p_298568_), 0.0F);
                break;
            case CENTER:
                quaternionf = p_298476_.rotationYXZ(-0.017453292F * cameraYrot(camera), ((float)Math.PI / 180F) * cameraXRot(camera), 0.0F);
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return quaternionf;
    }

    public void render(T movingBlock, float f1, float f2, PoseStack poseStack, MultiBufferSource bufferSource, int i0) {
        //
        Display.RenderState display$renderstate = movingBlock.renderState();
        if (display$renderstate != null) {
            MovingBlockEntity.BlockRenderState s = this.getSubState((MovingBlockEntity) movingBlock);
            if (s != null) {
                float f = movingBlock.calculateInterpolationProgress(f2);
                this.shadowRadius = display$renderstate.shadowRadius().get(f);
                this.shadowStrength = display$renderstate.shadowStrength().get(f);
                int i = display$renderstate.brightnessOverride();
                int j = i != -1 ? i : i0;
                super.render(movingBlock, f1, f2, poseStack, bufferSource, i0);
                poseStack.pushPose();
                poseStack.mulPose(this.calculateOrientation(display$renderstate, (MovingBlockEntity) movingBlock, f2, new Quaternionf()));
                Transformation transformation = display$renderstate.transformation().get(f);
                poseStack.mulPoseMatrix(transformation.getMatrix());
                poseStack.last().normal().rotate(transformation.getLeftRotation()).rotate(transformation.getRightRotation());
                this.renderInner( (MovingBlockEntity)movingBlock,(MovingBlockEntity.BlockRenderState) s, poseStack, bufferSource, j, f);
                poseStack.popPose();
            }
        }
    }

    private static Matrix4f compose(@Nullable Vector3f p_254465_, @Nullable Quaternionf p_254416_, @Nullable Vector3f p_254499_, @Nullable Quaternionf p_254334_) {
        Matrix4f matrix4f = new Matrix4f();
        if (p_254465_ != null) {
            matrix4f.translation(p_254465_);
        }

        if (p_254416_ != null) {
            matrix4f.rotate(p_254416_);
        }

        if (p_254499_ != null) {
            matrix4f.scale(p_254499_);
        }

        if (p_254334_ != null) {
            matrix4f.rotate(p_254334_);
        }

        return matrix4f;
    }
    public void renderInner(MovingBlockEntity movingBlock, MovingBlockEntity.BlockRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i0, float f0) {
            List<BlockPos> posList=movingBlock.getPosList();
            List<BlockState> stateList=movingBlock.getStateList();
            Level level=movingBlock.level();
       /* if(movingBlock.isLoopRotation()){
            *//*Transformation transformation = movingBlock.renderState().transformation().get(f0);
            Quaternionf leftRotation=new Quaternionf(new AxisAngle4d(Mth.PI, 0D, 1D, 0D));
            Quaternionf rightRotation=new Quaternionf(new AxisAngle4d(0, 0D, 1D, 0D));
            poseStack.mulPoseMatrix(compose(new Vector3f(0f,1f,1f),leftRotation,transformation.getScale(),rightRotation));
            poseStack.last().normal().rotate(leftRotation).rotate(rightRotation);*//*


            float f=movingBlock.tickCount;
            float f1 = -10.0F - movingBlock.getXRot();
            float f2 = Mth.lerp(f, 0.0F, f1);
            poseStack.rotateAround(Axis.XP.rotationDegrees(f), 0.0F, movingBlock.getBbHeight() / 2.0F, 0.0F);
        }*/
            poseStack.mulPose(Axis.XP.rotationDegrees((float) movingBlock.getVisualXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) movingBlock.getVisualYRot()));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) movingBlock.getVisualZRot()));
            for(int i=0;i<posList.size();i++){
               BlockPos eachPos=posList.get(i);
              // BlockPos pos=movingBlock.blockPosition().offset(eachPos.getX(),eachPos.getY(),eachPos.getZ());
               BlockState eachState=stateList.get(i);
               Block block=eachState.getBlock();


                if(!stateList.get(i).isAir()) {
                   /* BlockState downState=posList.contains(eachPos.below())?  stateList.get(posList.indexOf(eachPos.below())) : null;
                    BlockState upState=posList.contains(eachPos.above())?  stateList.get(posList.indexOf(eachPos.above())) : null;
                    BlockState northState=posList.contains(eachPos.north())?  stateList.get(posList.indexOf(eachPos.north())) : null;
                    BlockState southState=posList.contains(eachPos.south())?  stateList.get(posList.indexOf(eachPos.south())) : null;
                    BlockState westState=posList.contains(eachPos.west())?  stateList.get(posList.indexOf(eachPos.west())) : null;
                    BlockState eastState=posList.contains(eachPos.east())?  stateList.get(posList.indexOf(eachPos.east())) : null;
                 */   poseStack.pushPose();

                    poseStack.translate(eachPos.getX(), eachPos.getY(),eachPos.getZ());
                     /*if (block instanceof ChestBlock abstractchestblock) {

                        boolean flag = level!= null;
                        ChestType chesttype = eachState.hasProperty(ChestBlock.TYPE) ? eachState.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
                        boolean flag1 = chesttype != ChestType.SINGLE;
                        LidBlockEntity chestBlockEntity=new ChestBlockEntity(pos,eachState);


                        float f = eachState.getValue(ChestBlock.FACING).toYRot();
                        poseStack.translate(0.5F, 0.5F, 0.5F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(-f));
                        poseStack.translate(-0.5F, -0.5F, -0.5F);
                        DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> neighborcombineresult;
                        if (flag) {
                            neighborcombineresult = abstractchestblock.combine(eachState, level, pos, true);
                        } else {
                            neighborcombineresult = DoubleBlockCombiner.Combiner::acceptNone;
                        }

                        float f1 = neighborcombineresult.apply(ChestBlock.opennessCombiner(chestBlockEntity)).get(f0);

                        f1 = 1.0F - f1;
                        f1 = 1.0F - f1 * f1 * f1;
                           //  int i = blockDisplay.renderState().glowColorOverride();

                            //      int i = neighborcombineresult.apply(new BrightnessCombiner<>()).applyAsInt(i0);
                            //   int i = neighborcombineresult.apply(new BrightnessCombiner<>()).applyAsInt(-1);
                            Material material = this.getMaterial((BlockEntity) chestBlockEntity, chesttype);
                            VertexConsumer vertexconsumer = material.buffer(multiBufferSource, RenderType::entityCutout);
                            int ii = 0;
                            //  i=0; //色
                            //   int i=15;
                            //    ModCoreUgoBlock.logger.info("色;"+i);
                            //    i*=2;
                        int iii = Minecraft.getInstance().getBlockColors().getColor(eachState, (BlockAndTintGetter)null, pos, 0);
                        float ff = movingBlock.calculateInterpolationProgress(f0);
                            this.shadowRadius = movingBlock.renderState().shadowRadius().get(ff);
                            this.shadowStrength = movingBlock.renderState().shadowStrength().get(ff);
                            if (flag1) {
                                if (chesttype == ChestType.LEFT) {
                                    this.render(poseStack, vertexconsumer, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i0, ii);
                                } else {
                                    this.render(poseStack, vertexconsumer, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i0, ii);
                                }
                            } else {
                                this.render(poseStack, vertexconsumer, this.lid, this.lock, this.bottom, f1, i0, ii);
                            }

                            poseStack.scale(0F, 0F, 0F);



                    }*/
                    /*if (block instanceof ChestBlock || block instanceof EnderChestBlock || (block instanceof BedBlock)) {
                        //  poseStack.pushPose();
                        Direction direction = eachState.getValue(HorizontalDirectionalBlock.FACING);
                        Direction direction1 = Direction.from2DDataValue((direction.get2DDataValue()) % 4);
                        float f = -direction1.toYRot();
                        poseStack.mulPose(Axis.YP.rotationDegrees(f));
                        if (direction == Direction.EAST) {
                            poseStack.translate(-1D, 0D, 0D);
                        } else if (direction == Direction.NORTH) {
                            poseStack.translate(-1D, 0D, -1D);
                        } else if (direction == Direction.WEST) {
                            poseStack.translate(0D, 0D, -1D);
                        }
                    }*/
                    /*if(movingBlock.shouldFixFighting()) {
                        poseStack.translate(0.005F, 0.005F, 0.005F);
                        poseStack.scale(0.99F, 0.99F, 0.99F);

                    }*/
                    if (!(block instanceof BedBlock && eachState.getValue(BedBlock.PART) == BedPart.FOOT)) {
                       /* if (movingBlock.renderDown == null || movingBlock.renderDown.isEmpty()) {
                            this.blockRenderer.renderSingleBlock(eachState, poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);

                        } else {
                            Utils.renderSingleBlock(eachState, poseStack, multiBufferSource, i0,movingBlock.renderDown.get(i) ,movingBlock.renderUp.get(i), movingBlock.renderNorth.get(i), movingBlock.renderSouth.get(i), movingBlock.renderWest.get(i),movingBlock.renderEast.get(i));
                            //   Utils.renderSingleBlock(eachState, poseStack, multiBufferSource, i0, shouldRender(downState),shouldRender(upState), shouldRender(northState) ,shouldRender(southState),shouldRender(westState),shouldRender(eastState));
                        }*/

                        poseStack.translate(-0.4999D,-0.4999D,-0.4999D);

                        this.blockRenderer.renderSingleBlock(eachState, poseStack, multiBufferSource, i0, OverlayTexture.NO_OVERLAY);

                    }
                    poseStack.popPose();
                }
            }
        }
        private boolean shouldRender(BlockState state){
            return state==null||(state.getShape(null,null)!= Shapes.block())||!Utils.isBlockSolid(state);
        }
    protected Material getMaterial(BlockEntity blockEntity, ChestType chestType) {
        return Sheets.chooseMaterial(blockEntity, chestType, false);
    }
    private void render(PoseStack p_112370_, VertexConsumer p_112371_, ModelPart p_112372_, ModelPart p_112373_, ModelPart p_112374_, float p_112375_, int p_112376_, int p_112377_) {
        p_112372_.xRot = -(p_112375_ * ((float)Math.PI / 2F));
        p_112373_.xRot = p_112372_.xRot;
        p_112372_.render(p_112370_, p_112371_, p_112376_, p_112377_);
        p_112373_.render(p_112370_, p_112371_, p_112376_, p_112377_);
        p_112374_.render(p_112370_, p_112371_, p_112376_, p_112377_);
    }
    }

