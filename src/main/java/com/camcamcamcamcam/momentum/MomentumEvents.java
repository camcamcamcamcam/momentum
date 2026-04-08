package com.camcamcamcamcam.momentum;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MomentumEvents {

    public static final int BASE = 2;
    public static final double COEFFICIENT = -1.0 / 16;
    public static final double OFFSET = -3.0 / 16;

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {

        Player player = event.getPlayer();

        if (!doesMomentumApply(player, event.getState(), event.getPos())) {
            return;
        }

        String currentBlock = getNameOfBlock(event.getState());
        String lastBlockMined = player.getData(Momentum.LAST_BLOCK_MINED.get());

        if (currentBlock.equals(lastBlockMined)) {
            addOneToBlocksMined(player);
        } else {
            resetBlocksMinedToZero(player);
            player.setData(Momentum.LAST_BLOCK_MINED, currentBlock);
        }

        if (!event.getLevel().isClientSide()) {
            int blocksMined =  player.getData(Momentum.BLOCKS_MINED);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new MomentumPayload(lastBlockMined, blocksMined));
        }
    }

    public static void addOneToBlocksMined(Player player) {
        player.setData(Momentum.BLOCKS_MINED, player.getData(Momentum.BLOCKS_MINED) + 1);
    }

    public static void resetBlocksMinedToZero(Player player) {
        player.setData(Momentum.BLOCKS_MINED, 0);
    }

    @SubscribeEvent
    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (event.getPosition().isEmpty()) {
            return;
        }

        BlockPos blockPos = event.getPosition().get();
        BlockState blockState = event.getState();

        if (!doesMomentumApply(player, blockState, blockPos)) {
            return;
        }

        String currentBlock = getNameOfBlock(blockState);
        String lastBlockMined = player.getData(Momentum.LAST_BLOCK_MINED);
        if (!currentBlock.equals(lastBlockMined)) {
            return;
        }

        float hardness = event.getState().getDestroySpeed(player.level(), blockPos);
        float multiplier = (float) Math.pow(BASE, COEFFICIENT * hardness + OFFSET) + 1;
        float speedFactor = (float) Math.pow(multiplier, player.getData(Momentum.BLOCKS_MINED) + 1);

        event.setNewSpeed(event.getOriginalSpeed() * speedFactor);
    }

    public static String getNameOfBlock(BlockState state) {
        return state.getBlock().getName().getString();
    }

    public static boolean doesMomentumApply(Player player, BlockState state, BlockPos pos) {
        boolean toolHasMomentum = EnchantmentHelper.has(player.getItemInHand(InteractionHand.MAIN_HAND),
                Momentum.MOMENTUM.get());
        if (!toolHasMomentum) {
            return false;
        }

        boolean isBlockInstaminable = state.getDestroySpeed(player.level(), pos) == 0;
        if (isBlockInstaminable) {
            return false;
        }
        return true;
    }

    // todo Momentum II, and making momentum lose once you switch away from the tool.

}
