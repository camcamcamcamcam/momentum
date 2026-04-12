package com.camcamcamcamcam.momentum;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.function.Supplier;

public class MomentumEvents {

    public static final int BASE = 2;
    public static final double COEFFICIENT = -1.0 / 16;
    public static final double OFFSET = -3.0 / 16;

    public static final float MOVEMENT_AT_REST = 0.08f;
    public static final String AIR = "Air";

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {

        Player player = event.getPlayer();

        if (doesMomentumNotApply(player, event.getState(), event.getPos())) {
            return;
        }

        String currentBlock = getNameOfBlock(event.getState());

        if (blockMatchesLastBlocksMined(player, currentBlock)) {
            addOneToBlocksMined(player, currentBlock);
        } else {
            resetBlocksMinedToZero(player, getBlockWithSmallestBlocksMined(player));
        }
        recordBlockMined(player, currentBlock);

        if (!event.getLevel().isClientSide()) {
            int[] numbersOfBlocksMined = getNumbersOfBlocksMined(player);
            String currentItemHeld = player.getItemInHand(InteractionHand.MAIN_HAND).toString();
            String[] lastBlocksMined = getLastBlocksMined(player);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new MomentumPayload(lastBlocksMined[0],
                    numbersOfBlocksMined[0], lastBlocksMined[1], numbersOfBlocksMined[1], currentItemHeld));
        }
    }

    @SubscribeEvent
    public static void onPlayerMoveOrItemSwitch(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        String lastItemHeld = player.getData(Momentum.LAST_ITEM_HELD);
        String currentItemHeld = player.getItemInHand(InteractionHand.MAIN_HAND).getItem().toString();
        // I do not for the life of me know why lastItemHeld is adding "1 " on the front in the client side.
        if (!player.level().isClientSide() && !currentItemHeld.equals(lastItemHeld)
        || player.level().isClientSide() && !currentItemHeld.equals(lastItemHeld.substring(2))) {
            resetBlocksMinedToZero(player);
            player.setData(Momentum.LAST_ITEM_HELD, currentItemHeld);
            PacketDistributor.sendToServer(new MomentumPayload(AIR, 0, AIR, 0, currentItemHeld));
        }

        MobEffectInstance playerHaste = player.getEffect(MobEffects.DIG_SPEED);
        int levelOfHaste = playerHaste == null ? -1 : playerHaste.getAmplifier();

        if (player.isSteppingCarefully()) {
            return;
        }
        if (player.isSprinting() && levelOfHaste > 0) {
            return;
        }
        if (player.getDeltaMovement().length() < MOVEMENT_AT_REST) {
            return;
        }
        if (!player.isSprinting() && levelOfHaste != -1) {
            return;
        }

        resetBlocksMinedToZero(player);
        clearLastBlocksMined(player);
        PacketDistributor.sendToServer(new MomentumPayload(AIR, 0, AIR, 0, currentItemHeld));

    }

    public static boolean blockMatchesLastBlocksMined(Player player, String currentBlock) {
        String[] lastBlocksMined = getLastBlocksMined(player);
        switch (getLevelOfMomentum(player)) {
            case 1: return currentBlock.equals(lastBlocksMined[0]);
            case 2: return currentBlock.equals(lastBlocksMined[0])
                    || currentBlock.equals(lastBlocksMined[1]);
            default: return false;
        }
    }

    public static void recordBlockMined(Player player, String blockMined) {
        String[] lastBlocksMined = getLastBlocksMined(player);
        switch (getLevelOfMomentum(player)) {
            case 1:
                player.setData(Momentum.LAST_BLOCK_MINED_1, blockMined);
            break;
            case 2: if (blockMined.equals(lastBlocksMined[0]) || lastBlocksMined[0].equals(AIR)) {
                player.setData(Momentum.LAST_BLOCK_MINED_1, blockMined);
            } else {
                player.setData(Momentum.LAST_BLOCK_MINED_2, blockMined);
            }
        }
    }

    public static void clearLastBlocksMined(Player player) {
        player.setData(Momentum.LAST_BLOCK_MINED_1, AIR);
        player.setData(Momentum.LAST_BLOCK_MINED_2, AIR);
    }

    public static Supplier<AttachmentType<Integer>> blocksMined(Player player, String block) {
        String[] lastBlocksMined = getLastBlocksMined(player);
        if (lastBlocksMined[0].equals(block)) {
            return Momentum.BLOCKS_MINED_1;
        }
        if (lastBlocksMined[1].equals(block)) {
            return Momentum.BLOCKS_MINED_2;
        }
        return null;
    }

    public static int getBlocksMined(Player player, String block) {
        Supplier<AttachmentType<Integer>> blocksMinedOfBlock = blocksMined(player, block);
        return blocksMinedOfBlock == null ? 0 : player.getData(blocksMinedOfBlock);
    }

    public static void addOneToBlocksMined(Player player, String block) {
        Supplier<AttachmentType<Integer>> blocksMinedOfBlock = blocksMined(player, block);
        if (blocksMinedOfBlock == null) {
            return;
        }
        player.setData(blocksMinedOfBlock, player.getData(blocksMinedOfBlock) + 1);
    }

    public static void resetBlocksMinedToZero(Player player) {
        System.out.println(getNumbersOfBlocksMined(player)[0] + " " + getNumbersOfBlocksMined(player)[1]);
        if (!Arrays.equals(getNumbersOfBlocksMined(player), new int[]{0, 0})) {
            player.level().playSound(player, player.blockPosition(),
                    Momentum.MOMENTUM_HALT.value(), SoundSource.PLAYERS, 0.2f, 1.0f);
        }
        player.setData(Momentum.BLOCKS_MINED_1, 0);
        player.setData(Momentum.BLOCKS_MINED_2, 0);

    }

    public static void resetBlocksMinedToZero(Player player, String block) {
        Supplier<AttachmentType<Integer>> blocksMinedOfBlock = blocksMined(player, block);
        if (blocksMinedOfBlock == null) {
            return;
        }
        player.setData(blocksMinedOfBlock, 0);
    }

    public static String getBlockWithSmallestBlocksMined(Player player) {
        int[] blocksMined = getNumbersOfBlocksMined(player);
        if (blocksMined[0] >= blocksMined[1]) {
            return player.getData(Momentum.LAST_BLOCK_MINED_2);
        } else {
            return player.getData(Momentum.LAST_BLOCK_MINED_1);
        }
    }

    @SubscribeEvent
    public static void setBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (event.getPosition().isEmpty()) {
            return;
        }

        BlockPos blockPos = event.getPosition().get();
        BlockState blockState = event.getState();

        if (doesMomentumNotApply(player, blockState, blockPos)) {
            return;
        }

        String currentBlock = getNameOfBlock(blockState);
        if (!blockMatchesLastBlocksMined(player, currentBlock)) {
            return;
        }

        int blocksMined = getBlocksMined(player, currentBlock);

        float hardness = event.getState().getDestroySpeed(player.level(), blockPos);
        float multiplier = (float) Math.pow(BASE, COEFFICIENT * hardness + OFFSET) + 1;
        float speedFactor = (float) Math.pow(multiplier, blocksMined + 1);
        event.setNewSpeed(event.getOriginalSpeed() * speedFactor);
    }

    public static String getNameOfBlock(BlockState state) {
        return state.getBlock().getName().getString();
    }

    public static String[] getLastBlocksMined(Player player) {
        return new String[]{
                player.getData(Momentum.LAST_BLOCK_MINED_1),
                player.getData(Momentum.LAST_BLOCK_MINED_2)
        };
    }

    public static int[] getNumbersOfBlocksMined(Player player) {
        return new int[]{
                player.getData(Momentum.BLOCKS_MINED_1),
                player.getData(Momentum.BLOCKS_MINED_2)
        };
    }

    public static boolean doesMomentumNotApply(Player player, BlockState state, BlockPos pos) {
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean toolHasMomentum = EnchantmentHelper.has(itemInHand,
                Momentum.MOMENTUM.get());
        if (!toolHasMomentum) {
            return true;
        }

        boolean isBlockInstaminable = state.getDestroySpeed(player.level(), pos) == 0;
        if (isBlockInstaminable) {
            return true;
        }
        return false;
    }

    public static int getLevelOfMomentum(Player player) {
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        return EnchantmentHelper.getHighestLevel(itemInHand, Momentum.MOMENTUM.get()).getSecond().intValue();
    }

    // todo and make sound when it resets, and check generation json (not doing datagen)
    // todo you'll probably also need to allow jumping and falling without haste

}
