package com.minecraftabnormals.momentum.core;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.SoundSubRegistryHelper;
import com.minecraftabnormals.momentum.core.registry.Enchantments;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.sound.midi.Track;

@Mod(Momentum.MODID)
@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class Momentum {

    public static final String MODID = "momentum";

    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);

    public static final SoundSubRegistryHelper SOUND_HELPER = REGISTRY_HELPER.getSoundSubHelper();

    public static final TrackedData<Integer> BLOCKS_MINED = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();
    public static final TrackedData<ResourceLocation> LAST_BLOCK = TrackedData.Builder.create(DataProcessors.RESOURCE_LOCATION, () -> new ResourceLocation("air")).enableSaving().build();
    public static final TrackedData<Boolean> SOUND_PLAYED = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().build();

    public static final RegistryObject<SoundEvent> MOMENTUM_HALT = SOUND_HELPER.createSoundEvent("item.momentum.halt");

    public Momentum() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRY_HELPER.getSoundSubHelper().getDeferredRegister().register(modEventBus);
        Enchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "blocks_mined"), BLOCKS_MINED);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "last_block"), LAST_BLOCK);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "sound_played"), SOUND_PLAYED);

        LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(Momentum.MODID);
        injector.addLootInjection(injector.buildLootPool("abandoned_mineshaft", 1, 0), LootTables.ABANDONED_MINESHAFT);
        injector.addLootInjection(injector.buildLootPool("simple_dungeon", 1, 0), LootTables.SIMPLE_DUNGEON);
    }
}
