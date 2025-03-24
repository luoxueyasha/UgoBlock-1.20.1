package com.iwaliner.ugoblock.network;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;

public class WirelessRedstoneData {
    private CompoundTag remoteRedstoneTag;


   public boolean getSignal(DyeColor color1,DyeColor color2,DyeColor color3){
        String name="signal["+color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        return remoteRedstoneTag==null? false : remoteRedstoneTag.getBoolean(name);
   }
    public boolean isSignalNull(DyeColor color1,DyeColor color2,DyeColor color3){
        String name="signal["+color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        if(remoteRedstoneTag==null){
            remoteRedstoneTag=new CompoundTag();
        }
        return !remoteRedstoneTag.contains(name);
    }
    public void setSignalNull(DyeColor color1,DyeColor color2,DyeColor color3){
        String name="signal["+ color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        if(remoteRedstoneTag!=null) {
            remoteRedstoneTag.remove(name);
        }
    }
    public void setSignal(DyeColor color1,DyeColor color2,DyeColor color3,boolean power){

        String name="signal["+ color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        if(remoteRedstoneTag==null){
            remoteRedstoneTag=new CompoundTag();
        }
         remoteRedstoneTag.putBoolean(name,power);
    }
    public void setTag(CompoundTag tag){
       remoteRedstoneTag=tag;
    }
    public CompoundTag getTag(){
       return remoteRedstoneTag;
    }

    public void saveNBTData(CompoundTag nbt) {
       if(remoteRedstoneTag==null) {
           nbt.put("wirelessRedstoneData", new CompoundTag());
       }else{
           nbt.put("wirelessRedstoneData", remoteRedstoneTag);
       }
    }

    public void loadNBTData(CompoundTag nbt) {
       if(!nbt.contains("wirelessRedstoneData")){
           remoteRedstoneTag = new CompoundTag();
       }else {
           remoteRedstoneTag = nbt.getCompound("wirelessRedstoneData");
       }
    }
}