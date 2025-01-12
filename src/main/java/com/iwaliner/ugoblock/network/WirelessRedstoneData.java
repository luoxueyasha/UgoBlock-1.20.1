package com.iwaliner.ugoblock.network;

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
        remoteRedstoneTag.remove(name);
    }
    public void setSignal(DyeColor color1,DyeColor color2,DyeColor color3,boolean power){
        String name="signal["+ color1.getName()+","+color2.getName()+","+color3.getName()+"]";
        if(remoteRedstoneTag==null){
            remoteRedstoneTag=new CompoundTag();
        }
         remoteRedstoneTag.putBoolean(name,power);
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
       /* if(remoteRedstoneTag.isEmpty()){
            for(int color1=0;color1<16;color1++){
                for(int color2=0;color2<16;color2++){
                    for(int color3=0;color3<16;color3++){
                        remoteRedstoneTag.putBoolean("signal["+color1+","+color2+","+color3+"]",false);
                    }
                }
            }
        }*/
    }
}