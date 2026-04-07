package com.camcamcamcamcam.momentum;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class MomentumEvents {

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        boolean toolHasMomentum = EnchantmentHelper.has(player.getItemInHand(InteractionHand.MAIN_HAND), Momentum.MOMENTUM.get());
        if (!toolHasMomentum) {
            return;
        }

        boolean isBlockInstaminable = event.getState().getDestroySpeed(player.level(), event.getPos()) == 0;
        if (isBlockInstaminable) {
            return;
        }

        String currentBlock = event.getState().getBlock().getName().getString();
        String lastBlockMinedName = player.getData(Momentum.LAST_BLOCK_MINED.get());

        if (!currentBlock.equals(lastBlockMinedName)) {
            addOneToBlocksMined(player);
        } else {
            resetBlocksMinedToZero(player);
            player.setData(Momentum.LAST_BLOCK_MINED, currentBlock);
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
        
    }

}
