package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.network.WirelessRedstoneData;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.register.Register;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.phys.shapes.Shapes.box;


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
    }
    @SubscribeEvent
    public void CreativeTabEvent(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Register.slide_controller_blockitem.get());
            event.accept(Register.rotation_controller_blockitem.get());
            event.accept(Register.wireless_redstone_transmitter_blockitem.get());
            event.accept(Register.wireless_redstone_receiver_blockitem.get());
        }else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Register.smooth_crying_obsidian_blockitem.get());
            event.accept(Register.ender_infused_smooth_crying_obsidian_blockitem.get());
        }
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
