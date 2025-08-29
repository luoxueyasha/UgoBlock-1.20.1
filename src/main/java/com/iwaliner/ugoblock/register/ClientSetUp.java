package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerRenderer;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerScreen;
import com.iwaliner.ugoblock.object.controller.RotationControllerRenderer;
import com.iwaliner.ugoblock.object.controller.SlideControllerRenderer;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockRenderer;
import com.iwaliner.ugoblock.object.seat.InvisibleEntityRenderer;
import com.iwaliner.ugoblock.object.controller.RotationControllerScreen;
import com.iwaliner.ugoblock.object.controller.SlideControllerScreen;
import com.iwaliner.ugoblock.object.wireless_redstone_receiver.WirelessRedstoneReceiverRenderer;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.PortableAlternateWirelessRedstoneTransmitterItem;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.WirelessRedstoneTransmitterRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
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
        event.registerEntityRenderer(Register.CollisionEntity.get(), InvisibleEntityRenderer::new);
        event.registerEntityRenderer(Register.SeatEntity.get(), InvisibleEntityRenderer::new);
        event.registerEntityRenderer(Register.GravitateBlock.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(Register.DoorEntity.get(), InvisibleEntityRenderer::new);
        event.registerEntityRenderer(Register.StandingSeatEntity.get(), InvisibleEntityRenderer::new);
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
                ItemProperties.register(Register.portable_alternate_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color1"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor1(itemStack).getId());
                ItemProperties.register(Register.portable_alternate_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color2"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor2(itemStack).getId());
                ItemProperties.register(Register.portable_alternate_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color3"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor3(itemStack).getId());
                ItemProperties.register(Register.portable_alternate_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "powered"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.isPowered(itemStack)? 1 : 0);

                ItemProperties.register(Register.portable_momentary_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color1"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor1(itemStack).getId());
                ItemProperties.register(Register.portable_momentary_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color2"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor2(itemStack).getId());
                ItemProperties.register(Register.portable_momentary_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "color3"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.getColor3(itemStack).getId());
                ItemProperties.register(Register.portable_momentary_wireless_redstone_transmitter.get(), new ResourceLocation(ModCoreUgoBlock.MODID, "powered"), (itemStack, clientWorld, livingEntity, i) -> PortableAlternateWirelessRedstoneTransmitterItem.isPowered(itemStack)? 1 : 0);
            });
         }
    @SubscribeEvent
    public static void registerItemColorEvent(RegisterColorHandlersEvent.Item event) {
        event.register(ClientSetUp::getColorPortableWirelessRedstoneTransmitter,Register.portable_alternate_wireless_redstone_transmitter.get());
        event.register(ClientSetUp::getColorPortableWirelessRedstoneTransmitter,Register.portable_momentary_wireless_redstone_transmitter.get());
    }
    // @debug
    private static final int NO_COLOR = 2378057;
    private static int getColorPortableWirelessRedstoneTransmitter(ItemStack stack,int index){
        if(stack == null){
            return NO_COLOR;
        }
        if(index < 0 || index > 2){
            return NO_COLOR;
        }
        if(!stack.is(Register.portable_alternate_wireless_redstone_transmitter.get()) && !stack.is(Register.portable_momentary_wireless_redstone_transmitter.get())){
            return NO_COLOR;
        }

        // case index == 0,1,2:
        boolean isColor1Null = PortableAlternateWirelessRedstoneTransmitterItem.isColor1Null(stack);
        // if index 0 is null, then 1 and 2 are also null.
        if(isColor1Null){
            return NO_COLOR;
        }
        CompoundTag stackTag = Utils.getCompoundTagOrNewTag(stack);
        if(index == 0){
            return getColor(PortableAlternateWirelessRedstoneTransmitterItem.getColor1(stackTag).getId());
        }

        // case index == 1,2:
        boolean isColor2Null = PortableAlternateWirelessRedstoneTransmitterItem.isColor2Null(stack);
        // if index 1 is null, then 2 is also null.
        if(isColor2Null){
            return NO_COLOR;
        }

        if(index == 1){
            return getColor(PortableAlternateWirelessRedstoneTransmitterItem.getColor2(stackTag).getId());
        }

        // case index == 2:
        if (PortableAlternateWirelessRedstoneTransmitterItem.isColor3Null(stack)) {
            return NO_COLOR;
        }
        return getColor(PortableAlternateWirelessRedstoneTransmitterItem.getColor3(stackTag).getId());

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