package com.minecraftabnormals.momentum;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.abnormals_core.core.registry.LootInjectionRegistry;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.SoundSubRegistryHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Momentum.MODID)
@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class Momentum {

    public static final String MODID = "momentum";

    public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MODID);

    public static final SoundSubRegistryHelper SOUND_HELPER = REGISTRY_HELPER.getSoundSubHelper();
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Momentum.MODID);

    public static final RegistryObject<Enchantment> MOMENTUM = ENCHANTMENTS.register("momentum", MomentumEnchantment::new);
    public static final RegistryObject<SoundEvent> MOMENTUM_HALT = SOUND_HELPER.createSoundEvent("item.momentum.halt");

    public Momentum() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRY_HELPER.getSoundSubHelper().getDeferredRegister().register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "blocks_mined"), MomentumEnchantment.BLOCKS_MINED);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "last_block"), MomentumEnchantment.LAST_BLOCK);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(Momentum.MODID, "sound_played"), MomentumEnchantment.SOUND_PLAYED);

        LootInjectionRegistry.LootInjector injector = new LootInjectionRegistry.LootInjector(Momentum.MODID);
        injector.addLootInjection(injector.buildLootPool("abandoned_mineshaft", 1, 0), LootTables.ABANDONED_MINESHAFT);
        injector.addLootInjection(injector.buildLootPool("simple_dungeon", 1, 0), LootTables.SIMPLE_DUNGEON);
        // todo change these to be percentage chances when you find out how

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MomentumConfig.COMMON_SPEC);
    }
}
