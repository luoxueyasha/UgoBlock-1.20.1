package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.object.moving_block.MovingBlockRenderer;
import com.iwaliner.ugoblock.object.rotation_controller.RotationControllerScreen;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerScreen;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetUp {
    @SubscribeEvent
    public static void RegisterEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Register.MoveableBlock.get(), MovingBlockRenderer::new);
    }
    @Nullable
    @SubscribeEvent
    public static void RegisterRendererEvent(FMLClientSetupEvent event) {
        /**コンテナにGUIを登録*/
        MenuScreens.register(Register.SlideControllerMenu.get(), SlideControllerScreen::new);
        MenuScreens.register(Register.RotationControllerMenu.get(), RotationControllerScreen::new);

         }
}