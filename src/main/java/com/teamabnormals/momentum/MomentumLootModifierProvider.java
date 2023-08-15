package com.teamabnormals.momentum;

import com.teamabnormals.blueprint.common.loot.modification.LootModifierProvider;
import com.teamabnormals.blueprint.common.loot.modification.modifiers.LootPoolEntriesModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class MomentumLootModifierProvider extends LootModifierProvider {

    public MomentumLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(Momentum.MODID, output, lookupProvider);
    }

    @Override
    protected void registerEntries(HolderLookup.Provider provider) {
        addNoneReplacingModifier("add_momentum_to_simple_dungeon", createMomentumEnchantedWithCondition(Items.BOOK, 1, 0, 1, LootItemRandomChanceCondition.randomChance(0.75F)), BuiltInLootTables.SIMPLE_DUNGEON);
        addNoneReplacingModifier("add_momentum_to_abandoned_mineshaft", createMomentumEnchantedWithCondition(Items.IRON_PICKAXE, 3,0, 1, LootItemRandomChanceCondition.randomChance(0.5F)), BuiltInLootTables.ABANDONED_MINESHAFT);
    }

    private static LootPoolEntryContainer createMomentumEnchantedWithCondition(ItemLike item, int weight, int min, int max, LootItemCondition.Builder condition) {
        return LootItem.lootTableItem(item).setWeight(weight).when(condition).apply((new EnchantRandomlyFunction.Builder()).withEnchantment(Momentum.MOMENTUM.get())).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))).build();
    }

    private void addNoneReplacingModifier(String name, LootPoolEntryContainer lootEntry, ResourceLocation... modifierLocations) {
        entry(name).selects(modifierLocations).addModifier(new LootPoolEntriesModifier(false, 1, Collections.singletonList(lootEntry)));
    }
}
