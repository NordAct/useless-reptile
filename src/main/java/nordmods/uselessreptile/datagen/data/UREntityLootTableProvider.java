package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.loottable.EntityLootTableGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UREntityLootTableProvider extends EntityLootTableGenerator implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final Map<EntityType<?>, LootTable.Builder> lootTables = new HashMap<>();
    public UREntityLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(FeatureSet.empty(), registryLookupFuture.join());
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_table");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public void generate() {
        add(UREntities.WYVERN_ENTITY, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(URItems.WYVERN_SKIN)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(1, 3)))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.BONE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(1, 3)))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.ROTTEN_FLESH)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0, 2)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 1))))));

        add(UREntities.RIVER_PIKEHORN_ENTITY, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.COD)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 3)))
                                .apply(FurnaceSmeltLootFunction.builder().conditionally(createSmeltLootCondition())))
                        .with(ItemEntry.builder(Items.SALMON)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 3)))
                                .apply(FurnaceSmeltLootFunction.builder().conditionally(createSmeltLootCondition())))
                        .with(ItemEntry.builder(Items.TROPICAL_FISH)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 3))))
                        .with(ItemEntry.builder(Items.PUFFERFISH)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 3))))));

        add(UREntities.MOLECLAW_ENTITY, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.HANGING_ROOTS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(1, 3)))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.GLOW_LICHEN)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0, 2)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 2))))));

        add(UREntities.LIGHTNING_CHASER_ENTITY, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.IRON_NUGGET)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8, 16)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(8, 16)))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.BONE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0, 4)))
                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0, 4))))));
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            generate();
            List<CompletableFuture<?>> list = new ArrayList<>();
            lootTables.forEach((type, loot) -> {
                    LootTable lootTable = loot.build();
                    Path path = pathResolver.resolveJson(type.getLootTableKey().getValue());
                    list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, LootTable.CODEC, lootTable, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    private void add(EntityType<? extends Entity> type, LootTable.Builder builder) {
        lootTables.put(type, builder);
    }

    @Override
    public String getName() {
        return "Entity Loot Tables";
    }
}
