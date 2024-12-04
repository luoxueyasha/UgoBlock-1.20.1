package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ModCoreUgoBlock.MODID,"messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;



        net.messageBuilder(SlideControllerPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SlideControllerPacket::new)
                .encoder(SlideControllerPacket::write)
                .consumerMainThread(SlideControllerPacket::handle)
                .add();
      /*  net.messageBuilder(ServerBoundSlideControllerPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ServerBoundSlideControllerPacket::new)
                .encoder(ServerBoundSlideControllerPacket::write)
                .consumerMainThread(ServerBoundSlideControllerPacket::handle)
                .add();*/
    }
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
