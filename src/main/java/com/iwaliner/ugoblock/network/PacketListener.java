package com.iwaliner.ugoblock.network;

import net.minecraft.network.protocol.game.ServerGamePacketListener;


public interface PacketListener extends net.minecraft.network.PacketListener {
    void handleSlideController(ServerBoundSlideControllerPacket p_242214_);
}
