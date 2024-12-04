package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.network.PacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerBoundSlideControllerPacket implements Packet<PacketListener> {
    private final int startTime;
    private final int duration;
    private final BlockPos pos;
    public ServerBoundSlideControllerPacket(BlockPos pos, int startTime, int duration){
        this.startTime = startTime;
        this.duration = duration;
        this.pos=pos;
    }

    public ServerBoundSlideControllerPacket(FriendlyByteBuf friendlyByteBuf) {
        this.pos = friendlyByteBuf.readBlockPos();
        this.startTime = (int) friendlyByteBuf.readByte();
        this.duration = (int) friendlyByteBuf.readByte();
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeByte(startTime);
        friendlyByteBuf.writeByte(duration);
    }

    @Override
    public void handle(PacketListener listener) {
        listener.handleSlideController(this);
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public BlockPos getPos() {
        return pos;
    }
    /*public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        ServerPlayer player = context.getSender();
        if(player!=null) {
            ServerLevel level = player.serverLevel();
            context.enqueueWork(() -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof SlideControllerBlockEntity slideControllerBlockEntity) {
                    slideControllerBlockEntity.setStartTime(startTime * 20);
                    slideControllerBlockEntity.setDuration(duration * 20);
                }
            });
        }
        return true;
    }*/

}
