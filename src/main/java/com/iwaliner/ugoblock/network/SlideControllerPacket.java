package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.object.SlideControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SlideControllerPacket {
    private final int startTime;
    private final int duration;
    private final BlockPos pos;
    public SlideControllerPacket(BlockPos pos, int startTime,int duration){
        this.startTime = startTime;
        this.duration = duration;
        this.pos=pos;
    }

    public SlideControllerPacket(FriendlyByteBuf friendlyByteBuf) {
        this.pos = friendlyByteBuf.readBlockPos();
        this.startTime = (int) friendlyByteBuf.readByte();
        this.duration = (int) friendlyByteBuf.readByte();
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeByte(startTime);
        friendlyByteBuf.writeByte(duration);
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
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
    }

}
