package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.MoveableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> Entities = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModCoreUgoBlock.MODID);
    public static final RegistryObject<EntityType<MoveableBlockEntity>> MoveableBlock=Entities.register("moveable_block", () -> EntityType.Builder.<MoveableBlockEntity>of(MoveableBlockEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(1).build(new ResourceLocation(ModCoreUgoBlock.MODID,"moveable_block").toString()));
    public static void register(IEventBus eventBus) {
        Entities.register(eventBus);
    }
}
