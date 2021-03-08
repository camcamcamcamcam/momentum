package com.minecraftabnormals.momentum.core;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.momentum.core.registry.Enchantments;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Momentum.MODID)
@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class Momentum {

    public static final String MODID = "momentum";

    public static final TrackedData<Integer> BLOCKS_MINED = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();
    public static final TrackedData<ResourceLocation> LAST_BLOCK = TrackedData.Builder.create(DataProcessors.RESOURCE_LOCATION, () -> new ResourceLocation("air")).enableSaving().build();

    public Momentum() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Enchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "blocks_mined"), BLOCKS_MINED);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "last_block"), LAST_BLOCK);

        LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(Momentum.MODID);
        injector.addLootInjection(injector.buildLootPool("abandoned_mineshaft", 1, 0), LootTables.CHESTS_ABANDONED_MINESHAFT);
        injector.addLootInjection(injector.buildLootPool("simple_dungeon", 1, 0), LootTables.CHESTS_SIMPLE_DUNGEON);
    }
}
