package nordmods.uselessreptile.datagen.data.mod;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.*;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.init.URRegistries;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class URDragonVariantProvider implements DataProvider {
    private static final List<Pair<Identifier, DragonVariant>> holder = new ArrayList<>();
    protected final FabricPackOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public URDragonVariantProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/variant");
        this.registryLookupFuture = registryLookupFuture;
    }

    public void addEntry(Identifier id, DragonVariant variant) {
        holder.add(new Pair<>(id, variant));
    }

    public void addSecretEntry(Identifier id, DragonVariant variant) {
        holder.add(new Pair<>(id, variant));
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(variant -> {
                Path path = pathResolver.json(variant.getFirst());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DragonVariant.CODEC, variant.getSecond(), path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntries() {
        addWyvern("green");
        addWyvern("brown");
        CommonDragonVariantData jeb_ = new CommonDragonVariantData(
                "jeb_",
                "variant.uselessreptile.wyvern.jeb_",
                Optional.empty(),
                UselessReptile.id("wyvern/" + "jeb_"),
                UselessReptile.id("wyvern"),
                Optional.empty(),
                Optional.empty(),
                -1,
                Optional.empty(),
                Optional.empty()
        );
        addSecretEntry(UselessReptile.id("jeb_"), new WyvernVariant(jeb_));

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
        Identifier id = URRegistries.VARIANT_TYPE.getKey(URDragonVariantTypes.WYVERN);
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.wyvern." + name,
                Optional.empty(),
                UselessReptile.id("wyvern/" + name),
                UselessReptile.id("wyvern"),
                Optional.of(UselessReptile.id("wyvern/default")),
                Optional.empty(),
                256,
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(2, 4),
                                        Optional.of(1)
                                ),
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(2, 4),
                                        Optional.of(1)
                                ),
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true),
                                        new com.mojang.datafixers.util.Pair<>(1, 1),
                                        Optional.empty()
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 2, Optional.empty())
                        )
                )
        );
        addEntry(getId(id, name), new WyvernVariant(variant));
    }

    protected void addMoleclaw(String name, boolean rare) {
        Identifier id = URRegistries.VARIANT_TYPE.getKey(URDragonVariantTypes.MOLECLAW);
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.moleclaw." + name,
                Optional.empty(),
                UselessReptile.id("moleclaw/" + name),
                UselessReptile.id("moleclaw"),
                Optional.of(UselessReptile.id("moleclaw/" + (rare ? "rare" : "default"))),
                Optional.empty(),
                64,
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BEETROOT.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2),
                                        Optional.empty()
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ConventionalItemTags.VEGETABLE_FOODS.location(), true), 2, Optional.empty())
                        )
                )
        );
        addEntry(getId(id, name), new MoleclawVariant(variant));
    }

    protected void addRiverPikehorn(String name) {
        Identifier id = URRegistries.VARIANT_TYPE.getKey(URDragonVariantTypes.RIVER_PIKEHORN);
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.river_pikehorn." + name,
                Optional.empty(),
                UselessReptile.id("river_pikehorn/" + name),
                UselessReptile.id("empty"),
                Optional.of(UselessReptile.id("river_pikehorn/default")),
                Optional.empty(),
                8,
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.TROPICAL_FISH_BUCKET.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(8, 8),
                                        Optional.empty()
                                ),
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.TROPICAL_FISH.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2),
                                        Optional.empty()
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.FISHES.location(), true), 3, Optional.empty())
                        )
                )
        );
        addEntry(getId(id, name), new RiverPikehornVariant(variant));
    }

    protected void addLightningChaser(String name) {
        Identifier id = URRegistries.VARIANT_TYPE.getKey(URDragonVariantTypes.LIGHTNING_CHASER);
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.lightning_chaser." + name,
                Optional.empty(),
                UselessReptile.id("lightning_chaser/" + name),
                UselessReptile.id("lightning_chaser"),
                Optional.of(UselessReptile.id("lightning_chaser/" + name)),
                Optional.empty(),
                3,
                Optional.empty(),
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 3, Optional.empty())
                        )
                )
        );
        addEntry(getId(id, name), new LightningChaserVariant(variant));
    }

    protected void addMagmamuncher(String name) {
        Identifier id = URRegistries.VARIANT_TYPE.getKey(URDragonVariantTypes.MAGMAMUNCHER);
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.magmamuncher." + name,
                Optional.empty(),
                UselessReptile.id("magmamuncher/" + name),
                UselessReptile.id("empty"),
                Optional.of(UselessReptile.id("magmamuncher/default")),
                Optional.empty(),
                12,
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BLAZE_ROD.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 3),
                                        Optional.empty()
                                )
                        )
                ),
                Optional.of(
                        List.of(
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_BLOCK.builtInRegistryHolder().key().identifier(), false), 4, Optional.empty()),
                                new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_CREAM.builtInRegistryHolder().key().identifier(), false), 2, Optional.empty())
                        )
                )
        );
        addEntry(getId(id, name), new MagmamuncherVariant(variant));
    }

    protected Identifier getId(Identifier dragonId, String name) {
        return Identifier.fromNamespaceAndPath(dragonId.getNamespace(), dragonId.getPath() + "/" + name);
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Variant";
    }
}
