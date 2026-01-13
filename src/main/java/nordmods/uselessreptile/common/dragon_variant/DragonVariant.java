package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


//TODO:
//  hunt targets
//  untamed targets
//  tamed targets
//  per slot equipment items
//  effect immunities
//  damage immunities
//  potentially move all attributes to variant file

public record DragonVariant(
        Identifier dragonId,
        String name,
        String variantNameKey,
        Optional<String> displayNameKey,
        Identifier dragonModelData,
        Identifier dragonEquipment,
        Optional<Identifier> spawnConditions,
        Optional<Identifier> variantAttributeModifiers,
        int baseTamingProgress,
        Optional<List<TamingItem>> tamingItems,
        Optional<List<FoodItem>> foodItems
) {
    public static final Codec<DragonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Codec.STRING.fieldOf("variant_name_key").forGetter(DragonVariant::variantNameKey),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment),
                    Identifier.CODEC.optionalFieldOf("spawn_conditions").forGetter(DragonVariant::spawnConditions),
                    Identifier.CODEC.optionalFieldOf("attribute_modifiers").forGetter(DragonVariant::variantAttributeModifiers),
                    Codec.INT.fieldOf("base_taming_progress").forGetter(DragonVariant::baseTamingProgress),
                    TamingItem.LIST_CODEC.optionalFieldOf("taming_items").forGetter(DragonVariant::tamingItems),
                    FoodItem.LIST_CODEC.optionalFieldOf("food_items").forGetter(DragonVariant::foodItems))
            .apply(instance, DragonVariant::new));

    public static final Codec<DragonVariant> CODEC_NO_SERVER_INFO = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Codec.STRING.fieldOf("variant_name_key").forGetter(DragonVariant::variantNameKey),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment),
                    TamingItem.LIST_CODEC.optionalFieldOf("taming_items").forGetter(DragonVariant::tamingItems),
                    FoodItem.LIST_CODEC.optionalFieldOf("food_items").forGetter(DragonVariant::foodItems))
            .apply(instance, (
                    id,
                    variant,
                    variantNameKey,
                    displayNameKey,
                    dragonModelData,
                    dragonEquipment,
                    tamingItemList,
                    foodItemList
                    ) -> new DragonVariant(
                            id,
                            variant,
                            variantNameKey,
                            displayNameKey,
                            dragonModelData,
                            dragonEquipment,
                            Optional.empty(),
                            Optional.empty(),
                            0,
                            tamingItemList,
                            foodItemList
                    )
            ));

    public static final Codec<DragonVariant> CODEC_CUSTOM_NAME = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment))
            .apply(instance, (
                            id,
                            variant,
                            dragonModelData,
                            dragonEquipment
                    ) -> new DragonVariant(
                            id,
                            variant,
                    "",
                    Optional.empty(),
                            dragonModelData,
                            dragonEquipment,
                            Optional.empty(),
                            Optional.empty(),
                            0,
                    Optional.empty(),
                    Optional.empty()
                    )
            ));

    @NotNull
    public static DragonVariant getDefaultVariant(Identifier dragonId, Level world) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putString("id", dragonId.toString());
        URDragonEntity dragon = (URDragonEntity) EntityType.create(TagValueInput.create(UselessReptile.ERROR_REPORTER, world.registryAccess(), nbtCompound), world, EntitySpawnReason.TRIGGERED).get();
        dragon.discard();
        return dragon.level().registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT)
                .stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragon.getDragonId()) && dragonVariant.name().equals(dragon.getDefaultVariant()))
                .findFirst()
                .orElseThrow();
    }

    @Nullable
    public static DragonVariant getByVariant(Identifier dragonId, String variant, Level world) {
        Registry<DragonVariant> registry = world.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragonId) && dragonVariant.name().equals(variant))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static DragonVariant getByCustomName(Identifier dragonId, String name, Level world) {
        Registry<DragonVariant> registry = world.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT_CUSTOM_NAME);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragonId) && dragonVariant.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static DragonVariant getDragonVariant(Identifier dragonId, String name, String variant, Level world) {
        DragonVariant dragonVariant = null;

        if (name != null) dragonVariant = getByCustomName(dragonId, name, world);
        if (dragonVariant != null) return dragonVariant;

        dragonVariant = getByVariant(dragonId, variant, world);
        if (dragonVariant != null) return dragonVariant;

        return getDefaultVariant(dragonId, world);
    }

    public record TamingItem(ExtraCodecs.TagOrElementLocation item, Pair<Integer, Integer> tamingProgressIncrease) {
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
                        PAIR_WITH_ALTERNATIVE.fieldOf("taming_progress_increase").forGetter(TamingItem::tamingProgressIncrease)
                ).apply(instance, TamingItem::new)
        );

        public static final Codec<List<TamingItem>> LIST_CODEC = Codec.withAlternative(
                Codec.unboundedMap(ExtraCodecs.TAG_OR_ELEMENT_ID, PAIR_WITH_ALTERNATIVE).xmap(tagEntryIdIntegerMap ->
                        tagEntryIdIntegerMap
                                .entrySet()
                                .stream()
                                .map(entry -> new TamingItem(entry.getKey(), entry.getValue()))
                                .toList(), list -> {
                    HashMap<ExtraCodecs.TagOrElementLocation, Pair<Integer, Integer>> map = new HashMap<>();
                    list.forEach(tamingItem -> map.put(tamingItem.item(), tamingItem.tamingProgressIncrease()));
                    return map;
                }),
                CODEC.listOf()
        );
    }

    public record FoodItem(ExtraCodecs.TagOrElementLocation item, Integer healingAmount) {
        public static final Codec<FoodItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(FoodItem::item),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("healing_amount").forGetter(FoodItem::healingAmount)
                ).apply(instance, FoodItem::new)
        );

        public static final Codec<List<FoodItem>> LIST_CODEC = Codec.withAlternative(
                Codec.unboundedMap(ExtraCodecs.TAG_OR_ELEMENT_ID, ExtraCodecs.NON_NEGATIVE_INT).xmap(tagEntryIdIntegerMap ->
                        tagEntryIdIntegerMap
                                .entrySet()
                                .stream()
                                .map(entry -> new FoodItem(entry.getKey(), entry.getValue()))
                                .toList(), list -> {
                    HashMap<ExtraCodecs.TagOrElementLocation, Integer> map = new HashMap<>();
                    list.forEach(foodItem -> map.put(foodItem.item(), foodItem.healingAmount()));
                    return map;
                }),
                CODEC.listOf()
        );
    }
}
