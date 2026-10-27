package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.InteractionSettings;
import nordmods.uselessreptile.common.init.URMobEffect;
import nordmods.uselessreptile.common.init.URPotions;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonInteractionSettings extends FabricDynamicRegistryProvider {
    public static final ResourceKey<InteractionSettings> WYVERN = ResourceKey.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, UselessReptile.id("wyvern/default"));
    public static final ResourceKey<InteractionSettings> RIVER_PIKEHORN = ResourceKey.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, UselessReptile.id("river_pikehorn/default"));
    public static final ResourceKey<InteractionSettings> LIGHTNING_CHASER = ResourceKey.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, UselessReptile.id("lightning_chaser/default"));
    public static final ResourceKey<InteractionSettings> MOLECLAW = ResourceKey.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, UselessReptile.id("moleclaw/default"));
    public static final ResourceKey<InteractionSettings> MAGMAMUNCHER = ResourceKey.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, UselessReptile.id("magmamuncher/default"));

    public URDragonInteractionSettings(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_INTERACTION_SETTINGS));
    }

    public static void register(BootstrapContext<InteractionSettings> bootstrapContext) {
        ItemStackTemplate potion = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder().set(TypedDataComponent.createUnchecked(DataComponents.POTION_CONTENTS, new PotionContents(URPotions.ACID))).build());
        bootstrapContext.register(
                WYVERN,
                new InteractionSettings(
                        256,
                        List.of(
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(2, 4),
                                        Optional.of(1)
                                ),
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(2, 4),
                                        Optional.of(1)
                                ),
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true),
                                        new com.mojang.datafixers.util.Pair<>(1, 1),
                                        Optional.empty()
                                )
                        ),
                        List.of(
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.COOKED_CHICKEN.builtInRegistryHolder().key().identifier(), false), 4, Optional.of(1)),
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 2, Optional.empty())
                        ),
                        List.of(
                                new InteractionSettings.CustomItemInteraction(
                                        new ItemStackTemplate(Items.GLASS_BOTTLE),
                                        List.of(
                                                new InteractionSettings.CustomItemInteraction.Command(
                                                        "give @s " + potion.typeHolder().getRegisteredName() + "[minecraft:potion_contents={potion:\"uselessreptile:acid\"}]",
                                                        InteractionSettings.CustomItemInteraction.Executor.PLAYER
                                                ),
                                                new InteractionSettings.CustomItemInteraction.Command(
                                                        "playsound " + SoundEvents.BOTTLE_FILL.location() + " " + SoundSource.NEUTRAL.getName() + " @p ~ ~ ~",
                                                        InteractionSettings.CustomItemInteraction.Executor.ENTITY
                                                )
                                        ),
                                        true,
                                        true,
                                        true
                                )
                        ),
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(MobEffects.POISON.unwrapKey().orElseThrow().identifier(), false),
                                new ExtraCodecs.TagOrElementLocation(MobEffects.HUNGER.unwrapKey().orElseThrow().identifier(), false),
                                new ExtraCodecs.TagOrElementLocation(URMobEffect.ACID.unwrapKey().orElseThrow().identifier(), false)
                        ),
                        List.of()
                )
        );
        bootstrapContext.register(
                MOLECLAW,
                new InteractionSettings(
                        64,
                        List.of(
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BEETROOT.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2),
                                        Optional.empty()
                                )
                        ),
                        List.of(
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(ConventionalItemTags.VEGETABLE_FOODS.location(), true), 2, Optional.empty())
                        ),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );
        bootstrapContext.register(
                RIVER_PIKEHORN,
                new InteractionSettings(
                        8,
                        List.of(
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.TROPICAL_FISH_BUCKET.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(8, 8),
                                        Optional.empty()
                                ),
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.TROPICAL_FISH.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2),
                                        Optional.empty()
                                )
                        ),
                        List.of(
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.FISHES.location(), true), 3, Optional.empty())
                        ),
                        List.of(),
                        List.of(),
                        List.of()
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER,
                new InteractionSettings(
                        3,
                        List.of(),
                        List.of(
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(ItemTags.MEAT.location(), true), 3, Optional.empty())
                        ),
                        List.of(),
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(URMobEffect.SHOCK.unwrapKey().orElseThrow().identifier(), false)
                        ),
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(DamageTypeTags.IS_LIGHTNING.location(), true)
                        )
                )
        );
        bootstrapContext.register(
                MAGMAMUNCHER,
                new InteractionSettings(
                        12,
                        List.of(
                                new InteractionSettings.TamingItem(
                                        new ExtraCodecs.TagOrElementLocation(Items.BLAZE_ROD.builtInRegistryHolder().key().identifier(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 3),
                                        Optional.empty()
                                )
                        ),
                        List.of(
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_BLOCK.builtInRegistryHolder().key().identifier(), false), 4, Optional.empty()),
                                new InteractionSettings.FoodItem(new ExtraCodecs.TagOrElementLocation(Items.MAGMA_CREAM.builtInRegistryHolder().key().identifier(), false), 2, Optional.empty())
                        ),
                        List.of(),
                        List.of(),
                        List.of(
                                new ExtraCodecs.TagOrElementLocation(DamageTypeTags.IS_FIRE.location(), true)
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "Interaction Settings";
    }
}
