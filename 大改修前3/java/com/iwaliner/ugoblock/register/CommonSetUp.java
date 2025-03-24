package com.iwaliner.ugoblock.register;


import com.iwaliner.ugoblock.ModCoreUgoBlock;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ModCoreUgoBlock.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetUp {
    @SubscribeEvent
    public static void CommonSetUpEvent(FMLCommonSetupEvent event) {
     }
        @SubscribeEvent
    public static void MobAttributesEvent(EntityAttributeCreationEvent entityRegisterEvent) {


      /*  entityRegisterEvent.put(Register.ControllableEntity.get(),
                ControllableEntity.createAttributes()
                        .add(Attributes.MAX_HEALTH, 20.0D)
                        .add(Attributes.ATTACK_DAMAGE, 6.0D)
                        .add(Attributes.ATTACK_SPEED, 0.5D)
                        .add(Attributes.FLYING_SPEED, 1.0D)
                        .add(Attributes.MOVEMENT_SPEED, 0.2D)
                        .build());*/

    }

}