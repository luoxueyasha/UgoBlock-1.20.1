package com.iwaliner.ugoblock.register;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.*;
import com.iwaliner.ugoblock.object.moving_block.CollisionEntity;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.object.rotation_controller.RotationControllerBlock;
import com.iwaliner.ugoblock.object.rotation_controller.RotationControllerBlockEntity;
import com.iwaliner.ugoblock.object.rotation_controller.RotationControllerMenu;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerBlock;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerBlockEntity;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerMenu;
import com.iwaliner.ugoblock.object.wireless_redstone_receiver.WirelessRedstoneReceiverBlock;
import com.iwaliner.ugoblock.object.wireless_redstone_receiver.WirelessRedstoneReceiverBlockEntity;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.WirelessRedstoneTransmitterBlock;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.WirelessRedstoneTransmitterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class Register {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModCoreUgoBlock.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModCoreUgoBlock.MODID);
    public static final RegistryObject<Block> slide_controller_block =BLOCKS.register("slide_controller",() -> {return new SlideControllerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> slide_controller_blockitem =ITEMS.register("slide_controller",() -> {return new BlockItem( Objects.requireNonNull(slide_controller_block.get()), (new Item.Properties()));});
    public static final RegistryObject<Item> shape_card =ITEMS.register("shape_card",() -> {return new ShapeCardItem(  (new Item.Properties()).stacksTo(1));});
    public static final RegistryObject<Item> end_location_card =ITEMS.register("end_position_card",() -> {return new EndLocationCardItem(  (new Item.Properties()).stacksTo(1));});
    public static final RegistryObject<Block> rotation_controller_block =BLOCKS.register("rotation_controller",() -> {return new RotationControllerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> rotation_controller_blockitem =ITEMS.register("rotation_controller",() -> {return new BlockItem( Objects.requireNonNull(rotation_controller_block.get()), (new Item.Properties()));});
    public static final RegistryObject<Block> wireless_redstone_transmitter_block =BLOCKS.register("wireless_redstone_transmitter",() -> {return new WirelessRedstoneTransmitterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> wireless_redstone_transmitter_blockitem =ITEMS.register("wireless_redstone_transmitter",() -> {return new BlockItem( Objects.requireNonNull(wireless_redstone_transmitter_block.get()), (new Item.Properties()));});
    public static final RegistryObject<Block> wireless_redstone_receiver_block =BLOCKS.register("wireless_redstone_receiver",() -> {return new WirelessRedstoneReceiverBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> wireless_redstone_receiver_blockitem =ITEMS.register("wireless_redstone_receiver",() -> {return new BlockItem( Objects.requireNonNull(wireless_redstone_receiver_block.get()), (new Item.Properties()));});
    public static final RegistryObject<Block> smooth_crying_obsidian=BLOCKS.register("smooth_crying_obsidian",() -> {return new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> smooth_crying_obsidian_blockitem =ITEMS.register("smooth_crying_obsidian",() -> {return new BlockItem( Objects.requireNonNull(smooth_crying_obsidian.get()), (new Item.Properties()));});
    public static final RegistryObject<Block> ender_infused_smooth_crying_obsidian=BLOCKS.register("ender_infused_smooth_crying_obsidian",() -> {return new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1F, 1200.0F));});
    public static final RegistryObject<Item> ender_infused_smooth_crying_obsidian_blockitem =ITEMS.register("ender_infused_smooth_crying_obsidian",() -> {return new BlockItem( Objects.requireNonNull(ender_infused_smooth_crying_obsidian.get()), (new Item.Properties()));});



    public static final DeferredRegister<EntityType<?>> Entities = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModCoreUgoBlock.MODID);
    public static final RegistryObject<EntityType<MovingBlockEntity>> MoveableBlock=Entities.register("moving_block", () -> EntityType.Builder.<MovingBlockEntity>of(MovingBlockEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(16).build(new ResourceLocation(ModCoreUgoBlock.MODID,"moving_block").toString()));
    public static final RegistryObject<EntityType<CollisionEntity>> CollisionEntity=Entities.register("collision_entitiy", () -> EntityType.Builder.<CollisionEntity>of(CollisionEntity::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(4).build(new ResourceLocation(ModCoreUgoBlock.MODID,"collision_entity").toString()));





    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModCoreUgoBlock.MODID);
    public static final RegistryObject<BlockEntityType<SlideControllerBlockEntity>> SlideController=BLOCK_ENTITIES.register("slide_controller", () -> BlockEntityType.Builder.of(SlideControllerBlockEntity::new, Register.slide_controller_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<RotationControllerBlockEntity>> RotationController=BLOCK_ENTITIES.register("rotation_controller", () -> BlockEntityType.Builder.of(RotationControllerBlockEntity::new, Register.rotation_controller_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<WirelessRedstoneTransmitterBlockEntity>> WirelessRedstoneTransmitterBlockEntity=BLOCK_ENTITIES.register("wireless_redstone_transmitter", () -> BlockEntityType.Builder.of(WirelessRedstoneTransmitterBlockEntity::new, Register.wireless_redstone_transmitter_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<WirelessRedstoneReceiverBlockEntity>> WirelessRedstoneReceiverBlockEntity=BLOCK_ENTITIES.register("wireless_redstone_receiver", () -> BlockEntityType.Builder.of(WirelessRedstoneReceiverBlockEntity::new, Register.wireless_redstone_receiver_block.get()).build(null));





    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModCoreUgoBlock.MODID);
    public static final RegistryObject<MenuType<com.iwaliner.ugoblock.object.slide_controller.SlideControllerMenu>> SlideControllerMenu = MENUS.register("slide_controller", () -> IForgeMenuType.create((windowId, inv, data) -> {return new SlideControllerMenu(windowId, inv);}));
    public static final RegistryObject<MenuType<com.iwaliner.ugoblock.object.rotation_controller.RotationControllerMenu>> RotationControllerMenu = MENUS.register("rotation_controller", () -> IForgeMenuType.create((windowId, inv, data) -> {return new RotationControllerMenu(windowId, inv);}));





    public static final TagKey<Block> TAG_DISABLE_MOVING =TagKey.create(Registries.BLOCK, new ResourceLocation(ModCoreUgoBlock.MODID,"disable_moving"));
    public static final TagKey<Block> TAG_DISABLE_ITEM_DROP =TagKey.create(Registries.BLOCK, new ResourceLocation(ModCoreUgoBlock.MODID,"disable_item_drop"));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        Entities.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        MENUS.register(eventBus);
    }
}
