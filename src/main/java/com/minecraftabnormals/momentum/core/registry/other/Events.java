package com.minecraftabnormals.momentum.core.registry.other;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.momentum.core.Momentum;
import com.minecraftabnormals.momentum.core.registry.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
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
        float hardness = event.getState().getBlockHardness(event.getPlayer().getEntityWorld(), event.getPos());
        IDataManager player = ((IDataManager) event.getPlayer());
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MOMENTUM.get(), event.getPlayer().getHeldItemMainhand()) > 0
        && player.getValue(Enchantments.LAST_BLOCK).toString().equals(currentBlock.toString()))
        {
            float speedFactor = (float) Math.pow(Math.pow(2, -1.0/16 * hardness + 3.0/16) + 1, player.getValue(Enchantments.BLOCKS_MINED) + 1);
            event.setNewSpeed(event.getOriginalSpeed() * (player.getValue(Enchantments.BLOCKS_MINED) + 1 < 4 * hardness ? speedFactor :
                    Math.min(22 * hardness / event.getPlayer().getHeldItemMainhand().getDestroySpeed(event.getState()), speedFactor)));
        }
    }

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {
        ResourceLocation currentBlock = event.getState().getBlock().getRegistryName();
        IDataManager player = ((IDataManager) event.getPlayer());
        boolean isBlockInstaminable = event.getState().getBlockHardness(event.getPlayer().getEntityWorld(), event.getPos()) != 0;
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MOMENTUM.get(), event.getPlayer().getHeldItemMainhand()) > 0
                && player.getValue(Enchantments.LAST_BLOCK).toString().equals(currentBlock.toString()))
        {
            player.setValue(Enchantments.BLOCKS_MINED, player.getValue(Enchantments.BLOCKS_MINED) + 1);
        } else if (isBlockInstaminable) {
            player.setValue(Enchantments.BLOCKS_MINED, 0);
        }
        if (isBlockInstaminable)
            player.setValue(Enchantments.LAST_BLOCK, currentBlock);
    }
}
