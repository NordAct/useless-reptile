package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonVariantProvider implements DataProvider {
    private static final List<Tuple<Identifier, DragonVariant>> holder = new ArrayList<>();
    private static final List<Tuple<Identifier, DragonVariant>> holderCustomName = new ArrayList<>();
    protected final FabricDataOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final PackOutput.PathProvider pathResolverCustomName;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public URDragonVariantProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/variant");
        this.pathResolverCustomName = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/custom_name");
        this.registryLookupFuture = registryLookupFuture;
    }

    public void addEntry(Identifier id, DragonVariant variant) {
        holder.add(new Tuple<>(id, variant));
    }

    public void addCustomNameEntry(Identifier id, DragonVariant variant) {
        holderCustomName.add(new Tuple<>(id, variant));
    }

    public void addCustomNameEntry(Identifier id, String name) {
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "",
                Optional.empty(),
                UselessReptile.id("wyvern/" + name),
                UselessReptile.id("wyvern"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                -1,
                Optional.empty(),
                Optional.empty()
        );
        addCustomNameEntry(getId(id, name), variant);
    }


    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(variant -> {
                Path path = pathResolver.json(variant.getA());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DragonVariant.CODEC, variant.getB(), path));
            });
            holderCustomName.forEach(variant -> {
                Path path = pathResolverCustomName.json(variant.getA());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DragonVariant.CODEC, variant.getB(), path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntries() {
        addWyvern("green");
        addWyvern("brown");
        addCustomNameEntry(EntityType.getKey(UREntities.WYVERN_ENTITY), "jeb_");

        addMoleclaw("black", false);
        addMoleclaw("brown", false);
        addMoleclaw("grey", false);
        addMoleclaw("albino", true);

        addLightningChaser("blue");
        addLightningChaser("grey");
        addLightningChaser("brown");
        addLightningChaser("purple");

        addRiverPikehorn("green");
        addRiverPikehorn("dark_green");
        addRiverPikehorn("blue");
        addRiverPikehorn("dark_blue");
        addRiverPikehorn("purple");
        addRiverPikehorn("dark_purple");
        addRiverPikehorn("teal");
        addRiverPikehorn("dark_teal");

        addMagmamuncher("netherrack");
        addMagmamuncher("magma");
    }

    protected void addWyvern(String name) {
        Identifier id = EntityType.getKey(UREntities.WYVERN_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "variant.uselessreptile.wyvern." + name,
                Optional.empty(),
                UselessReptile.id("wyvern/" + name),
                UselessReptile.id("wyvern"),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.WYVERN_SADDLES.location(), true))),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(UselessReptile.id("wyvern/default")),
                Optional.empty(),
                128,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2)
                                ),
                                new DragonVariant.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 1)
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false), 4),
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false), 4)
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addMoleclaw(String name, boolean rare) {
        Identifier id = EntityType.getKey(UREntities.MOLECLAW_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "variant.uselessreptile.moleclaw." + name,
                Optional.empty(),
                UselessReptile.id("moleclaw/" + name),
                UselessReptile.id("moleclaw"),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.MOLECLAW_SADDLES.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.MOLECLAW_HELMETS.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.MOLECLAW_CHESTPLATES.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.MOLECLAW_TAIL_ARMOR.location(), true))),
                Optional.of(UselessReptile.id("moleclaw/" + (rare ? "rare" : "default"))),
                Optional.empty(),
                64,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BEETROOT.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2)
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(ConventionalItemTags.VEGETABLE_FOODS.location(), true), 2)
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addRiverPikehorn(String name) {
        Identifier id = EntityType.getKey(UREntities.RIVER_PIKEHORN_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "variant.uselessreptile.river_pikehorn." + name,
                Optional.empty(),
                UselessReptile.id("river_pikehorn/" + name),
                UselessReptile.id("empty"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(UselessReptile.id("river_pikehorn/default")),
                Optional.empty(),
                1,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.TROPICAL_FISH_BUCKET.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 1)
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.FISHES.location(), true), 3)
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addLightningChaser(String name) {
        Identifier id = EntityType.getKey(UREntities.LIGHTNING_CHASER_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "variant.uselessreptile.lightning_chaser." + name,
                Optional.empty(),
                UselessReptile.id("lightning_chaser/" + name),
                UselessReptile.id("lightning_chaser"),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.LIGHTNING_CHASER_SADDLES.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.LIGHTNING_CHASER_HELMETS.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.LIGHTNING_CHASER_CHESTPLATES.location(), true))),
                Optional.of(List.of(new ExtraCodecs.TagOrElementLocation(URTags.LIGHTNING_CHASER_TAIL_ARMOR.location(), true))),
                Optional.of(UselessReptile.id("lightning_chaser/" + name)),
                Optional.empty(),
                3,
                Optional.empty(),
                Optional.of(
                        List.of(
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 3)
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addMagmamuncher(String name) {
        Identifier id = EntityType.getKey(UREntities.MAGMAMUNCHER_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                "variant.uselessreptile.magmamuncher." + name,
                Optional.empty(),
                UselessReptile.id("magmamuncher/" + name),
                UselessReptile.id("empty"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(UselessReptile.id("magmamuncher/default")),
                Optional.empty(),
                12,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BLAZE_ROD.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 3)
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_BLOCK.builtInRegistryHolder().key().identifier(), false), 4),
                                new DragonVariant.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_CREAM.builtInRegistryHolder().key().identifier(), false), 2)
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected Identifier getId(Identifier dragonId, String name) {
        return Identifier.fromNamespaceAndPath(dragonId.getNamespace(), dragonId.getPath() + "/" + name);
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Variant";
    }
}
