package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.TargetingSettings;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URDragonTargetingSettings extends FabricDynamicRegistryProvider {
    public static final ResourceKey<TargetingSettings> WYVERN = ResourceKey.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, UselessReptile.id("wyvern/default"));
    public static final ResourceKey<TargetingSettings> RIVER_PIKEHORN = ResourceKey.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, UselessReptile.id("river_pikehorn/default"));
    public static final ResourceKey<TargetingSettings> LIGHTNING_CHASER = ResourceKey.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, UselessReptile.id("lightning_chaser/default"));
    public static final ResourceKey<TargetingSettings> MOLECLAW = ResourceKey.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, UselessReptile.id("moleclaw/default"));
    public static final ResourceKey<TargetingSettings> MAGMAMUNCHER = ResourceKey.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, UselessReptile.id("magmamuncher/default"));

    public URDragonTargetingSettings(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_TARGETING_SETTINGS));
    }

    public static void register(BootstrapContext<TargetingSettings> bootstrapContext) {
        bootstrapContext.register(
                WYVERN,
                new TargetingSettings(
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.CHICKEN), false)
                        ),
                        List.of(),
                        true,
                        false
                )
        );
        bootstrapContext.register(
                RIVER_PIKEHORN,
                new TargetingSettings(
                        List.of(),
                        List.of(),
                        true,
                        true
                )
        );
        bootstrapContext.register(
                MAGMAMUNCHER,
                new TargetingSettings(
                        List.of(),
                        List.of(),
                        true,
                        true
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER,
                new TargetingSettings(
                        List.of(),
                        List.of(),
                        true,
                        false
                )
        );
        bootstrapContext.register(
                MOLECLAW,
                new TargetingSettings(
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(EntityType.getKey(EntityType.PLAYER), false)
                        ),
                        List.of(),
                        true,
                        true
                )
        );
    }

    @Override
    public String getName() {
        return "Targeting Settings";
    }
}
