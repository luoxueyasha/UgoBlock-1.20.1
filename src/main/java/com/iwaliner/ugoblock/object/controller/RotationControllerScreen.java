package com.iwaliner.ugoblock.object.controller;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class RotationControllerScreen extends AbstractContainerScreen<RotationControllerMenu>{
    private final ResourceLocation texture;
    public RotationControllerScreen(RotationControllerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.texture = new ResourceLocation("ugoblock:textures/gui/rotation_controller.png");
    }
    public void init() {
        super.init();
       this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }
    @Override
    public boolean mouseScrolled(double x, double y, double scrollY) {
        /**scrollYは、マウスのホイールを下に回したときに負、上に回したときに正。*/
        if(scrollY>0) {
            if (this.menu.clickMenuButton(this.minecraft.player, 3)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 3);
            }
        }else{
            if (this.menu.clickMenuButton(this.minecraft.player, 0)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
            }
        }
        return super.mouseScrolled(x, y, scrollY);
    }

    public boolean mouseClicked(double x, double y, int ii) {
        if(isButtonA(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 9)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 9);
                return true;
            }
        }else if(isButtonB(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 1)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 1);
                return true;
            }
        }else if(isButtonC(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 2)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 2);
                return true;
            }
        }else if(isButtonD(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 8)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 8);
                return true;
            }
        }else if(isButtonE(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 4)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 4);
                return true;
            }
        }else if(isButtonF(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 5)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 5);
                return true;
            }
        }else if(isButtonG(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 6)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 6);
                return true;
            }
        }else if(isButtonH(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 7)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 7);
                return true;
            }
        }else if(isButtonCollisionShape(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 10)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 10);
                return true;
            }
        }
        return super.mouseClicked(x, y, ii);
    }
    @Override
    public boolean isMouseOver(double x, double y) {
       return isButtonA(x,y)||isButtonB(x,y)||isButtonC(x,y)||isButtonD(x,y)||isButtonE(x,y)||isButtonF(x,y)||isButtonG(x,y)||isButtonH(x,y);
    }
    private boolean isButtonA(double x, double y) {
            return x < this.leftPos + 76 && x > this.leftPos + 53 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonB(double x, double y) {
        return x < this.leftPos + 89 && x > this.leftPos + 76 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonC(double x, double y) {
        return x < this.leftPos + 133 && x > this.leftPos + 120 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonD(double x, double y) {
        return x < this.leftPos + 156 && x > this.leftPos + 133 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonE(double x, double y) {
        return x < this.leftPos + 76 && x > this.leftPos + 53 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonF(double x, double y) {
        return x < this.leftPos + 89 && x > this.leftPos + 76 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonG(double x, double y) {
        return x < this.leftPos + 133 && x > this.leftPos + 120 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonH(double x, double y) {
        return x < this.leftPos + 156 && x > this.leftPos + 133 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonCollisionShape(double x, double y) {
        return x < this.leftPos + 139&& x > this.leftPos + 79 && y > this.topPos +71&& y <this.topPos +84;
    }
    public void render(GuiGraphics p_282573_, int p_97859_, int p_97860_, float p_97861_) {
        this.renderBackground(p_282573_);
        this.renderBg(p_282573_, p_97861_, p_97859_, p_97860_);
        super.render(p_282573_, p_97859_, p_97860_, p_97861_);
        this.renderTooltip(p_282573_, p_97859_, p_97860_);
    }
    protected void renderBg(GuiGraphics p_282928_, float p_281631_, int p_281252_, int p_281891_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);
        int i = this.leftPos;
        int j = this.topPos;
        p_282928_.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
        Component mouse=Component.literal(" \uD83D\uDDB1↕").withStyle(ChatFormatting.BLUE);
        guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.rotation_controller_angle").append(mouse), 105, 17, 8587492);
        guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.rotation_controller_duration"), 105, 48, 8587492);
        if(getMenu().getDegreeAngle()==181){
            guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.rotation_controller_loop_counter_clockwise"), 105, 30, 16777215);
        }else if(getMenu().getDegreeAngle()==-181){
            guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.rotation_controller_loop_clockwise"), 105, 30, 16777215);
        }else{
            guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(getMenu().getDegreeAngle())+"°"), 105, 30, 16777215);
        }
        guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(getMenu().getDurationSecond())), 105, 61, 16777215);
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 16766976, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 16766976, false);
        if (menu.hasCollisionShape()){
            guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.has_collision_shape"), 109, 74, 16766976);
        }else{
            guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.not_has_collision_shape"), 109, 74, 16766976);
        }
    }
}
