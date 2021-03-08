package com.minecraftabnormals.momentum.core.registry;

import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.momentum.common.enchantment.MomentumEnchantment;
import com.minecraftabnormals.momentum.core.Momentum;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Enchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Momentum.MODID);

    public static final RegistryObject<Enchantment> MOMENTUM = ENCHANTMENTS.register("momentum", MomentumEnchantment::new);

    public static final TrackedData<Integer> BLOCKS_MINED = TrackedData.Builder.create(DataProcessors.INT, () -> 0).build();
    public static final TrackedData<ResourceLocation> LAST_BLOCK = TrackedData.Builder.create(DataProcessors.RESOURCE_LOCATION, () -> new ResourceLocation("air")).build();

    public static void registerTrackedData() {
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "blocks_mined"), BLOCKS_MINED);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "last_block"), LAST_BLOCK);
    }

    public static void registerLootInjectors() {
        LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(Momentum.MODID);
        injector.addLootInjection(injector.buildLootPool("abandoned_mineshaft", 1, 0), LootTables.CHESTS_ABANDONED_MINESHAFT);
        injector.addLootInjection(injector.buildLootPool("simple_dungeon", 1, 0), LootTables.CHESTS_SIMPLE_DUNGEON);
    }


}
