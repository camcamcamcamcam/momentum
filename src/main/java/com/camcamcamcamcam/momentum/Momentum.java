package com.camcamcamcamcam.momentum;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Momentum.MODID)
public class Momentum {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "momentum";

    // In some registration class
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GainMomentum>> MOMENTUM =
            ENCHANTMENT_COMPONENT_TYPES.register("gain_momentum", () -> DataComponentType.<GainMomentum>builder()
                    .persistent(GainMomentum.CODEC)
                    .build());

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final Holder<SoundEvent> MOMENTUM_HALT = SOUND_EVENTS.register("momentum_halt", SoundEvent::createVariableRangeEvent);

    // Create the DeferredRegister for attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<String>> LAST_BLOCK_MINED_1 = ATTACHMENT_TYPES.register(
            "last_block_mined_1", () -> AttachmentType.builder(() -> MomentumEvents.AIR).serialize(Codec.STRING).build()
    );

    public static final Supplier<AttachmentType<Integer>> BLOCKS_MINED_1 = ATTACHMENT_TYPES.register(
            "blocks_mined_1", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<String>> LAST_BLOCK_MINED_2 = ATTACHMENT_TYPES.register(
            "last_block_mined_2", () -> AttachmentType.builder(() -> MomentumEvents.AIR).serialize(Codec.STRING).build()
    );

    public static final Supplier<AttachmentType<Integer>> BLOCKS_MINED_2 = ATTACHMENT_TYPES.register(
            "blocks_mined_2", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<String>> LAST_ITEM_HELD = ATTACHMENT_TYPES.register(
            "last_item_held", () -> AttachmentType.builder(() -> "minecraft:air").serialize(Codec.STRING).build()
    );

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Momentum(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ENCHANTMENT_COMPONENT_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(MomentumEvents.class);


    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

}
