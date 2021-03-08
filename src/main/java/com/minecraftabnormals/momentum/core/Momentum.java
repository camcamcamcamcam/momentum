package com.minecraftabnormals.momentum.core;

import com.minecraftabnormals.momentum.core.registry.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Momentum.MODID)
@Mod.EventBusSubscriber(modid = Momentum.MODID)
public class Momentum
{
    public static final String MODID = "momentum";

    public Momentum() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Enchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::setupCommon);

    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            Enchantments.registerTrackedData();
            Enchantments.registerLootInjectors();
        });
    }
}
