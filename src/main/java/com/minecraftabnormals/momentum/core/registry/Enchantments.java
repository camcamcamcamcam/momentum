package com.minecraftabnormals.momentum.core.registry;

import com.minecraftabnormals.momentum.common.enchantment.MomentumEnchantment;
import com.minecraftabnormals.momentum.core.Momentum;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Enchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Momentum.MODID);

    public static final RegistryObject<Enchantment> MOMENTUM = ENCHANTMENTS.register("momentum", MomentumEnchantment::new);
}
