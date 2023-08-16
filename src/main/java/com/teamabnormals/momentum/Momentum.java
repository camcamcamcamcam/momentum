package com.teamabnormals.momentum;

import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import com.teamabnormals.blueprint.core.util.registry.SoundSubRegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MomentumConfig.COMMON_SPEC);
        modEventBus.addListener(this::gatherData);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator =  event.getGenerator();
        generator.addProvider(event.includeServer(), new MomentumLootModifierProvider(generator));
    }
}
