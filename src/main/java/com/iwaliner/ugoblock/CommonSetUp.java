package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.network.Network;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetUp {

    @SubscribeEvent
    public static void CommonSetUpEvent(FMLCommonSetupEvent event) {
        Network.register();
    }
}