package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.register.BlockEntityRegister;
import com.iwaliner.ugoblock.register.EntityRegister;
import com.iwaliner.ugoblock.register.ItemAndBlockRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(ModCoreUgoBlock.MODID)
public class ModCoreUgoBlock
{
     public static final String MODID = "ugoblock";

    public ModCoreUgoBlock() {
        IEventBus  modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EntityRegister.register(modEventBus);
        ItemAndBlockRegister.register(modEventBus);
        BlockEntityRegister.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

    }


}
