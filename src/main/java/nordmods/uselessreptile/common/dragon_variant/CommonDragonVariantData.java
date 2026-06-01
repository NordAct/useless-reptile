package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.IntStream;


//TODO:
//  hunt targets
//  untamed targets
//  tamed targets
//  effect immunities
//  damage immunities
//  potentially move all attributes to variant file

public record CommonDragonVariantData(
        String name,
        String variantNameKey,
        Optional<String> displayNameKey,
        Identifier dragonModelData,
        Identifier dragonEquipment,
        Optional<Identifier> spawnConditions,
        Optional<Identifier> variantAttributeModifiers,
        int baseTamingProgress,
        Optional<List<TamingItem>> tamingItems,
        Optional<List<FoodItem>> foodItems,
        Identifier abilities
) {
    public static final Codec<CommonDragonVariantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(CommonDragonVariantData::name),
                    Codec.STRING.fieldOf("variant_name_key").forGetter(CommonDragonVariantData::variantNameKey),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(CommonDragonVariantData::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(CommonDragonVariantData::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(CommonDragonVariantData::dragonEquipment),
                    Identifier.CODEC.optionalFieldOf("spawn_conditions").forGetter(CommonDragonVariantData::spawnConditions),
                    Identifier.CODEC.optionalFieldOf("attribute_modifiers").forGetter(CommonDragonVariantData::variantAttributeModifiers),
                    Codec.INT.fieldOf("base_taming_progress").forGetter(CommonDragonVariantData::baseTamingProgress),
                    TamingItem.LIST_CODEC.optionalFieldOf("taming_items").forGetter(CommonDragonVariantData::tamingItems),
                    FoodItem.LIST_CODEC.optionalFieldOf("food_items").forGetter(CommonDragonVariantData::foodItems),
                    Identifier.CODEC.fieldOf("abilities").forGetter(CommonDragonVariantData::abilities))
            .apply(instance, CommonDragonVariantData::new));

    public static final MapCodec<CommonDragonVariantData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(CommonDragonVariantData::name),
                    Codec.STRING.fieldOf("variant_name_key").forGetter(CommonDragonVariantData::variantNameKey),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(CommonDragonVariantData::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(CommonDragonVariantData::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(CommonDragonVariantData::dragonEquipment),
                    Identifier.CODEC.optionalFieldOf("spawn_conditions").forGetter(CommonDragonVariantData::spawnConditions),
                    Identifier.CODEC.optionalFieldOf("attribute_modifiers").forGetter(CommonDragonVariantData::variantAttributeModifiers),
                    Codec.INT.fieldOf("base_taming_progress").forGetter(CommonDragonVariantData::baseTamingProgress),
                    TamingItem.LIST_CODEC.optionalFieldOf("taming_items").forGetter(CommonDragonVariantData::tamingItems),
                    FoodItem.LIST_CODEC.optionalFieldOf("food_items").forGetter(CommonDragonVariantData::foodItems),
                    Identifier.CODEC.fieldOf("abilities").forGetter(CommonDragonVariantData::abilities))
            .apply(instance, CommonDragonVariantData::new));

    @ApiStatus.Internal //idk where else to put it
    public static final Map<Item, Set<Component>> EQUIPMENT_INFO_MAP = new HashMap<>();

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
