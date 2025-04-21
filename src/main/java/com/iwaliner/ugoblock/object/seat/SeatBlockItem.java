package com.iwaliner.ugoblock.object.seat;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

public class SeatBlockItem extends BlockItem {
    public SeatBlockItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i0, boolean flag0) {
        super.inventoryTick(stack, level, entity, i0, flag0);
        if(entity instanceof Player player&&(player.getMainHandItem().is(Register.seat_blockitem.get())||player.getOffhandItem().is(Register.seat_blockitem.get()))){
            int bigger=10;
            for (Entity seat : level.getEntities((Entity) null, new AABB(entity.blockPosition()).inflate(bigger, bigger, bigger), (o) -> {
                return (o instanceof SeatEntity);
            })) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Register.seat.get().defaultBlockState()),seat.getX(),seat.getY()+0.35D,seat.getZ(),0D,0D,0D);
            }
        }
    }
}
