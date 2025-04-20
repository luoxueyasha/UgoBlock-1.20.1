package com.iwaliner.ugoblock.object.controller;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.basket_maker.BasketMakerBlock;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RotationControllerBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty MOVING = BooleanProperty.create("moving");
    public static final BooleanProperty COUNTER_CLOCKWISE = BooleanProperty.create("counter_clockwise");
    public static final BooleanProperty IGNORE = BooleanProperty.create("ignore");
    public RotationControllerBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(MOVING,false).setValue(COUNTER_CLOCKWISE,false).setValue(IGNORE,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(POWERED,FACING,MOVING,COUNTER_CLOCKWISE,IGNORE);
    }
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RotationControllerBlockEntity) {
            player.openMenu((MenuProvider)blockentity);
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack=player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity) {
            if (stack.getItem() == Register.shape_card.get()&&blockEntity.getItem(0).isEmpty()&&!player.isSuppressingBounce()) {
                blockEntity.setItem(0,stack);
                player.setItemInHand(hand,ItemStack.EMPTY);
                player.level().playSound(player,pos, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS,1F,1F);
                return InteractionResult.SUCCESS;
            }else if(stack.is(Register.block_imitation_wand.get())){
                return InteractionResult.PASS;
            }else if(!state.getValue(MOVING)&&!state.getValue(POWERED)) {
                if (!player.isSuppressingBounce()) {
                    if (level.isClientSide) {
                        return InteractionResult.SUCCESS;
                    }
                    this.openContainer(level, pos, player);
                    return InteractionResult.CONSUME;
                }else if(!blockEntity.getImitatingState().isAir()){
                    blockEntity.setImitatingState(Blocks.AIR.defaultBlockState());
                    level.playSound(player,pos, SoundEvents.DYE_USE, SoundSource.BLOCKS,1F,1F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if(state.getValue(MOVING)){
            player.displayClientMessage(Component.translatable("info.ugoblock.controller_denyed_opening_gui").withStyle(ChatFormatting.YELLOW), true);
        }else{
            player.displayClientMessage(Component.translatable("info.ugoblock.idle_rotation_controller_denyed_opening_gui").withStyle(ChatFormatting.YELLOW), true);
        }
         return InteractionResult.PASS;
    }
    public static Direction.Axis getAxis(BlockState state){
        Direction facing=state.getValue(FACING);
        return facing.getAxis();
    }
    /*public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {*//**neighborChangedでエンティティ化したら、その2tickあとにここでエンティティ化済みのブロックを除去する。時間をあけるのは一瞬何も表示されなくなる(=一瞬消えることでちらつく)のを軽減するため。*//*
        *//*if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.hasCards()) {
            BlockPos startPos = pos.relative(state.getValue(FACING));
            List<BlockPos> posList=state.getValue(POWERED)? blockEntity.getPositionList() :  Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),blockEntity.getDegreeAngle());

            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos); *//**//**neighborChangedでエンティティ化したら、その2tickあとにここでエンティティ化済みのブロックを除去する。時間をあけるのは一瞬何も表示されなくなる(=一瞬消えることでちらつく)のを軽減するため。*//**//*
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
        }*//*
        if(level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&state.getBlock() instanceof RotationControllerBlock){
            removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);
        }
    }*/
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&state.getBlock() instanceof RotationControllerBlock) {
            removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);
            /*if(blockEntity.isLoop()&&state.getValue(MOVING)&& state.getValue(POWERED)) {
                if (blockEntity.hasCards() && blockEntity.isLoop() *//*&& blockEntity.getTickCount() > 2 && state.getValue(POWERED)*//*) {
                    BlockPos startPos = pos.relative(state.getValue(FACING));
                    //removeBlocks(level,startPos,blockEntity,state);
                    //blockEntity.setMoving(false);
                    //       level.setBlock(pos, state.setValue(POWERED,false).setValue(MOVING,false).setValue(IGNORE,true), 82);
                    //    level.scheduleTick(pos, this, 2);
                    for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                        return (o instanceof MovingBlockEntity);
                    })) {
                        MovingBlockEntity movingBlock = (MovingBlockEntity) entity;
                        //  if(movingBlock.getTimeCount()<10){
                        removeBlocks(level, startPos, blockEntity, state);
                        //   }
                        movingBlock.setDiscardTime(-1);
                    }
                    blockEntity.setTickCount(0);
                    blockEntity.setMoving(false);
                    //level.setBlock(pos, state.setValue(POWERED,false).setValue(MOVING,false).setValue(IGNORE,true), 2);
                    blockEntity.setNotFirstTime(false);
                    level.setBlockAndUpdate(pos, state.setValue(POWERED, false).setValue(MOVING, false)*//*.setValue(IGNORE, true)*//*);
                }
            }else{
                   // removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);

            }*/
        }
    }
    public static void removeBlocks(Level level, BlockPos startPos, RotationControllerBlockEntity blockEntity, BlockState state){
        if(blockEntity.hasCards()) {
            List<BlockPos> posList=state.getValue(POWERED)? blockEntity.getPositionList() :  Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),getDegreeAngle(state,blockEntity));
            if(posList!=null) {
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        destroyOldBlock(level, eachPos);
                    }
                }
                for (BlockPos eachPos : posList) {
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        updateDestroyedPos(level, eachPos);
                    }
                }
                blockEntity.setNotFirstTime(true);
            }
            List<BlockPos> basketPosList=blockEntity.getBasketPosList();
             if(basketPosList!=null){
            for (int i = 0; i < basketPosList.size(); i++) {
                   BlockPos eachBasketPos=basketPosList.get(i).offset(startPos.getX(),startPos.getY(),startPos.getZ());
                    destroyOldBlock(level,eachBasketPos);
            }
                 for (int i = 0; i < basketPosList.size(); i++) {
                     BlockPos eachBasketPos=basketPosList.get(i).offset(startPos.getX(),startPos.getY(),startPos.getZ());
                     updateDestroyedPos(level,eachBasketPos);
                 }
            }
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
        boolean signal=level.hasNeighborSignal(pos);
        int signalVolume=level.getBestNeighborSignal(pos);
        if (level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.isPreRedstoneSignalON()!=signal&&!level.isClientSide()) {
            blockEntity.setPreRedstoneSignal(signal);
            BlockPos startPos = pos.relative(state.getValue(FACING));
            Direction controllerDirection=state.getValue(BlockStateProperties.FACING);
            if(state.getValue(IGNORE)){
                level.setBlock(pos, state.setValue(IGNORE,false), 2);
                return;
            }
            int start=blockEntity.getStartTime();
            int duration=blockEntity.getDuration();
            boolean makingEntitySuccess=false;
            if(blockEntity.hasCards()&&blockEntity.isLoop()){
                if(!flag){
                    if(blockEntity.isIgnore()){
                        blockEntity.setIgnore(false);
                        return;
                    }
                    if (duration > 0) {
                        List<BlockPos> posList = blockEntity.getPositionList();

                        if (posList != null) {

                            blockEntity.setMoving(true);
                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), getDegreeAngle(state,blockEntity),blockEntity.getPositionList(),0,false,BlockPos.ZERO,0);
                            if(makingEntitySuccess) {
                                //removeBlocks(level,startPos,blockEntity,state);
                                blockEntity.setMoving(true);
                                level.setBlock(pos, state.setValue(POWERED,true).setValue(MOVING, true), 2);
                                level.scheduleTick(pos, this, 0);
                            }
                        }
                    }
                }else{
                    for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                        return (o instanceof MovingBlockEntity);
                    })) {
                        MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                        movingBlock.setDiscardTime(-1);
                        //blockEntity.setNotFirstTime(false);
                        blockEntity.setMoving(false);
                        blockEntity.setIgnore(true);
                        int angle=blockEntity.getDegreeAngleAndLoop();
                        ItemStack card=blockEntity.getItem(0);

                        RotationControllerBlockEntity newBlockEntity=new RotationControllerBlockEntity(pos,state);
                        newBlockEntity.setDegreeAngle(angle);
                        newBlockEntity.setDuration(duration);
                        newBlockEntity.setItem(0,card);
                        newBlockEntity.setMoving(false);
                        newBlockEntity.setIgnore(true);
                        level.removeBlockEntity(pos);
                        level.setBlockEntity(newBlockEntity);
                        level.setBlock(pos,state.setValue(RotationControllerBlock.POWERED,false).setValue(RotationControllerBlock.MOVING,false),2);
                    }
                }

            } else if(!blockEntity.isMoving()&&blockEntity.hasCards()&&!blockEntity.isLoop()) { /**動いている最中は赤石入力の変化を無視する*/

                if(!flag) {
                    if (duration > 0) {
                        List<BlockPos> posList = blockEntity.getPositionList();

                        if (posList != null) {

                            blockEntity.setMoving(true);
                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), getDegreeAngle(state,blockEntity),blockEntity.getPositionList(),0,false,BlockPos.ZERO,0);

                        }
                    }
                    if(makingEntitySuccess) {
                        //removeBlocks(level,startPos,blockEntity,state);
                        level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                        level.scheduleTick(pos, this, 0);
                    }
                }else{

                    boolean b1=false;
                    for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                        return (o instanceof MovingBlockEntity);
                    })) {
                        b1=true;
                        MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                        CompoundTag tag=movingBlock.getCompoundTag();
                        int angle=movingBlock.getDegreeAngle();
//                              makeMoveableBlockReverse(level,pos,startPos,start,duration,getAxis(state),angle,tag);
//                              ((MovingBlockEntity) entity).setDiscardTime(2);
                        makeMoveableBlockReverse(level,pos,movingBlock);
                    }
                    if(!b1){
                        int degree=-getDegreeAngle(state,blockEntity);
                              /*if(degree>=180){
                                  degree=-180;
                              }else if(degree<=-180){
                                  degree=180;
                              }*/
                        boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
                        int degree0=degree;
                        // int visualDegree0=blockEntity.getVisualDegree();

                        if(invertRotation&&(degree0%90)==0){
                            //  degree0=-degree0;
                            //  visualDegree0=-visualDegree0;
                        }
                        List<BlockPos> rotatedPosList = Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),getDegreeAngle(state,blockEntity));
                        List<BlockPos> rotatedBasketPosList = Utils.rotateBasketPosList(blockEntity.getBasketPosList(),BlockPos.ZERO,startPos,state.getValue(FACING).getAxis(),-getDegreeAngle(state,blockEntity),blockEntity.getBasketOriginPosList());
                              /*List<BlockPos> rotatedBasketPosList = blockEntity.getBasketPosList();
                              for(int j=0;j<rotatedBasketPosList.size();j++){
                                  rotatedBasketPosList.set(j,rotatedBasketPosList.get(j).offset(startPos.getX(),startPos.getY(),startPos.getZ()));
                              }*/
                        rotatedPosList.addAll(rotatedBasketPosList);
                        if(!blockEntity.isLoop()){
                            //  int visualDegree= blockEntity.getVisualDegree();

                            /**ブロックをエンティティ化*/
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),rotatedPosList, getVisualDegreeAngle(state,blockEntity),true,BlockPos.ZERO,0);
                            if(makingEntitySuccess) {
                                blockEntity.setMoving(true);
                                level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                                level.scheduleTick(pos, this, 0);
                            }
                        }else{
                            removeBlocks(level,startPos,blockEntity,state);
                            makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),blockEntity.isNotFirstTime()? rotatedPosList: blockEntity.getPositionList(), 0,true,BlockPos.ZERO,0);
                            //removeBlocks(level,startPos,blockEntity,state);

                        }
                    }else{
                        blockEntity.setMoving(true);
                        level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                    }
                         /*if(!blockEntity.isLoop()){
                              blockEntity.setMoving(true);
                              level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                                  level.scheduleTick(pos, this, 2);
                          }else{
                             removeBlocks(level,startPos,blockEntity,state);
                             blockEntity.setMoving(false);
                             level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,false), 2);
                             //level.scheduleTick(pos, this, 2);

                         }*/
                   /* if(blockEntity.isLoop()){
                        removeBlocks(level,startPos,blockEntity,state);
                        blockEntity.setMoving(false);
                        level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,false), 2);
                    }*/

                }
            }/*else if(blockEntity.hasCards()&&blockEntity.isLoop()&&blockEntity.getTickCount()>2&&state.getValue(POWERED)){
                //removeBlocks(level,startPos,blockEntity,state);
                blockEntity.setMoving(false);
                level.setBlock(pos, state.setValue(POWERED,false).setValue(MOVING,false)*//*.setValue(IGNORE,true)*//*, 2);
                //    level.scheduleTick(pos, this, 2);
                for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                    return (o instanceof MovingBlockEntity);
                })) {
                    MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                    if(movingBlock.getTimeCount()<10){
                        removeBlocks(level,startPos,blockEntity,state);
                    }
                    movingBlock.setDiscardTime(-1);
                }
                blockEntity.setTickCount(0);
            }*/
            blockEntity.setPreRedstoneSignal(signal);
        }

    }
   /* public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean b) {
        boolean flag = state.getValue(POWERED);
             if (level.hasNeighborSignal(pos)&&level.getBlockEntity(pos) instanceof RotationControllerBlockEntity blockEntity&&blockEntity.isPreRedstoneSignalON()==false) {
                  BlockPos startPos = pos.relative(state.getValue(FACING));
                Direction controllerDirection=state.getValue(BlockStateProperties.FACING);
                if(state.getValue(IGNORE)){
                    level.setBlock(pos, state.setValue(IGNORE,false), 2);
                    ModCoreUgoBlock.logger.info("BBB");
                    return;
                }
                 if(blockEntity.hasCards()&&blockEntity.isLoop()&&blockEntity.getTickCount()>2&&state.getValue(POWERED)) {
                     //  level.setBlockAndUpdate(pos, state.setValue(IGNORE, true));
                     level.scheduleTick(pos, this, 0);
                     return;
                 }
                     if(!blockEntity.isMoving()&&blockEntity.hasCards()) { *//**動いている最中は赤石入力の変化を無視する*//**//*
                      int start=blockEntity.getStartTime();
                      int duration=blockEntity.getDuration();
                    boolean makingEntitySuccess=false;
                      if(!flag) {
                          if (duration > 0) {
                              List<BlockPos> posList = blockEntity.getPositionList();
                              if (posList != null) {
                                  blockEntity.setMoving(true);
                                  //**ブロックをエンティティ化*//**//*
                                  makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), getDegreeAngle(state,blockEntity),blockEntity.getPositionList(),0,false,BlockPos.ZERO,0);

                              }
                          }
                          if(makingEntitySuccess) {
                              //removeBlocks(level,startPos,blockEntity,state);
                              level.scheduleTick(pos, this, 0);
                              if(blockEntity.isLoop()){
                                  level.setBlock(pos, state.setValue(POWERED,true).setValue(MOVING, true), 2);
                              }else{
                                  level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                              }

                              //level.scheduleTick(pos, this, 0);
                              //removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);
                          }
                      }else{

                          boolean b1=false;
                          for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                              return (o instanceof MovingBlockEntity);
                          })) {
                              b1=true;
                              MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                              CompoundTag tag=movingBlock.getCompoundTag();
                              int angle=movingBlock.getDegreeAngle();
//                              makeMoveableBlockReverse(level,pos,startPos,start,duration,getAxis(state),angle,tag);
//                              ((MovingBlockEntity) entity).setDiscardTime(2);
                              makeMoveableBlockReverse(level,pos,movingBlock);
                          }
                          if(!b1){
                              int degree=-getDegreeAngle(state,blockEntity);
                              if(degree>=180){
                                  degree=-180;
                              }else if(degree<=-180){
                                  degree=180;
                              }
                              boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
                              int degree0=degree;
                             // int visualDegree0=blockEntity.getVisualDegree();

                              if(invertRotation&&(degree0%90)==0){
                                  //  degree0=-degree0;
                                  //  visualDegree0=-visualDegree0;
                              }
                              List<BlockPos> rotatedPosList = Utils.rotatePosList(blockEntity.getPositionList(),startPos,startPos,state.getValue(FACING).getAxis(),getDegreeAngle(state,blockEntity));
                              List<BlockPos> rotatedBasketPosList = Utils.rotateBasketPosList(blockEntity.getBasketPosList(),BlockPos.ZERO,startPos,state.getValue(FACING).getAxis(),-getDegreeAngle(state,blockEntity),blockEntity.getBasketOriginPosList());
                              List<BlockPos> rotatedBasketPosList = blockEntity.getBasketPosList();
                              for(int j=0;j<rotatedBasketPosList.size();j++){
                                  rotatedBasketPosList.set(j,rotatedBasketPosList.get(j).offset(startPos.getX(),startPos.getY(),startPos.getZ()));
                              }
                              rotatedPosList.addAll(rotatedBasketPosList);
                              if(!blockEntity.isLoop()){
                                //  int visualDegree= blockEntity.getVisualDegree();

                                  /**ブロックをエンティティ化*//*
                                 boolean makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),rotatedPosList, getVisualDegreeAngle(state,blockEntity),true,BlockPos.ZERO,0);
                                  if(makingEntitySuccess) {
                                      blockEntity.setMoving(true);
                                      level.setBlock(pos, state.cycle(POWERED).setValue(MOVING, true), 2);
                                      //removeBlocks(level,pos.relative(state.getValue(FACING)),blockEntity,state);
                                      level.scheduleTick(pos, this, 0);
                                  }
                              }else{
                                  //removeBlocks(level,startPos,blockEntity,state);
                                  makingEntitySuccess= Utils.makeMoveableBlock(level, pos, startPos, start, duration, getAxis(state), -getDegreeAngle(state,blockEntity),blockEntity.isNotFirstTime()? rotatedPosList: blockEntity.getPositionList(), 0,true,BlockPos.ZERO,0);
                                  //removeBlocks(level,startPos,blockEntity,state);
                                  level.scheduleTick(pos, this, 0);
                              }
                          }else{
                              blockEntity.setMoving(true);
                              level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                          }
                         if(!blockEntity.isLoop()){
                              blockEntity.setMoving(true);
                              level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,true), 2);
                                  level.scheduleTick(pos, this, 2);
                          }else{
                             removeBlocks(level,startPos,blockEntity,state);
                             blockEntity.setMoving(false);
                             level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,false), 2);
                             //level.scheduleTick(pos, this, 2);

                         }
                        if(blockEntity.isLoop()){
                             level.scheduleTick(pos, this, 0);
                             //removeBlocks(level,startPos,blockEntity,state);
                             blockEntity.setMoving(false);
                             level.setBlock(pos, state.cycle(POWERED).setValue(MOVING,false), 2);
                         }

                      }
                   }else if(blockEntity.hasCards()&&blockEntity.isLoop()&&blockEntity.getTickCount()>2&&state.getValue(POWERED)){
                  //  level.setBlockAndUpdate(pos, state.setValue(IGNORE, true));
                    level.scheduleTick(pos, this, 0);
                   ModCoreUgoBlock.logger.info("AAAA");
                    //removeBlocks(level,startPos,blockEntity,state);
                           //blockEntity.setMoving(false);
                    //       level.setBlock(pos, state.setValue(POWERED,false).setValue(MOVING,false).setValue(IGNORE,true), 82);
                       //    level.scheduleTick(pos, this, 2);
                       for (Entity entity : level.getEntities((Entity) null, new AABB(startPos).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                           return (o instanceof MovingBlockEntity);
                       })) {
                           MovingBlockEntity movingBlock= (MovingBlockEntity) entity;
                         //  if(movingBlock.getTimeCount()<10){
                               removeBlocks(level,startPos,blockEntity,state);
                        //   }
                           movingBlock.setDiscardTime(-1);
                       }
                    blockEntity.setTickCount(0);
                    blockEntity.setMoving(false);
                    //level.setBlock(pos, state.setValue(POWERED,false).setValue(MOVING,false).setValue(IGNORE,true), 2);
                    blockEntity.setNotFirstTime(false);
                    level.setBlockAndUpdate(pos, state.setValue(POWERED,false).setValue(MOVING,false).setValue(IGNORE,true));
                }

            }

    }*/
    public static int getDegreeAngle(BlockState state, RotationControllerBlockEntity blockEntity){
        Direction controllerDirection=state.getValue(FACING);
        boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
        if(invertRotation){
          return   -blockEntity.getDegreeAngle();
        }
        return blockEntity.getDegreeAngle();
    }
    public static int getVisualDegreeAngle(BlockState state, RotationControllerBlockEntity blockEntity){
        Direction controllerDirection=state.getValue(FACING);
        boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
        /*if(invertRotation){
            return   -blockEntity.getVisualDegree();
        }*/
        return blockEntity.getVisualDegree();
    }

   /* public void makeMoveableBlock(Level level, BlockPos controllerPos, BlockPos startPos, int start, int duration, Direction.Axis axis, int degree, List<BlockPos> positionList, int visualDegree, boolean rotateState){
        BlockState controllerState=level.getBlockState(controllerPos);
        if(controllerState.getBlock() instanceof RotationControllerBlock&&level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            CompoundTag entityTag=new CompoundTag();
            CompoundTag posTag=new CompoundTag();
            List<BlockPos> posList=rotationControllerBlockEntity.getPositionList();

            CompoundTag stateTag=new CompoundTag();
            CompoundTag blockEntityTag=new CompoundTag();
            Direction controllerDirection=controllerState.getValue(FACING);
            boolean invertRotation=controllerDirection==Direction.NORTH||controllerDirection==Direction.WEST||controllerDirection==Direction.DOWN;
            if(invertRotation){
                degree=-degree;
                visualDegree=-visualDegree;
            }
            for(int i=0; i<posList.size();i++){
                BlockPos eachPos=positionList.get(i);
                BlockState eachState=level.getBlockState(eachPos);
                BlockEntity eachBlockEntity = level.getBlockEntity(eachPos);
                if (eachBlockEntity != null&&i!=positionList.indexOf(controllerPos)&&!(eachBlockEntity instanceof PistonMovingBlockEntity)) {

                    blockEntityTag.put("blockEntity_" + String.valueOf(i),eachBlockEntity.saveWithFullMetadata());
                }else if (eachBlockEntity instanceof PistonMovingBlockEntity pistonMovingBlockEntity) {
                    eachState=pistonMovingBlockEntity.getMovedState();
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }else if (eachState.getBlock() instanceof PressurePlateBlock||eachState.getBlock() instanceof ButtonBlock||eachState.getBlock() instanceof TripWireBlock) {
                    eachState=eachState.setValue(ButtonBlock.POWERED,false);
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }else{
                    blockEntityTag.put("blockEntity_" + String.valueOf(i),new CompoundTag());
                }
                if(eachState.is(Register.TAG_DISABLE_MOVING)||i==positionList.indexOf(controllerPos)){
                    eachState=Blocks.AIR.defaultBlockState();
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)&&axis== Direction.Axis.Y){
                    Direction oldDirection=eachState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction newDirection=oldDirection;
                    if(degree==90){
                        newDirection=oldDirection.getCounterClockWise();
                    }else if(degree==-90){
                        newDirection=oldDirection.getClockWise();
                    }else if(degree==180||degree==-180){
                        newDirection=oldDirection.getOpposite();
                    }
                    eachState=eachState.setValue(BlockStateProperties.HORIZONTAL_FACING,newDirection);
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.AXIS)){
                    Direction.Axis oldAxis=eachState.getValue(BlockStateProperties.AXIS);
                    Direction.Axis newAxis=oldAxis;
                    if(degree==90||degree==-90){
                        if(axis== Direction.Axis.X){
                            newAxis= oldAxis== Direction.Axis.Y? Direction.Axis.Z :oldAxis== Direction.Axis.X? Direction.Axis.X : Direction.Axis.Y;
                        }else if(axis== Direction.Axis.Y){
                            newAxis= oldAxis== Direction.Axis.X? Direction.Axis.Z :oldAxis== Direction.Axis.Y? Direction.Axis.Y : Direction.Axis.X;
                        }else if(axis== Direction.Axis.Z){
                            newAxis= oldAxis== Direction.Axis.X? Direction.Axis.Y :oldAxis== Direction.Axis.Z? Direction.Axis.Z : Direction.Axis.X;
                        }
                    }
                    eachState=eachState.setValue(BlockStateProperties.AXIS,newAxis);
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.FACING)){
                    Direction oldDirection=eachState.getValue(BlockStateProperties.FACING);
                    Direction newDirection=oldDirection;
                    if(degree==90){
                        newDirection=oldDirection.getCounterClockWise(getAxis(controllerState));
                    }else if(degree==-90){
                        newDirection=oldDirection.getClockWise(getAxis(controllerState));
                    }else if(degree==180||degree==-180){
                        newDirection=oldDirection.getClockWise(getAxis(controllerState)).getClockWise(getAxis(controllerState));
                    }
                    eachState=eachState.setValue(BlockStateProperties.FACING,newDirection);
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.HALF)&&getAxis(controllerState)!= Direction.Axis.Y){
                    Half oldHalf=eachState.getValue(BlockStateProperties.HALF);
                    Half newHalf=oldHalf;
                    if(degree==180||degree==-180){
                        if(oldHalf==Half.BOTTOM){
                            newHalf=Half.TOP;
                        }else if(oldHalf==Half.TOP){
                            newHalf=Half.BOTTOM;
                        }
                    }
                    eachState=eachState.setValue(BlockStateProperties.HALF,newHalf);
                }
                if(rotateState&&eachState.hasProperty(BlockStateProperties.SLAB_TYPE)&&getAxis(controllerState)!= Direction.Axis.Y){
                    SlabType oldHalf=eachState.getValue(BlockStateProperties.SLAB_TYPE);
                    SlabType newHalf=oldHalf;
                    if(degree==180||degree==-180){
                        if(oldHalf==SlabType.BOTTOM){
                            newHalf=SlabType.TOP;
                        }else if(oldHalf==SlabType.TOP){
                            newHalf=SlabType.BOTTOM;
                        }
                    }
                    eachState=eachState.setValue(BlockStateProperties.SLAB_TYPE,newHalf);
                }
                eachState= eachState.hasProperty(BlockStateProperties.WATERLOGGED) ? eachState.setValue(BlockStateProperties.WATERLOGGED, false) : level.getFluidState(eachPos).isEmpty() ? eachState : Blocks.AIR.defaultBlockState();
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(new BlockPos(posList.get(i).getX() - startPos.getX(), posList.get(i).getY() - startPos.getY(), posList.get(i).getZ() - startPos.getZ())));
                stateTag.put("state_" + String.valueOf(i), NbtUtils.writeBlockState(eachState));

            }
            entityTag.put("positionList",posTag);
            entityTag.put("stateList",stateTag);
            entityTag.put("blockEntityList",blockEntityTag);

            MovingBlockEntity moveableBlock = new MovingBlockEntity(level, startPos, level.getBlockState(controllerPos), start + 1, duration, axis,degree,entityTag,visualDegree,rotationControllerBlockEntity.isLoop(),!rotateState,BlockPos.ZERO);
            rotationControllerBlockEntity.setVisualDegree(degree);
            if (!level.isClientSide) {
                level.addFreshEntity(moveableBlock);
            }

        }

    }*/

    public static void makeMoveableBlockReverse(Level level, BlockPos controllerPos, MovingBlockEntity movingBlock){
        if(level.getBlockEntity(controllerPos) instanceof RotationControllerBlockEntity rotationControllerBlockEntity&&rotationControllerBlockEntity.getItem(0).getItem()==Register.shape_card.get()&&rotationControllerBlockEntity.getItem(0).getTag().contains("positionList")) {
            movingBlock.setTimeCount(0);
            movingBlock.setDuration(movingBlock.getDuration());
            movingBlock.setStartRotation(movingBlock.getDegreeAngle());
            rotationControllerBlockEntity.setVisualDegree(movingBlock.getDegreeAngle());
            movingBlock.setDegreeAngle(-movingBlock.getDegreeAngle());
            movingBlock.setShouldRotateState(false);

        }

    }
    public static void destroyOldBlock(Level level,BlockPos pos){
        BlockState state=level.getBlockState(pos);
        if(!state.is(Register.TAG_DISABLE_MOVING)) {
            level.removeBlockEntity(pos);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 82);
        }
    }
    public static void updateDestroyedPos(Level level,BlockPos pos){
        BlockState state=level.getBlockState(pos);
        if(!state.is(Register.TAG_DISABLE_MOVING)) {
            level.removeBlockEntity(pos);
            level.updateNeighborsAt(pos,state.getBlock());
        }
    }

    public RenderShape getRenderShape(BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotationControllerBlockEntity(pos,state);
    }
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152160_, BlockState p_152161_, BlockEntityType<T> p_152162_) {
        return createTickerHelper(p_152162_, Register.RotationController.get(), RotationControllerBlockEntity::tick);
    }
    public BlockState getStateForPlacement(BlockPlaceContext p_55087_) {
        return this.defaultBlockState().setValue(FACING, p_55087_.getNearestLookingDirection().getOpposite());
    }
    public BlockState rotate(BlockState p_55115_, Rotation p_55116_) {
        return p_55115_.setValue(FACING, p_55116_.rotate(p_55115_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_55112_, Mirror p_55113_) {
        return p_55112_.rotate(p_55113_.getRotation(p_55112_.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, livingEntity, stack);
        if(livingEntity instanceof Player&&state.getBlock() instanceof RotationControllerBlock){
            ItemEntity itemEntity1=new ItemEntity(level,livingEntity.getX(),livingEntity.getY()+1D,livingEntity.getZ(),new ItemStack(Register.shape_card.get()));
            if(!level.isClientSide){
                level.addFreshEntity(itemEntity1);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean b) {
        if(state2.isAir()) {

            for (Entity entity : level.getEntities((Entity) null, new AABB(pos.relative(state.getValue(FACING))).move(0.5D, 0.5D, 0.5D).inflate(0d, 0.1d, 0d), (o) -> {
                return (o instanceof MovingBlockEntity);
            })) {
                MovingBlockEntity movingBlock = (MovingBlockEntity) entity;
                List<BlockPos> posList=movingBlock.getPosList();
                posList.addAll(movingBlock.getBasketPosList());
                    if(movingBlock.shouldRotate()) {
                        for (int i=0;i<posList.size();i++) {
                            BlockState movingState=movingBlock.getStateList().get(i);
                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, pos.getCenter()).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.THIS_ENTITY, movingBlock);
                            for (ItemStack itemStack : movingState.getDrops(lootparams$builder)) {
                                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                                if (!level.isClientSide) {
                                    level.addFreshEntity(itemEntity);
                                }
                            }
                        }
                        movingBlock.getPassengers().forEach(Entity::discard);
                        movingBlock.discard();
                    }
                    break;
            }
            level.removeBlockEntity(pos);
        }

        super.onRemove(state2, level, pos, state2, b);
    }
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {
        list.add(Component.translatable("info.ugoblock.rotation_controller").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.rotation_controller_scroll").withStyle(ChatFormatting.GREEN));
        list.add(Component.translatable("info.ugoblock.block_imitatable").withStyle(ChatFormatting.GREEN));
    }
}
