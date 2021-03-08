package com.minecraftabnormals.momentum.common.enchantment;

import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class MomentumEnchantment extends Enchantment {
    public MomentumEnchantment() {
        super(Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    public boolean canApplyTogether(Enchantment ench) {
        return ench instanceof EfficiencyEnchantment ? false : super.canApplyTogether(ench);
    }

    public boolean isTreasureEnchantment() {
        return true;
    }
}
