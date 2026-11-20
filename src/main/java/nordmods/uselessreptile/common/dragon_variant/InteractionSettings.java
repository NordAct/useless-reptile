package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

public record InteractionSettings(
        int baseTamingProgress,
        List<TamingItem> tamingItems,
        List<FoodItem> foodItems,
        List<CustomItemInteraction> customItemInteractions,
        List<ExtraCodecs.TagOrElementLocation> effectImmunities,
        List<ExtraCodecs.TagOrElementLocation> damageTypeImmunities
) {
    public static final Codec<InteractionSettings> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("base_taming_progress").forGetter(InteractionSettings::baseTamingProgress),
            TamingItem.LIST_CODEC.fieldOf("taming_items").forGetter(InteractionSettings::tamingItems),
            FoodItem.LIST_CODEC.fieldOf("food_items").forGetter(InteractionSettings::foodItems),
            CustomItemInteraction.LIST_CODEC.fieldOf("custom_item_interactions").forGetter(InteractionSettings::customItemInteractions),
            ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("effect_immunities").forGetter(InteractionSettings::effectImmunities),
            ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("damage_type_immunities").forGetter(InteractionSettings::damageTypeImmunities)

    ).apply(i, InteractionSettings::new));

    public static final Codec<Holder<InteractionSettings>> CODEC = RegistryFileCodec.create(URResourceKeys.DRAGON_INTERACTION_SETTINGS, DIRECT_CODEC);

    public @Nullable TamingItem getTamingItem(ItemStack itemStack) {
        return tamingItems().stream()
                .filter(item -> {
                    ExtraCodecs.TagOrElementLocation entryId = item.item();
                    if (entryId.tag()) return itemStack.is(TagKey.create(Registries.ITEM, entryId.id()));
                    return itemStack.is(ResourceKey.create(Registries.ITEM, entryId.id()));
                })
                .max(Comparator.comparingInt(item -> item.priority().orElse(Integer.MIN_VALUE)))
                .orElse(null);
    }

    public @Nullable FoodItem getFoodItem(ItemStack itemStack) {
        return foodItems().stream()
                .filter(item -> {
                    ExtraCodecs.TagOrElementLocation entryId = item.item();
                    if (entryId.tag()) return itemStack.is(TagKey.create(Registries.ITEM, entryId.id()));
                    return itemStack.is(ResourceKey.create(Registries.ITEM, entryId.id()));
                })
                .max(Comparator.comparingInt(item -> item.priority().orElse(Integer.MIN_VALUE)))
                .orElse(null);
    }

    public boolean isImmuneToEffect(Holder<MobEffect> effect) {
        for (ExtraCodecs.TagOrElementLocation entry : effectImmunities) {
            if (entry.tag()) {
                if (effect.is(TagKey.create(Registries.MOB_EFFECT, entry.id()))) return true;
            } else {
                if (effect.is(ResourceKey.create(Registries.MOB_EFFECT, entry.id()))) return true;
            }
        }
        return false;
    }

    public boolean isImmuneToDamage(Holder<DamageType> damageType) {
        for (ExtraCodecs.TagOrElementLocation entry : damageTypeImmunities) {
            if (entry.tag()) {
                if (damageType.is(TagKey.create(Registries.DAMAGE_TYPE, entry.id()))) return true;
            } else {
                if (damageType.is(ResourceKey.create(Registries.DAMAGE_TYPE, entry.id()))) return true;
            }
        }
        return false;
    }

    public record CustomItemInteraction(
            ExtraCodecs.TagOrElementLocation item,
            int count,
            DataComponentPatch components,
            List<CustomItemInteraction.Command> commands,
            boolean tame,
            boolean ownerOnly,
            boolean consume
    ) {
        public static final Codec<CustomItemInteraction> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(CustomItemInteraction::item),
                ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(CustomItemInteraction::count),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(CustomItemInteraction::components),
                CustomItemInteraction.Command.CODEC.listOf().fieldOf("commands").forGetter(CustomItemInteraction::commands),
                Codec.BOOL.fieldOf("tame").forGetter(CustomItemInteraction::tame),
                Codec.BOOL.fieldOf("owner_only").forGetter(CustomItemInteraction::ownerOnly),
                Codec.BOOL.fieldOf("consume").forGetter(CustomItemInteraction::consume)
        ).apply(i, CustomItemInteraction::new));

        public static final Codec<List<CustomItemInteraction>> LIST_CODEC = CODEC.listOf();

        public boolean tryExecute(URDragonEntity dragon, Player player, ItemStack stackInHand, @NonNull InteractionHand hand) {
            if (dragon.isTame() != tame || ownerOnly && dragon.isTame() && dragon.getOwner() != player) return false;
            if (stackInHand.count() < count || !Objects.equals(stackInHand.getComponentsPatch(), components)) return false;
            if (item.tag()) {
                if (!stackInHand.is(TagKey.create(Registries.ITEM, item.id()))) return false;
            } else {
                if (!stackInHand.is(ResourceKey.create(Registries.ITEM, item.id()))) return false;
            }
            if (!dragon.level().isClientSide()) {
                commands.forEach(command -> command.execute(dragon, player, dragon.level().getServer()));
            }
            if (consume) {
                dragon.consumeGivenItem(player, stackInHand, null, hand, count);
                player.awardStat(Stats.ITEM_USED.get(stackInHand.getItem()));
            }
            return true;
        }

        public record Command(
                String command,
                CustomItemInteraction.Executor executor
        ) {
            public static final Codec<CustomItemInteraction.Command> CODEC = RecordCodecBuilder.create(i -> i.group(
                    Codec.STRING.fieldOf("command").forGetter(CustomItemInteraction.Command::command),
                    StringRepresentable.fromEnum(CustomItemInteraction.Executor::values).fieldOf("executor").forGetter(CustomItemInteraction.Command::executor)
            ).apply(i, CustomItemInteraction.Command::new));

            public void execute(URDragonEntity dragon, Player player, MinecraftServer server) {
                switch (executor) {
                    case PLAYER -> {
                        if (player.level() instanceof ServerLevel serverLevel) {
                            server.getCommands().performPrefixedCommand(player.createCommandSourceStackForNameResolution(serverLevel).withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER), command);

                        }
                    }
                    case ENTITY -> {
                        if (dragon.level() instanceof ServerLevel serverLevel) {
                            server.getCommands().performPrefixedCommand(dragon.createCommandSourceStackForNameResolution(serverLevel).withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER), command);

                        }
                    }
                    case SERVER -> {
                        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withMaximumPermission(LevelBasedPermissionSet.GAMEMASTER), command);
                    }
                }
            }
        }

        public enum Executor implements StringRepresentable {
            PLAYER("player"),
            ENTITY("entity"),
            SERVER("server");

            private final String name;

            Executor(String name) {
                this.name = name;
            }

            @Override
            public String getSerializedName() {
                return name;
            }
        }
    }

    public record TamingItem(ExtraCodecs.TagOrElementLocation item, Pair<Integer, Integer> tamingProgressIncrease, Optional<Integer> priority) {
        private static final Codec<Pair<Integer, Integer>> PAIR_CODEC = Codec.pair(
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min").codec(),
                ExtraCodecs.POSITIVE_INT.fieldOf("max").codec()
        );
        private static final Codec<Pair<Integer, Integer>> PAIR_WITH_ALTERNATIVE = Codec.withAlternative(
                Codec.INT_STREAM
                        .comapFlatMap(
                                stream -> Util.fixedSize(stream, 2).map(values -> new Pair<>(values[0], values[1])),
                                pair -> IntStream.of(pair.getFirst(), pair.getSecond())
                        )
                        .stable(),
                PAIR_CODEC
        );

        public static final Codec<TamingItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(TamingItem::item),
                        PAIR_WITH_ALTERNATIVE.fieldOf("taming_progress_increase").forGetter(TamingItem::tamingProgressIncrease),
                        Codec.INT.optionalFieldOf("priority").forGetter(TamingItem::priority)
                ).apply(instance, TamingItem::new)
        );

        public static final Codec<List<TamingItem>> LIST_CODEC = Codec.withAlternative(
                CODEC.listOf(),
                Codec.unboundedMap(ExtraCodecs.TAG_OR_ELEMENT_ID, PAIR_WITH_ALTERNATIVE).xmap(tagEntryIdIntegerMap ->
                        tagEntryIdIntegerMap
                                .entrySet()
                                .stream()
                                .map(entry -> new TamingItem(entry.getKey(), entry.getValue(), Optional.empty()))
                                .toList(), list -> {
                    HashMap<ExtraCodecs.TagOrElementLocation, Pair<Integer, Integer>> map = new HashMap<>();
                    list.forEach(tamingItem -> map.put(tamingItem.item(), tamingItem.tamingProgressIncrease()));
                    return map;
                })
        );
    }

    public record FoodItem(ExtraCodecs.TagOrElementLocation item, Integer healingAmount, Optional<Integer> priority) {
        public static final Codec<FoodItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(FoodItem::item),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("healing_amount").forGetter(FoodItem::healingAmount),
                        Codec.INT.optionalFieldOf("priority").forGetter(FoodItem::priority)
                ).apply(instance, FoodItem::new)
        );

        public static final Codec<List<FoodItem>> LIST_CODEC = Codec.withAlternative(
                Codec.unboundedMap(ExtraCodecs.TAG_OR_ELEMENT_ID, ExtraCodecs.NON_NEGATIVE_INT).xmap(tagEntryIdIntegerMap ->
                        tagEntryIdIntegerMap
                                .entrySet()
                                .stream()
                                .map(entry -> new FoodItem(entry.getKey(), entry.getValue(), Optional.empty()))
                                .toList(), list -> {
                    HashMap<ExtraCodecs.TagOrElementLocation, Integer> map = new HashMap<>();
                    list.forEach(foodItem -> map.put(foodItem.item(), foodItem.healingAmount()));
                    return map;
                }),
                CODEC.listOf()
        );
    }
}
