package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.network.Network;
import com.iwaliner.ugoblock.network.ServerBoundSlideControllerPacket;
import com.iwaliner.ugoblock.network.SlideControllerPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class SlideControllerScreen extends AbstractContainerScreen<SlideControllerMenu>{

    private final ResourceLocation texture;
//    private EditBox startTimeEdit;
//    private EditBox durationEdit;
    public SlideControllerScreen(SlideControllerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.texture = new ResourceLocation("ugoblock:textures/gui/slide_controller.png");

    }
    public void init() {
        super.init();
        int startTime = this.getMenu().getStartTime();
        int duration = this.getMenu().getDuration();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
      /*  this.startTimeEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, Component.translatable("structure_block.size.y"));
        this.startTimeEdit.setMaxLength(15);
        this.startTimeEdit.setValue(Integer.toString(startTime));
        this.addWidget(this.startTimeEdit);
        this.durationEdit = new EditBox(this.font, this.width / 2 + 8, 80, 80, 20, Component.translatable("structure_block.position.z"));
        this.durationEdit.setMaxLength(15);
        this.durationEdit.setValue(Integer.toString(duration));
        this.addWidget(this.durationEdit);*/

    }

   /* @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        if (getMenu().container instanceof SlideControllerBlockEntity blockEntity) {

            //this.minecraft.getConnection().send(new ServerBoundSlideControllerPacket(blockEntity.getBlockPos(), getMenu().getStartTime(), getMenu().getDuration()));
            Network.sendToServer(new SlideControllerPacket(blockEntity.getBlockPos(), getMenu().getStartTime(), getMenu().getDuration()));
        }
        return super.keyPressed(p_97765_, p_97766_, p_97767_);
    }*/
    public boolean mouseClicked(double x, double y, int ii) {
        if(isButtonA(x,y)) {
            if (this.menu.clickMenuButton(this.minecraft.player, 0)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
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
            if (this.menu.clickMenuButton(this.minecraft.player, 3)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 3);
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
        }
        return super.mouseClicked(x, y, ii);
    }
    @Override
    public boolean isMouseOver(double x, double y) {
       return isButtonA(x,y)||isButtonB(x,y)||isButtonC(x,y)||isButtonD(x,y)||isButtonE(x,y)||isButtonF(x,y)||isButtonG(x,y)||isButtonH(x,y);
    }
    private boolean isButtonA(double x, double y) {
            return x < this.leftPos + 86 && x > this.leftPos + 63 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonB(double x, double y) {
        return x < this.leftPos + 99 && x > this.leftPos + 86 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonC(double x, double y) {
        return x < this.leftPos + 133 && x > this.leftPos + 120 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonD(double x, double y) {
        return x < this.leftPos + 156 && x > this.leftPos + 133 && y > this.topPos +26 && y < this.topPos + 41;
    }
    private boolean isButtonE(double x, double y) {
        return x < this.leftPos + 86 && x > this.leftPos + 63 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonF(double x, double y) {
        return x < this.leftPos + 99 && x > this.leftPos + 86 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonG(double x, double y) {
        return x < this.leftPos + 133 && x > this.leftPos + 120 && y > this.topPos +57 && y < this.topPos + 72;
    }
    private boolean isButtonH(double x, double y) {
        return x < this.leftPos + 156 && x > this.leftPos + 133 && y > this.topPos +57 && y < this.topPos + 72;
    }

    /*public void resize(Minecraft p_99411_, int p_99412_, int p_99413_) {
        String s = this.startTimeEdit.getValue();
        String s1 = this.durationEdit.getValue();
        this.init(p_99411_, p_99412_, p_99413_);
        this.startTimeEdit.setValue(s);
        this.durationEdit.setValue(s1);
    }
    @Override
    protected void containerTick() {
        super.containerTick();
        this.startTimeEdit.tick();
        this.durationEdit.tick();
        boolean isStartTimeDigit=true;
        for (int i = 0; i < startTimeEdit.getValue().length(); i++) {
            isStartTimeDigit = Character.isDigit(startTimeEdit.getValue().charAt(i));
            if (!isStartTimeDigit) {
                break;
            }
        }
        boolean isDurationDigit=true;
        for (int i = 0; i < durationEdit.getValue().length(); i++) {
            isDurationDigit = Character.isDigit(durationEdit.getValue().charAt(i));
            if (!isDurationDigit) {
                break;
            }
        }
        if(isStartTimeDigit&& !startTimeEdit.getValue().isEmpty()) {
            this.getMenu().setStartTime(Integer.valueOf(startTimeEdit.getValue()).intValue());
        }
        if(isDurationDigit&& !durationEdit.getValue().isEmpty()) {
            this.getMenu().setDuration(Integer.valueOf(durationEdit.getValue()).intValue());
        }
    }*/

    public void render(GuiGraphics p_282573_, int p_97859_, int p_97860_, float p_97861_) {
        this.renderBackground(p_282573_);
        this.renderBg(p_282573_, p_97861_, p_97859_, p_97860_);
        super.render(p_282573_, p_97859_, p_97860_, p_97861_);
        this.renderTooltip(p_282573_, p_97859_, p_97860_);
//        this.startTimeEdit.render(p_282573_, p_97859_, p_97860_, p_97861_);
//        this.durationEdit.render(p_282573_, p_97859_, p_97860_, p_97861_);
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
        guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.slide_controller_start_time"), 110, 17, 9145227);
        guiGraphics.drawCenteredString(this.font, Component.translatable("info.ugoblock.slide_controller_duration"), 110, 48, 9145227);
        //if (getMenu().container instanceof SlideControllerBlockEntity blockEntity) {
       //     guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(Mth.floor((double) blockEntity.getStartTime()/20D))), 110, 30, 16777215);
      //      guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(Mth.floor((double) blockEntity.getDuration()/20D))), 110, 61, 16777215);
      //  }
        guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(getMenu().getStartTime())), 110, 30, 16777215);
              guiGraphics.drawCenteredString(this.font, Component.literal(String.valueOf(getMenu().getDuration())), 110, 61, 16777215);

        super.renderLabels(guiGraphics,i,j);
    }

  /*  protected void slotClicked(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
        super.slotClicked(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
        if (getMenu().container instanceof SlideControllerBlockEntity blockEntity) {

            //this.minecraft.getConnection().send(new ServerBoundSlideControllerPacket(blockEntity.getBlockPos(), getMenu().getStartTime(), getMenu().getDuration()));
            Network.sendToServer(new SlideControllerPacket(blockEntity.getBlockPos(), getMenu().getStartTime(), getMenu().getDuration()));
        }
    }
*/


}
