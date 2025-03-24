package com.iwaliner.ugoblock.network;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WirelessRedstoneProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<WirelessRedstoneData> WIRELESS_REDSTONE = CapabilityManager.get(new CapabilityToken<WirelessRedstoneData>() { });

    private WirelessRedstoneData data = null;
    private final LazyOptional<WirelessRedstoneData> optional = LazyOptional.of(this::createData);

    private WirelessRedstoneData createData() {
        if(this.data == null) {
            this.data = new WirelessRedstoneData();
        }

        return this.data;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == WIRELESS_REDSTONE) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createData().loadNBTData(nbt);
    }
}
