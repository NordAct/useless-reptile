package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.*;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
public class URDragonVariantProvider extends FabricDynamicRegistryProvider {
    public static final ResourceKey<DragonVariant> WYVERN_JEB = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("wyvern/jeb_"));
    public static final ResourceKey<DragonVariant> WYVERN_GREEN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("wyvern/green"));
    public static final ResourceKey<DragonVariant> WYVERN_BROWN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("wyvern/brown"));

    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_GREEN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/green"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_BLUE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/blue"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/purple"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_TEAL = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/teal"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_DARK_GREEN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/dark_green"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_DARK_BLUE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/dark_blue"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_DARK_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/dark_purple"));
    public static final ResourceKey<DragonVariant> RIVER_PIKEHORN_DARK_TEAL = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("river_pikehorn/dark_teal"));

    public static final ResourceKey<DragonVariant> LIGHTNING_CHASER_GREY = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("lightning_chaser/grey"));
    public static final ResourceKey<DragonVariant> LIGHTNING_CHASER_BLUE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("lightning_chaser/blue"));
    public static final ResourceKey<DragonVariant> LIGHTNING_CHASER_BROWN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("lightning_chaser/brown"));
    public static final ResourceKey<DragonVariant> LIGHTNING_CHASER_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("lightning_chaser/purple"));

    public static final ResourceKey<DragonVariant> MOLECLAW_BLACK = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("moleclaw/black"));
    public static final ResourceKey<DragonVariant> MOLECLAW_GREY = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("moleclaw/grey"));
    public static final ResourceKey<DragonVariant> MOLECLAW_BROWN = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("moleclaw/brown"));
    public static final ResourceKey<DragonVariant> MOLECLAW_ALBINO = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("moleclaw/albino"));

    public static final ResourceKey<DragonVariant> MAGMAMUNCHER_NETHERRACK = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("magmamuncher/netherrack"));
    public static final ResourceKey<DragonVariant> MAGMAMUNCHER_MAGMA = ResourceKey.create(URResourceKeys.DRAGON_VARIANT, UselessReptile.id("magmamuncher/magma"));

    public URDragonVariantProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }
    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_VARIANT));
    }

    public static void register(BootstrapContext<DragonVariant> bootstrapContext) {
        bootstrapContext.register(WYVERN_GREEN, makeWyvern(bootstrapContext, "green"));
        bootstrapContext.register(WYVERN_BROWN, makeWyvern(bootstrapContext, "brown"));
        CommonDragonVariantData jeb_ = new CommonDragonVariantData(
                "jeb_",
                "variant.uselessreptile.wyvern.jeb_",
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(URDragonModelProvider.WYVERN_JEB),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.CANNOT_SPAWN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.WYVERN),
                -1,
                List.of(),
                List.of(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.WYVERN),
                List.of(),
                List.of(),
                true,
                false
        );
        bootstrapContext.register(WYVERN_JEB, new WyvernVariant(jeb_));

        bootstrapContext.register(MOLECLAW_BLACK, makeMoleclaw(bootstrapContext , "black", false));
        bootstrapContext.register(MOLECLAW_BROWN, makeMoleclaw(bootstrapContext , "brown", false));
        bootstrapContext.register(MOLECLAW_GREY, makeMoleclaw(bootstrapContext , "grey", false));
        bootstrapContext.register(MOLECLAW_ALBINO, makeMoleclaw(bootstrapContext , "albino", true));

        bootstrapContext.register(LIGHTNING_CHASER_BLUE, makeLightningChaser(bootstrapContext, "blue"));
        bootstrapContext.register(LIGHTNING_CHASER_GREY, makeLightningChaser(bootstrapContext, "grey"));
        bootstrapContext.register(LIGHTNING_CHASER_BROWN, makeLightningChaser(bootstrapContext, "brown"));
        bootstrapContext.register(LIGHTNING_CHASER_PURPLE, makeLightningChaser(bootstrapContext, "purple"));

        bootstrapContext.register(RIVER_PIKEHORN_GREEN, makeRiverPikehorn(bootstrapContext, "green"));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_GREEN, makeRiverPikehorn(bootstrapContext, "dark_green"));
        bootstrapContext.register(RIVER_PIKEHORN_BLUE, makeRiverPikehorn(bootstrapContext, "blue"));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_BLUE, makeRiverPikehorn(bootstrapContext, "dark_blue"));
        bootstrapContext.register(RIVER_PIKEHORN_PURPLE, makeRiverPikehorn(bootstrapContext, "purple"));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_PURPLE, makeRiverPikehorn(bootstrapContext, "dark_purple"));
        bootstrapContext.register(RIVER_PIKEHORN_TEAL, makeRiverPikehorn(bootstrapContext, "teal"));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_TEAL, makeRiverPikehorn(bootstrapContext, "dark_teal"));

        bootstrapContext.register(MAGMAMUNCHER_NETHERRACK, addMagmamuncher(bootstrapContext, "netherrack"));
        bootstrapContext.register(MAGMAMUNCHER_MAGMA, addMagmamuncher(bootstrapContext, "magma"));
    }

    protected static DragonVariant makeWyvern(BootstrapContext<DragonVariant> bootstrapContext, String name) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.wyvern." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("wyvern/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.WYVERN),
                256,
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
                ),
                List.of(
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 2, Optional.empty())
                ),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.WYVERN),
                List.of(
                        new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.CHICKEN), false)
                ),
                List.of(),
                true,
                false
        );
        return new WyvernVariant(variant);
    }

    protected static DragonVariant makeMoleclaw(BootstrapContext<DragonVariant> bootstrapContext, String name, boolean rare) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.moleclaw." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.MOLECLAW),
                rare ? bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.MOLECLAW_RARE) : bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.MOLECLAW),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.MOLECLAW),
                64,
                List.of(
                        new CommonDragonVariantData.TamingItem(
                                new ExtraCodecs.TagOrElementLocation(Items.BEETROOT.builtInRegistryHolder().key().identifier(), false),
                                new com.mojang.datafixers.util.Pair<>(1, 2),
                                Optional.empty()
                        )
                ),
                List.of(
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ConventionalItemTags.VEGETABLE_FOODS.location(), true), 2, Optional.empty())
                ),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.MOLECLAW),
                List.of(
                        new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.PLAYER), false)
                ),
                List.of(),
                true,
                true
        );
        return new MoleclawVariant(variant);
    }

    protected static DragonVariant makeRiverPikehorn(BootstrapContext<DragonVariant> bootstrapContext, String name) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.river_pikehorn." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.EMPTY),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.RIVER_PIKEHORN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.RIVER_PIKEHORN),
                8,
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
                ),
                List.of(
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.FISHES.location(), true), 3, Optional.empty())
                ),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.RIVER_PIKEHORN),
                List.of(),
                List.of(),
                true,
                true
        );
        return new RiverPikehornVariant(variant, List.of(new ExtraCodecs.TagOrElementLocation(URTags.RIVER_PIKEHORN_HUNT_TARGET.location(), true)));
    }

    protected static DragonVariant makeLightningChaser(BootstrapContext<DragonVariant> bootstrapContext, String name) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.lightning_chaser." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.LIGHTNING_CHASER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("lightning_chaser/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.LIGHTNING_CHASER),
                3,
                List.of(),
                List.of(
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 3, Optional.empty())
                ),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.LIGHTNING_CHASER),
                List.of(),
                List.of(),
                true,
                false
        );
        return new LightningChaserVariant(variant);
    }

    protected static DragonVariant addMagmamuncher(BootstrapContext<DragonVariant> bootstrapContext,String name) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.magmamuncher." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("magmamuncher/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.EMPTY),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(URSpawnConditionsProvider.MAGMAMUNCHER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.MAGMAMUNCHER),
                12,
                List.of(
                        new CommonDragonVariantData.TamingItem(
                                new ExtraCodecs.TagOrElementLocation(Items.BLAZE_ROD.builtInRegistryHolder().key().identifier(), false),
                                new com.mojang.datafixers.util.Pair<>(1, 3),
                                Optional.empty()
                        )
                ),
                List.of(
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_BLOCK.builtInRegistryHolder().key().identifier(), false), 4, Optional.empty()),
                        new CommonDragonVariantData.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_CREAM.builtInRegistryHolder().key().identifier(), false), 2, Optional.empty())
                ),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.MAGMAMUNCHER),
                List.of(
                        new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.MAGMA_CUBE), false)
                ),
                List.of(
                        new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.MAGMA_CUBE), false)
                ),
                true,
                true
        );
        return new MagmamuncherVariant(variant);
    }

    protected Identifier getId(Identifier dragonId, String name) {
        return Identifier.fromNamespaceAndPath(dragonId.getNamespace(), dragonId.getPath() + "/" + name);
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Variant";
    }
}
