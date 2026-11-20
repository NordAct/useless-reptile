package nordmods.uselessreptile.datagen.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URItems;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class URChestLootTableProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    public URChestLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.CHEST);
        this.registryLookupFuture = registryLookupFuture;
    }
    public static final ResourceKey<LootTable> LIGHTNING_CHASER_NEST = ResourceKey.create(Registries.LOOT_TABLE, UselessReptile.id("chests/lightning_chaser_nest"));

    @Override
    public void generate(@NonNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        try {
            HolderLookup.Provider provider = registryLookupFuture.get();
            HolderGetter<Enchantment> enchantments = provider.lookupOrThrow(Registries.ENCHANTMENT);
            output.accept(
                    LIGHTNING_CHASER_NEST,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(UniformGenerator.between(2, 4))
                                            .add(TagEntry.tagContents(ConventionalItemTags.NUGGETS)
                                                    .setWeight(70)
                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 27))))
                                            .add(TagEntry.tagContents(ConventionalItemTags.RAW_MATERIALS)
                                                    .setWeight(30)
                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                            )
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(2))
                                            .add(LootItem.lootTableItem(URItems.MOLECLAW_HELMET_COPPER)
                                                    .setWeight(3)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_HELMET_COPPER)
                                                    .setWeight(3)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_CHESTPLATE_COPPER)
                                                    .setWeight(3)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_TAIL_ARMOR_COPPER)
                                                    .setWeight(3)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_HELMET)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_BOOTS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_SPEAR)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_HELMET)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_BOOTS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.COPPER_SPEAR)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))

                                            .add(LootItem.lootTableItem(URItems.MOLECLAW_HELMET_IRON)
                                                    .setWeight(2)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_HELMET_IRON)
                                                    .setWeight(2)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_CHESTPLATE_IRON)
                                                    .setWeight(2)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_TAIL_ARMOR_IRON)
                                                    .setWeight(2)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_BOOTS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_SPEAR)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_BOOTS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.IRON_SPEAR)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))

                                            .add(LootItem.lootTableItem(URItems.MOLECLAW_HELMET_GOLD)
                                                    .setWeight(1)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_HELMET_GOLD)
                                                    .setWeight(1)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_CHESTPLATE_GOLD)
                                                    .setWeight(1)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(URItems.DRAGON_TAIL_ARMOR_GOLD)
                                                    .setWeight(1)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_BOOTS)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_SPEAR)
                                                    .setWeight(1)
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                                    .setWeight(1)
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_CHESTPLATE)
                                                    .setWeight(1)
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_LEGGINGS)
                                                    .setWeight(1)
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_BOOTS)
                                                    .setWeight(1)
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))

                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                            .add(LootItem.lootTableItem(Items.GOLDEN_SPEAR)
                                                    .setWeight(1)
                                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(provider, UniformGenerator.between(0, 20)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.8f, 1)))
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))

                                            .add(LootItem.lootTableItem(Items.BOOK)
                                                    .setWeight(5)
                                                    .apply(new EnchantRandomlyFunction.Builder()
                                                            .withOneOf(
                                                                    HolderSet.direct(
                                                                            enchantments.getOrThrow(Enchantments.CHANNELING),
                                                                            enchantments.getOrThrow(Enchantments.LOYALTY)
                                                                    )
                                                            )
                                                    )
                                            )
                                            .add(LootItem.lootTableItem(Items.TRIDENT)
                                                    .setWeight(1)
                                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                                    .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.05f, 0.5f)))
                                            )
                                            .add(EmptyLootItem.emptyItem().setWeight(40))
                            )
            );
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
