package com.minecraftabnormals.momentum;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.IDataManager;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class MomentumEnchantment extends Enchantment {

    public static final TrackedData<Integer> BLOCKS_MINED = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();
    public static final TrackedData<ResourceLocation> LAST_BLOCK = TrackedData.Builder.create(DataProcessors.RESOURCE_LOCATION, () -> new ResourceLocation("air")).enableSaving().build();
    public static final TrackedData<Boolean> SOUND_PLAYED = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().build();

    public MomentumEnchantment() {
        super(Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return !(ench instanceof EfficiencyEnchantment) && super.checkCompatibility(ench);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @SubscribeEvent
    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        ResourceLocation currentBlock = event.getState().getBlock().getRegistryName();
        PlayerEntity player = event.getPlayer();
        IDataManager playerManager = ((IDataManager) player);
        float hardness = event.getState().getDestroySpeed(player.level, event.getPos());

        if (EnchantmentHelper.getItemEnchantmentLevel(Momentum.MOMENTUM.get(), player.getMainHandItem()) > 0
                && playerManager.getValue(LAST_BLOCK).toString().equals(currentBlock.toString()))
        {
            float speedFactor = (float) Math.pow(Math.pow(2, -1.0 / 16 * hardness + 3.0 / 16) + 1, playerManager.getValue(BLOCKS_MINED) + 1);
            if (playerManager.getValue(BLOCKS_MINED) + 1 >= 8 * Math.sqrt(hardness) && !MomentumConfig.COMMON.noMoving.get()) {
                speedFactor = Math.min(22 * hardness / player.getMainHandItem().getDestroySpeed(event.getState()), speedFactor);
                if (!playerManager.getValue(SOUND_PLAYED)) {
                    event.getPlayer().level.playSound(player, event.getPos(), Momentum.MOMENTUM_HALT.get(), SoundCategory.PLAYERS, 0.2f, 1.0f);
                    playerManager.setValue(SOUND_PLAYED, true);
                }
            }
            event.setNewSpeed(event.getOriginalSpeed() * speedFactor);
        }
    }

    @SubscribeEvent
    public static void onBlockDestroy(BlockEvent.BreakEvent event) {
        ResourceLocation currentBlock = event.getState().getBlock().getRegistryName();
        PlayerEntity player = event.getPlayer();
        IDataManager playerManager = ((IDataManager) player);
        boolean isBlockInstaminable = event.getState().getDestroySpeed(player.level, event.getPos()) == 0;

        if (EnchantmentHelper.getItemEnchantmentLevel(Momentum.MOMENTUM.get(), player.getMainHandItem()) > 0
                && playerManager.getValue(LAST_BLOCK).toString().equals(currentBlock.toString())) {
            playerManager.setValue(BLOCKS_MINED, playerManager.getValue(BLOCKS_MINED) + 1);
        } else if (!isBlockInstaminable) {
            playerManager.setValue(BLOCKS_MINED, 0);
            playerManager.setValue(SOUND_PLAYED, false);
        }
        if (!isBlockInstaminable) {
            playerManager.setValue(LAST_BLOCK, currentBlock);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase == TickEvent.Phase.START)
            return;

        if (!MomentumConfig.COMMON.noMoving.get())
            return;

        PlayerEntity player = event.player;
        IDataManager playerManager = ((IDataManager) player);

        EffectInstance haste = player.getEffect(Effects.DIG_SPEED);
        int hasteLevel = -1;
        if (haste != null)
            hasteLevel = haste.getAmplifier();


        double disX = player.getX() - player.xOld;
        double disZ = player.getZ() - player.zOld;

        if (player.getDeltaMovement().length() == 0.0 || disX == 0.0 && disZ == 0.0) {
            return;
        }

        if (player.isShiftKeyDown())
            return;
        if (!player.isSprinting() && hasteLevel >= 0)
            return;
        if (player.isSprinting() && hasteLevel >= 1)
            return;

        if (playerManager.getValue(BLOCKS_MINED) == 0)
            return;

        playerManager.setValue(BLOCKS_MINED, 0);
        playerManager.setValue(SOUND_PLAYED, false);
        player.level.playSound(player, player.blockPosition(), Momentum.MOMENTUM_HALT.get(), SoundCategory.PLAYERS, 0.2f, 1.0f);

    }

}
