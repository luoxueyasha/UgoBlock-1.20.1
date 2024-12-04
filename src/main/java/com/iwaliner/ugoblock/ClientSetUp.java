package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.object.MoveableBlockRenderer;
import com.iwaliner.ugoblock.object.SlideControllerScreen;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetUp {
    @SubscribeEvent
    public static void RegisterEntityRendererEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Register.MoveableBlock.get(), MoveableBlockRenderer::new);
    }
    @Nullable
    @SubscribeEvent
    public static void RegisterRendererEvent(FMLClientSetupEvent event) {
        /**コンテナにGUIを登録*/
        MenuScreens.register(Register.SlideControllerMenu.get(), SlideControllerScreen::new);

         }
}