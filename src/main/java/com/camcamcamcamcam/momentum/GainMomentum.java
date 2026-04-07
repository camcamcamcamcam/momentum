package com.camcamcamcamcam.momentum;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;

public record GainMomentum() implements EnchantmentEffectComponents {
    public static final Codec<GainMomentum> CODEC = Codec.unit(GainMomentum::new);
}
