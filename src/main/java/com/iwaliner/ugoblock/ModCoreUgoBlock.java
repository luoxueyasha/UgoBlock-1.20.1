package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.network.WirelessRedstoneData;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.object.block_imitation_wand.BlockImitationWandDecoration;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.PortableWirelessRedstoneTransmitterItem;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ModCoreUgoBlock.MODID)
public class ModCoreUgoBlock
{
     public static final String MODID = "ugoblock";
    public static Logger logger = LogManager.getLogger("ugoblock");
    public ModCoreUgoBlock() {
        IEventBus  modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Register.register(modEventBus);
       MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::CreativeTabEvent);
        modEventBus.addListener(this::ItemDecorationRegisterEvent);
    }

    @SubscribeEvent
    public void CreativeTabEvent(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Register.slide_controller_blockitem.get());
            event.accept(Register.rotation_controller_blockitem.get());
            event.accept(Register.wireless_redstone_transmitter_blockitem.get());
            event.accept(Register.wireless_redstone_receiver_blockitem.get());
            event.accept(Register.portable_wireless_redstone_transmitter.get());
            event.accept(Register.block_imitation_wand.get());
        }else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Register.smooth_crying_obsidian_blockitem.get());
            event.accept(Register.ender_infused_smooth_crying_obsidian_blockitem.get());
        }
    }
    @SubscribeEvent
    public void ItemDecorationRegisterEvent(RegisterItemDecorationsEvent event) {
        event.register(Register.block_imitation_wand.get(),new BlockImitationWandDecoration());
    }
    @SubscribeEvent
    public void ItemStackedOnOtherEvent(ItemStackedOnOtherEvent event){
        ItemStack transmitterStack=event.getCarriedItem();
        ItemStack dyeStack=event.getStackedOnItem();
        if(dyeStack.getItem()instanceof DyeItem&&transmitterStack.is(Register.portable_wireless_redstone_transmitter.get())) {
            DyeColor dyeColor=((DyeItem) dyeStack.getItem()).getDyeColor();
            if (PortableWirelessRedstoneTransmitterItem.isColor1Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableWirelessRedstoneTransmitterItem.setColor1(transmitterStack,dyeColor);
                event.setCanceled(true);
            }else if (PortableWirelessRedstoneTransmitterItem.isColor2Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableWirelessRedstoneTransmitterItem.setColor2(transmitterStack,dyeColor);
                event.setCanceled(true);
            }else if (PortableWirelessRedstoneTransmitterItem.isColor3Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableWirelessRedstoneTransmitterItem.setColor3(transmitterStack,dyeColor);
                event.getPlayer().level().getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    boolean alreadyExist=!data.isSignalNull(PortableWirelessRedstoneTransmitterItem.getColor1(transmitterStack),PortableWirelessRedstoneTransmitterItem.getColor2(transmitterStack),PortableWirelessRedstoneTransmitterItem.getColor3(transmitterStack));
                    if(alreadyExist&&event.getPlayer().level().isClientSide){
                        event.getPlayer().displayClientMessage(Component.translatable("info.ugoblock.frequency_already_exists_color_portable").withStyle(ChatFormatting.RED), false);
                    }
                });
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void ItemTooltipEvent(ItemTooltipEvent event) {
            CompoundTag tag=event.getItemStack().getTag();
    }
    @SubscribeEvent
    public void BreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        Item item=event.getEntity().getMainHandItem().getItem();
        if(item== Register.end_location_card.get()||item== Register.shape_card.get()){
            event.setNewSpeed(10000f);
        }
    }

    @SubscribeEvent
    public void AttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
            if(!event.getObject().getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).isPresent()) {
                event.addCapability(new ResourceLocation(MODID, "wireless_redstone_properties"), new WirelessRedstoneProvider());
            }
    }
    @SubscribeEvent
    public void RegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(WirelessRedstoneData.class);
    }

}
