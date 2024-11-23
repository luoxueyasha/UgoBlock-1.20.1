package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.SlideControllerBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class ItemAndBlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModCoreUgoBlock.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModCoreUgoBlock.MODID);
    public static final RegistryObject<Block> slide_controller_block =BLOCKS.register("slide_controller_block",() -> {return new SlideControllerBlock(BlockBehaviour.Properties.of().noOcclusion().noLootTable());});
    public static final RegistryObject<Item> slide_controller_blockitem =ITEMS.register("slide_controller_block",() -> {return new BlockItem( Objects.requireNonNull(slide_controller_block.get()), (new Item.Properties()));});
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
    }
}
