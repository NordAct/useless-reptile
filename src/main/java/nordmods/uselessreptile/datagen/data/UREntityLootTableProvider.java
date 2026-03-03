package nordmods.uselessreptile.datagen.data;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UREntityLootTableProvider extends EntityLootSubProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private final Map<Pair<String, EntityType<?>>, LootTable.Builder> lootTables = new HashMap<>();
    public UREntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(FeatureFlagSet.of(), registryLookupFuture.join());
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public void generate() {
        addEntry(UREntities.WYVERN, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(URItems.WYVERN_SKIN)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(1, 3)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(1, 3)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 1))))));

        addEntry(UREntities.RIVER_PIKEHORN, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.COD)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 3)))
                                .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot())))
                        .add(LootItem.lootTableItem(Items.SALMON)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 3)))
                                .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot())))
                        .add(LootItem.lootTableItem(Items.TROPICAL_FISH)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 3))))
                        .add(LootItem.lootTableItem(Items.PUFFERFISH)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 3))))));

        addEntry(UREntities.MOLECLAW, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.HANGING_ROOTS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(1, 3)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.GLOW_LICHEN)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 2))))));

        addEntry(UREntities.LIGHTNING_CHASER, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 16)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(8, 16)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 4)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 4))))));

        addEntry(UREntities.MAGMAMUNCHER, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.NETHERRACK)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.COAL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0, 3))))));

        addEntry(UREntities.MAGMAMUNCHER, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.COAL)
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .when(LootItemRandomChanceCondition.randomChance(0.25f)))),
                "_from_magma");
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            generate();
            List<CompletableFuture<?>> list = new ArrayList<>();
            lootTables.forEach((type, loot) -> {
                String suffix = type.getFirst();
                LootTable lootTable = loot.build();
                Path path = Path.of(pathResolver.json(type.getSecond().getDefaultLootTable().get()).toString().replace(".json", suffix + ".json"));
                list.add(DataProvider.saveStable(writer, registryLookupFuture, LootTable.DIRECT_CODEC, lootTable, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    private void addEntry(EntityType<? extends Entity> type, LootTable.Builder builder, String suffix) {
        lootTables.put(new Pair<>(suffix, type), builder);
    }

    private void addEntry(EntityType<? extends Entity> type, LootTable.Builder builder) {
        addEntry(type, builder, "");
    }

    @Override
    public @NonNull String getName() {
        return "Entity Loot Tables";
    }
}
