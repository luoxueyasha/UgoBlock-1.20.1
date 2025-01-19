package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShapeCardItem extends Item {
    public ShapeCardItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if(stack.getItem() instanceof ShapeCardItem) {
            List<BlockPos> list=new ArrayList<>();
            Level level=player.level();
            BlockState state=level.getBlockState(pos);
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                tag = new CompoundTag();
            }
            if(!tag.contains("positionList")){
                tag.put("positionList",new CompoundTag());
            }
            CompoundTag posTag=tag.getCompound("positionList");
            /**このタグは、tureのときfalseにした上で範囲選択終了処理を行い、falseのときはtrueにしたうえで範囲選択開始処理を行う*/
            if(!tag.contains("select")){
                tag.putBoolean("select",false);
            }
            tag.putBoolean("select",!tag.getBoolean("select"));
            if(!(state.getBlock() instanceof SlideControllerBlock)) {
                int ii = -1;
                for (int i = 0; i < Utils.getMaxSize(); i++) {
                    if (!posTag.contains("location_" + String.valueOf(i))) {
                        ii = i;
                        break;
                    } else {
                        list.add(NbtUtils.readBlockPos(posTag.getCompound("location_" + String.valueOf(i))));
                    }
                }
                if (ii != -1) {
                    if (tag.getBoolean("select")) {
                        /**範囲選択の始点を登録*/
                        tag.put("edge_A", NbtUtils.writeBlockPos(pos));

                        stack.setTag(tag);
                    } else {
                        /**範囲選択の終点は今クリックした地点なので、始点も呼び出すことで範囲が確定*/
                        BlockPos edgeA = NbtUtils.readBlockPos(tag.getCompound("edge_A"));
                        List<BlockPos> removeList = new ArrayList<>();
                        List<BlockPos> newList = new ArrayList<>();
                        for (int i = 0; i <= Math.abs(edgeA.getX() - pos.getX()); i++) {
                            for (int j = 0; j <= Math.abs(edgeA.getY() - pos.getY()); j++) {
                                for (int k = 0; k <= Math.abs(edgeA.getZ() - pos.getZ()); k++) {
                                    BlockPos pos2 = pos.offset(edgeA.getX() - pos.getX() >= 0 ? i : -i, edgeA.getY() - pos.getY() >= 0 ? j : -j, edgeA.getZ() - pos.getZ() >= 0 ? k : -k);
                                    if (list.contains(pos2)) {
                                        removeList.add(pos2);
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (!removeList.contains(list.get(i))) {
                                newList.add(list.get(i));
                            }
                        }
                        CompoundTag newTag = new CompoundTag();
                        for (int i = 0; i < newList.size(); i++) {
                            newTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(newList.get(i)));
                        }
                        tag.put("positionList",newTag);
                        stack.setTag(tag);
                    }
                } else {
                    stack.setTag(tag);
                }
            }
            level.playSound(player,pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS,1F,1F);

        }

        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level=context.getLevel();
        BlockPos pos=context.getClickedPos();
        BlockState state=level.getBlockState(pos);
        ItemStack stack=context.getItemInHand();
        List<BlockPos> list=new ArrayList<>();

        if(stack.getItem() instanceof ShapeCardItem) {
            CompoundTag tag =stack.getTag();
            if(tag==null){
                tag=new CompoundTag();
            }
            if(!tag.contains("positionList")){
                tag.put("positionList",new CompoundTag());
            }
            CompoundTag posTag=tag.getCompound("positionList");
            /**このタグは、tureのときfalseにした上で範囲選択終了処理を行い、falseのときはtrueにしたうえで範囲選択開始処理を行う*/
            if(!tag.contains("select")){
                tag.putBoolean("select",false);
            }
            tag.putBoolean("select",!tag.getBoolean("select"));

            if(!(state.getBlock() instanceof SlideControllerBlock)){
                int ii=-1;
                for(int i=0;i<Utils.getMaxSize();i++){
                    if(!posTag.contains("location_"+String.valueOf(i))){
                        ii=i;
                        break;
                    }else{
                        list.add(NbtUtils.readBlockPos(posTag.getCompound("location_"+String.valueOf(i))));
                    }
                }
                if(ii!=-1) {
                    if(tag.getBoolean("select")){
                        /**範囲選択の始点を登録*/
                        tag.put("edge_A", NbtUtils.writeBlockPos(pos));
                    }else{
                        /**範囲選択の終点は今クリックした地点なので、始点も呼び出すことで範囲が確定*/
                        BlockPos edgeA=NbtUtils.readBlockPos(tag.getCompound("edge_A"));
                        for(int i=0;i<=Math.abs(edgeA.getX()-pos.getX());i++){
                            for(int j=0;j<=Math.abs(edgeA.getY()-pos.getY());j++) {
                                for (int k = 0; k <= Math.abs(edgeA.getZ() - pos.getZ()); k++) {
                                    BlockPos pos2=pos.offset(edgeA.getX()-pos.getX()>=0? i: -i,edgeA.getY()-pos.getY()>=0? j: -j,edgeA.getZ()-pos.getZ()>=0? k: -k);
                                    int ii2=-1;
                                    for(int i0=0;i0<Utils.getMaxSize();i0++){
                                        if(!posTag.contains("location_"+String.valueOf(i0))){
                                            ii2=i0;
                                            break;
                                        }
                                    }
                                    if(ii2!=-1&&!level.getBlockState(pos2).isAir()&&!list.contains(pos2)) {
                                        posTag.put("location_" + String.valueOf(ii2), NbtUtils.writeBlockPos(pos2));
                                    }
                                }
                            }
                        }
                        /**始点をリセット*/
                        tag.put("edge_A", NbtUtils.writeBlockPos(Utils.errorPos()));
                    }
                }
                stack.setTag(tag);
                level.playSound(context.getPlayer(),pos, SoundEvents.UI_STONECUTTER_SELECT_RECIPE, SoundSource.BLOCKS,1F,1F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }



   public boolean isFoil(ItemStack stack) {
        return stack.getTag()!=null&&stack.getTag().getBoolean("select");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("info.ugoblock.shape_card").withStyle(ChatFormatting.GREEN));
        if(stack.getTag()!=null) {
            int size=Utils.getPositionList(stack.getTag()).size();
            for (int i = 0;size>5? i < 5 : i<size; i++) {
                BlockPos pos = NbtUtils.readBlockPos(stack.getTag().getCompound("positionList").getCompound("location_" + String.valueOf(i)));
                if (!pos.equals(Utils.errorPos())) {
                    list.add(Component.translatable("info.ugoblock.shape_card_location").append("[").append(String.valueOf(pos.getX())).append(", ").append(String.valueOf(pos.getY())).append(", ").append(String.valueOf(pos.getZ())).append("]").withStyle(ChatFormatting.GRAY));
                }
            }
            if(size>5) {
                list.add(Component.translatable("info.ugoblock.shape_card_location_other").withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
