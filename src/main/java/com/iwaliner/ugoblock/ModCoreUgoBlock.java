package com.iwaliner.ugoblock;

import com.iwaliner.ugoblock.network.WirelessRedstoneData;
import com.iwaliner.ugoblock.network.WirelessRedstoneProvider;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerBlock;
import com.iwaliner.ugoblock.object.block_imitation_wand.BlockImitationWandDecoration;
import com.iwaliner.ugoblock.object.controller.AbstractControllerBlockEntity;
import com.iwaliner.ugoblock.object.controller.RotationControllerBlock;
import com.iwaliner.ugoblock.object.controller.SlideControllerBlock;
import com.iwaliner.ugoblock.object.wireless_redstone_transmitter.PortableAlternateWirelessRedstoneTransmitterItem;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


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
            event.accept(Register.seat.get());
            event.accept(Register.slide_controller_blockitem.get());
            event.accept(Register.rotation_controller_blockitem.get());
            event.accept(Register.basket_maker_blockitem.get());
            event.accept(Register.wireless_redstone_transmitter_blockitem.get());
            event.accept(Register.wireless_redstone_receiver_blockitem.get());
            event.accept(Register.portable_alternate_wireless_redstone_transmitter.get());
            event.accept(Register.portable_momentary_wireless_redstone_transmitter.get());
            event.accept(Register.block_imitation_wand.get());
           // event.accept(Register.gravitate_piston_blockitem.get());
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
        if(dyeStack.getItem()instanceof DyeItem&& (transmitterStack.is(Register.portable_alternate_wireless_redstone_transmitter.get()) || transmitterStack.is(Register.portable_momentary_wireless_redstone_transmitter.get()) )) {
            DyeColor dyeColor=((DyeItem) dyeStack.getItem()).getDyeColor();
            if (PortableAlternateWirelessRedstoneTransmitterItem.isColor1Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableAlternateWirelessRedstoneTransmitterItem.setColor1(transmitterStack,dyeColor);
                event.setCanceled(true);
            }else if (PortableAlternateWirelessRedstoneTransmitterItem.isColor2Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableAlternateWirelessRedstoneTransmitterItem.setColor2(transmitterStack,dyeColor);
                event.setCanceled(true);
            }else if (PortableAlternateWirelessRedstoneTransmitterItem.isColor3Null(transmitterStack)) {
                event.getPlayer().level().playSound(event.getPlayer(),event.getPlayer().blockPosition(), SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                PortableAlternateWirelessRedstoneTransmitterItem.setColor3(transmitterStack,dyeColor);
                event.getPlayer().level().getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                    boolean alreadyExist=!data.isSignalNull(PortableAlternateWirelessRedstoneTransmitterItem.getColor1(transmitterStack), PortableAlternateWirelessRedstoneTransmitterItem.getColor2(transmitterStack), PortableAlternateWirelessRedstoneTransmitterItem.getColor3(transmitterStack));
                    if(alreadyExist&&event.getPlayer().level().isClientSide){
                        event.getPlayer().displayClientMessage(Utils.getComponentFrequencyAlreadyExists(PortableAlternateWirelessRedstoneTransmitterItem.getColor1(transmitterStack), PortableAlternateWirelessRedstoneTransmitterItem.getColor2(transmitterStack), PortableAlternateWirelessRedstoneTransmitterItem.getColor3(transmitterStack)), false);
                    }
                });
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void CardLeftClickEvent(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack=event.getItemStack();
        BlockPos pos=event.getPos();
        if(stack.is(Register.shape_card.get())&&event.getAction()== PlayerInteractEvent.LeftClickBlock.Action.START){
            List<BlockPos> list=new ArrayList<>();
            Level level=event.getLevel();
            BlockState state=level.getBlockState(pos);
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                tag = new CompoundTag();
            }
            if(!tag.contains("positionList")){
                tag.put("positionList",new CompoundTag());
            }
            CompoundTag posTag=tag.getCompound("positionList");
            /**このタグは、tureのときfalseにした上で範囲選択終了処理を行い、falseのときはtrueにしたうえで範囲選択開始処理を行う*/
            if(!tag.contains("select")){
                tag.putBoolean("select",false);
            }
            tag.putBoolean("select",!tag.getBoolean("select"));
            //   if(!(state.getBlock() instanceof SlideControllerBlock)) {
            int ii = -1;
            for (int i = 0; i < Utils.getMaxSize(); i++) {
                if (!posTag.contains("location_" + String.valueOf(i))) {
                    ii = i;
                    break;
                } else {
                    list.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
                }
            }
            if (ii != -1) {
                if (tag.getBoolean("select")) {
                    /**範囲選択の始点を登録*/
                    tag.put("edge_A", NbtUtils.writeBlockPos(pos));

                    stack.setTag(tag);
                } else {
                    /**範囲選択の終点は今クリックした地点なので、始点も呼び出すことで範囲が確定*/
                    BlockPos edgeA = NbtUtils.readBlockPos(tag.getCompound("edge_A"));
                    List<BlockPos> removeList = new ArrayList<>();
                    List<BlockPos> newList = new ArrayList<>();
                    for (int i = 0; i <= Math.abs(edgeA.getX() - pos.getX()); i++) {
                        for (int j = 0; j <= Math.abs(edgeA.getY() - pos.getY()); j++) {
                            for (int k = 0; k <= Math.abs(edgeA.getZ() - pos.getZ()); k++) {
                                BlockPos pos2 = pos.offset(edgeA.getX() - pos.getX() >= 0 ? i : -i, edgeA.getY() - pos.getY() >= 0 ? j : -j, edgeA.getZ() - pos.getZ() >= 0 ? k : -k);
                                if (list.contains(pos2)) {
                                    removeList.add(pos2);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (!removeList.contains(list.get(i))) {
                            newList.add(list.get(i));
                        }
                    }
                    CompoundTag newTag = new CompoundTag();
                    for (int i = 0; i < newList.size(); i++) {
                        newTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(newList.get(i)));
                    }
                    tag.put("positionList",newTag);
                    stack.setTag(tag);
                }
            } else {
                stack.setTag(tag);
            }
            //  }
            level.playSound(event.getEntity(),pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS,1F,1F);
            if(event.getEntity().isCreative()) {
                event.setCanceled(true);
            }
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
    @SubscribeEvent
    public void denyOpenGUIEvent(PlayerInteractEvent.RightClickBlock event) {
        if(event.getEntity() == null){
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state=event.getLevel().getBlockState(pos);
        Block block=state.getBlock();
        LivingEntity livingEntity = (LivingEntity)event.getEntity();
        ItemStack stack=livingEntity.getItemInHand(event.getHand());
        if(stack.is(Register.shape_card.get())||stack.is(Register.vector_card.get())) {
            if(state.getBlock() instanceof BasketMakerBlock||state.getBlock() instanceof RotationControllerBlock||state.getBlock() instanceof SlideControllerBlock){
                if(event.getLevel().getBlockEntity(pos) instanceof AbstractControllerBlockEntity blockEntity){
                    if(stack.is(Register.shape_card.get())&&blockEntity.hasShapeCard()){
                        event.setUseItem(Event.Result.ALLOW);
                        event.setUseBlock(Event.Result.DENY);
                    }else if(stack.is(Register.vector_card.get())&&blockEntity.hasVectorCard()){
                        event.setUseItem(Event.Result.ALLOW);
                        event.setUseBlock(Event.Result.DENY);
                    }
                }
            }else {
                event.setUseItem(Event.Result.ALLOW);
                event.setUseBlock(Event.Result.DENY);
            }
        }
    }
}
