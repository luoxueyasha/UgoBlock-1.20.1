package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.object.SlideControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PacketListenerImpl implements PacketListener{
    public ServerPlayer player;
    @Override
    public void handleSlideController(ServerBoundSlideControllerPacket packet) {
        BlockPos pos=packet.getPos();
        int startTime=packet.getStartTime();
        int duration=packet.getDuration();

        BlockState blockstate = this.player.level().getBlockState(pos);
        BlockEntity blockentity = this.player.level().getBlockEntity(pos);
        if (blockentity instanceof SlideControllerBlockEntity slideControllerBlockEntity) {
            slideControllerBlockEntity.setStartTime(startTime*20);
            slideControllerBlockEntity.setDuration(duration*20);
            slideControllerBlockEntity.setChanged();
            this.player.level().sendBlockUpdated(pos, blockstate, blockstate, 3);
        }
    }

    @Override
    public void onDisconnect(Component component) {
    /*    this.connection.send(new ClientboundDisconnectPacket(p_9943_), PacketSendListener.thenRun(() -> {
            this.connection.disconnect(p_9943_);
        }));
        this.connection.setReadOnly();
        this.server.executeBlocking(this.connection::handleDisconnection);*/
    }

    @Override
    public boolean isAcceptingMessages() {
        return false;
    }
}
