package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerRenderer;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerScreen;
import com.iwaliner.ugoblock.object.controller.RotationControllerRenderer;
import com.iwaliner.ugoblock.object.controller.SlideControllerRenderer;
import com.iwaliner.ugoblock.object.moving_block.CollisionEntityRenderer;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockRenderer;
import com.iwaliner.ugoblock.object.controller.RotationControllerScreen;
import com.iwaliner.ugoblock.object.controller.SlideControllerScreen;
import com.iwaliner.ugoblock.object.wireless_redstone_receiver.WirelessRedstoneReceiverRenderer;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.PortableWirelessRedstoneTransmitterItem;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.WirelessRedstoneTransmitterRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
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
        event.registerEntityRenderer(Register.CollisionEntity.get(), CollisionEntityRenderer::new);
      //  event.registerEntityRenderer(Register.ControllableEntity.get(), ControllableEntityRenderer::new);
    }

    @Nullable
    @SubscribeEvent
    public static void RegisterRendererEvent(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(Register.SlideController.get(), SlideControllerRenderer::new);
        BlockEntityRenderers.register(Register.RotationController.get(), RotationControllerRenderer::new);
        BlockEntityRenderers.register(Register.WirelessRedstoneTransmitterBlockEntity.get(), WirelessRedstoneTransmitterRenderer::new);
        BlockEntityRenderers.register(Register.WirelessRedstoneReceiverBlockEntity.get(), WirelessRedstoneReceiverRenderer::new);
        BlockEntityRenderers.register(Register.BasketMakerBlockEntity.get(), BasketMakerRenderer::new);
        MenuScreens.register(Register.SlideControllerMenu.get(), SlideControllerScreen::new);
        MenuScreens.register(Register.RotationControllerMenu.get(), RotationControllerScreen::new);
        MenuScreens.register(Register.BasketMakerMenu.get(), BasketMakerScreen::new);
            event.enqueueWork(() -> {
                ItemProperties.register(Register.portable_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color1"), (itemStack, clientWorld, livingEntity, i) -> PortableWirelessRedstoneTransmitterItem.getColor1(itemStack).getId());
                ItemProperties.register(Register.portable_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color2"), (itemStack, clientWorld, livingEntity, i) -> PortableWirelessRedstoneTransmitterItem.getColor2(itemStack).getId());
                ItemProperties.register(Register.portable_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color3"), (itemStack, clientWorld, livingEntity, i) -> PortableWirelessRedstoneTransmitterItem.getColor3(itemStack).getId());
                ItemProperties.register(Register.portable_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "powered"), (itemStack, clientWorld, livingEntity, i) -> PortableWirelessRedstoneTransmitterItem.isPowered(itemStack)? 1 : 0);

            });

         }
    @SubscribeEvent
    public static void registerItemColorEvent(RegisterColorHandlersEvent.Item event) {
        event.register(ClientSetUp::getColorPortableWirelessRedstoneTransmitter,Register.portable_wireless_redstone_transmitter.get());
    }
    private static int getColorPortableWirelessRedstoneTransmitter(ItemStack stack,int index){
        if(stack.is(Register.portable_wireless_redstone_transmitter.get())){
            int color1 =PortableWirelessRedstoneTransmitterItem.isColor1Null(stack)? -1 : PortableWirelessRedstoneTransmitterItem.getColor1(stack).getId();
            int color2 =PortableWirelessRedstoneTransmitterItem.isColor2Null(stack)? -1 : PortableWirelessRedstoneTransmitterItem.getColor2(stack).getId();
            int color3 =PortableWirelessRedstoneTransmitterItem.isColor3Null(stack)? -1 : PortableWirelessRedstoneTransmitterItem.getColor3(stack).getId();
            if(index==0){
               return getColor(color1);
            }else if(index==1){
                return getColor(color2);
            }else if(index==2){
                return getColor(color3);
            }
        }
        return 2378057;
    }
    private static int getColor( int i){
        if(i==-1){
            return 2378057;
        }
        switch (DyeColor.byId(i)){
            case WHITE : return 16448250;
            case BLACK:  return 0;
            case BLUE : return 3883174;
            case BROWN : return 8278063;
            case CYAN : return 1480344;
            case GRAY : return 4541519;
            case GREEN : return 6060054;
            case LIGHT_BLUE : return 4834020;
            case LIGHT_GRAY : return 10066323;
            case LIME : return 8176411;
            case MAGENTA : return 13259457;
            case ORANGE : return 16351261;
            case PINK: return 16033728;
            case PURPLE : return 8794293;
            case RED : return 11283492;
            case YELLOW : return 16634933;
        }
        return 2378057;
    }
}