package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
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
        bootstrapContext.register(WYVERN_GREEN, makeWyvern(bootstrapContext, "green", URSpawnConditionsProvider.WYVERN));
        bootstrapContext.register(WYVERN_BROWN, makeWyvern(bootstrapContext, "brown", URSpawnConditionsProvider.WYVERN));
        bootstrapContext.register(WYVERN_JEB, makeWyvern(bootstrapContext, "jeb_", URSpawnConditionsProvider.CANNOT_SPAWN));

        bootstrapContext.register(MOLECLAW_BLACK, makeMoleclaw(bootstrapContext , "black", URSpawnConditionsProvider.MOLECLAW));
        bootstrapContext.register(MOLECLAW_BROWN, makeMoleclaw(bootstrapContext , "brown", URSpawnConditionsProvider.MOLECLAW));
        bootstrapContext.register(MOLECLAW_GREY, makeMoleclaw(bootstrapContext , "grey", URSpawnConditionsProvider.MOLECLAW));
        bootstrapContext.register(MOLECLAW_ALBINO, makeMoleclaw(bootstrapContext , "albino", URSpawnConditionsProvider.MOLECLAW_RARE));

        bootstrapContext.register(LIGHTNING_CHASER_BLUE, makeLightningChaser(bootstrapContext, "blue", URSpawnConditionsProvider.LIGHTNING_CHASER_BLUE));
        bootstrapContext.register(LIGHTNING_CHASER_GREY, makeLightningChaser(bootstrapContext, "grey", URSpawnConditionsProvider.LIGHTNING_CHASER_GREY));
        bootstrapContext.register(LIGHTNING_CHASER_BROWN, makeLightningChaser(bootstrapContext, "brown", URSpawnConditionsProvider.LIGHTNING_CHASER_BROWN));
        bootstrapContext.register(LIGHTNING_CHASER_PURPLE, makeLightningChaser(bootstrapContext, "purple", URSpawnConditionsProvider.LIGHTNING_CHASER_PURPLE));

        bootstrapContext.register(RIVER_PIKEHORN_GREEN, makeRiverPikehorn(bootstrapContext, "green", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_GREEN, makeRiverPikehorn(bootstrapContext, "dark_green", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_BLUE, makeRiverPikehorn(bootstrapContext, "blue", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_BLUE, makeRiverPikehorn(bootstrapContext, "dark_blue", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_PURPLE, makeRiverPikehorn(bootstrapContext, "purple", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_PURPLE, makeRiverPikehorn(bootstrapContext, "dark_purple", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_TEAL, makeRiverPikehorn(bootstrapContext, "teal", URSpawnConditionsProvider.RIVER_PIKEHORN));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_TEAL, makeRiverPikehorn(bootstrapContext, "dark_teal", URSpawnConditionsProvider.RIVER_PIKEHORN));

        bootstrapContext.register(MAGMAMUNCHER_NETHERRACK, addMagmamuncher(bootstrapContext, "netherrack", URSpawnConditionsProvider.MAGMAMUNCHER));
        bootstrapContext.register(MAGMAMUNCHER_MAGMA, addMagmamuncher(bootstrapContext, "magma", URSpawnConditionsProvider.MAGMAMUNCHER));
    }

    protected static DragonVariant makeWyvern(BootstrapContext<DragonVariant> bootstrapContext, String name, ResourceKey<List<DragonSpawnConditions>> spawnConditions) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.wyvern." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("wyvern/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(spawnConditions),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_INTERACTION_SETTINGS).getOrThrow(URDragonInteractionSettings.WYVERN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_TARGETING_SETTINGS).getOrThrow(URDragonTargetingSettings.WYVERN)
        );
        return new WyvernVariant(variant);
    }

    protected static DragonVariant makeMoleclaw(BootstrapContext<DragonVariant> bootstrapContext, String name, ResourceKey<List<DragonSpawnConditions>> spawnConditions) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.moleclaw." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.MOLECLAW),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(spawnConditions),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.MOLECLAW),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.MOLECLAW),
                bootstrapContext.lookup(URResourceKeys.DRAGON_INTERACTION_SETTINGS).getOrThrow(URDragonInteractionSettings.MOLECLAW),
                bootstrapContext.lookup(URResourceKeys.DRAGON_TARGETING_SETTINGS).getOrThrow(URDragonTargetingSettings.MOLECLAW)
        );
        return new MoleclawVariant(variant);
    }

    protected static DragonVariant makeRiverPikehorn(BootstrapContext<DragonVariant> bootstrapContext, String name, ResourceKey<List<DragonSpawnConditions>> spawnConditions) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.river_pikehorn." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.RIVER_PIKEHORN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(spawnConditions),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.RIVER_PIKEHORN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.RIVER_PIKEHORN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_INTERACTION_SETTINGS).getOrThrow(URDragonInteractionSettings.RIVER_PIKEHORN),
                bootstrapContext.lookup(URResourceKeys.DRAGON_TARGETING_SETTINGS).getOrThrow(URDragonTargetingSettings.RIVER_PIKEHORN)
        );
        return new RiverPikehornVariant(variant, List.of(new ExtraCodecs.TagOrElementLocation(URTags.RIVER_PIKEHORN_HUNT_TARGET.location(), true)));
    }

    protected static DragonVariant makeLightningChaser(BootstrapContext<DragonVariant> bootstrapContext, String name, ResourceKey<List<DragonSpawnConditions>> spawnConditions) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.lightning_chaser." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.LIGHTNING_CHASER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(spawnConditions),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.LIGHTNING_CHASER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.LIGHTNING_CHASER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_INTERACTION_SETTINGS).getOrThrow(URDragonInteractionSettings.LIGHTNING_CHASER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_TARGETING_SETTINGS).getOrThrow(URDragonTargetingSettings.LIGHTNING_CHASER)
        );
        return new LightningChaserVariant(variant);
    }

    protected static DragonVariant addMagmamuncher(BootstrapContext<DragonVariant> bootstrapContext,String name, ResourceKey<List<DragonSpawnConditions>> spawnConditions) {
        CommonDragonVariantData variant = new CommonDragonVariantData(
                name,
                "variant.uselessreptile.magmamuncher." + name,
                Optional.empty(),
                bootstrapContext.lookup(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("magmamuncher/" + name))),
                bootstrapContext.lookup(URResourceKeys.DRAGON_EQUIPMENT).getOrThrow(UREquipmentProvider.MAGMAMUNCHER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_SPAWN_CONDITIONS).getOrThrow(spawnConditions),
                bootstrapContext.lookup(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES).getOrThrow(URDragonBaseAttributeValues.MAGMAMUNCHER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_ABILITIES).getOrThrow(URDragonAbilitiesProvider.MAGMAMUNCHER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_INTERACTION_SETTINGS).getOrThrow(URDragonInteractionSettings.MAGMAMUNCHER),
                bootstrapContext.lookup(URResourceKeys.DRAGON_TARGETING_SETTINGS).getOrThrow(URDragonTargetingSettings.MAGMAMUNCHER)
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
