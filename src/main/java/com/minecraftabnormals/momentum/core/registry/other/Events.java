package com.minecraftabnormals.momentum.core.registry.other;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.momentum.core.Momentum;
import com.minecraftabnormals.momentum.core.registry.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class Events {

    @SubscribeEvent
    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        ResourceLocation currentBlock = event.getState().getBlock().getRegistryName();
        PlayerEntity player = event.getPlayer();
        IDataManager playerManager = ((IDataManager) player);
        float hardness = event.getState().getBlockHardness(player.getEntityWorld(), event.getPos());

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MOMENTUM.get(), player.getHeldItemMainhand()) > 0
        && playerManager.getValue(Momentum.LAST_BLOCK).toString().equals(currentBlock.toString()))
        {
            float speedFactor = (float) Math.pow(Math.pow(2, -1.0 / 16 * hardness + 3.0 / 16) + 1, playerManager.getValue(Momentum.BLOCKS_MINED) + 1);
            event.setNewSpeed(event.getOriginalSpeed() * (playerManager.getValue(Momentum.BLOCKS_MINED) + 1 < 8 * Math.sqrt(hardness) ? speedFactor :
                    Math.min(22 * hardness / player.getHeldItemMainhand().getDestroySpeed(event.getState()), speedFactor)));
        }
    }

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {
        ResourceLocation currentBlock = event.getState().getBlock().getRegistryName();
        PlayerEntity player = event.getPlayer();
        IDataManager playerManager = ((IDataManager) player);
        boolean isBlockInstaminable = event.getState().getBlockHardness(player.getEntityWorld(), event.getPos()) != 0;

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MOMENTUM.get(), player.getHeldItemMainhand()) > 0
                && playerManager.getValue(Momentum.LAST_BLOCK).toString().equals(currentBlock.toString())) {
            playerManager.setValue(Momentum.BLOCKS_MINED, playerManager.getValue(Momentum.BLOCKS_MINED) + 1);
        } else if (isBlockInstaminable) {
            playerManager.setValue(Momentum.BLOCKS_MINED, 0);
        }
        if (isBlockInstaminable)
            playerManager.setValue(Momentum.LAST_BLOCK, currentBlock);
    }

}
