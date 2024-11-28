package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.EndLocationCardItem;
import com.iwaliner.ugoblock.object.ShapeCardItem;
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
    public static final RegistryObject<Block> slide_controller_block =BLOCKS.register("slide_controller",() -> {return new SlideControllerBlock(BlockBehaviour.Properties.of());});
    public static final RegistryObject<Item> slide_controller_blockitem =ITEMS.register("slide_controller",() -> {return new BlockItem( Objects.requireNonNull(slide_controller_block.get()), (new Item.Properties()));});
    public static final RegistryObject<Item> shape_card =ITEMS.register("shape_card",() -> {return new ShapeCardItem(  (new Item.Properties()).stacksTo(1));});
    public static final RegistryObject<Item> end_location_card =ITEMS.register("end_location_card",() -> {return new EndLocationCardItem(  (new Item.Properties()).stacksTo(1));});

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
    }
}
