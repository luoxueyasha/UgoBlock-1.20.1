package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class WirelessRedstoneTransmitterPacket {
    private final CompoundTag wirelessRedstoneTag;

    public WirelessRedstoneTransmitterPacket(CompoundTag tag) {
        this.wirelessRedstoneTag = tag;
    }

    public WirelessRedstoneTransmitterPacket(FriendlyByteBuf buf) {
        this.wirelessRedstoneTag = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(wirelessRedstoneTag);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        ServerLevel level = player.serverLevel();
        context.enqueueWork(() -> {
            level.getCapability(WirelessRedstoneProvider.WIRELESS_REDSTONE).ifPresent(data -> {
                data.setTag(wirelessRedstoneTag);
            });
        });
        return true;
    }
}
