package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistryKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

//TODO also move custom name registry to clientside
public record DragonVariant(
        Identifier dragonId,
        String name,
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
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment),
                    Identifier.CODEC.optionalFieldOf("spawn_conditions").forGetter(DragonVariant::spawnConditions),
                    Identifier.CODEC.optionalFieldOf("attribute_modifiers").forGetter(DragonVariant::variantAttributeModifiers),
                    Codec.INT.fieldOf("base_taming_progress").forGetter(DragonVariant::baseTamingProgress),
                    TamingItem.CODEC.listOf().optionalFieldOf("taming_items").forGetter(DragonVariant::tamingItems),
                    FoodItem.CODEC.listOf().optionalFieldOf("food_items").forGetter(DragonVariant::foodItems))
            .apply(instance, DragonVariant::new));

    public static final Codec<DragonVariant> CODEC_NO_SERVER_INFO = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment),
                    TamingItem.CODEC.listOf().optionalFieldOf("taming_items").forGetter(DragonVariant::tamingItems),
                    FoodItem.CODEC.listOf().optionalFieldOf("food_items").forGetter(DragonVariant::foodItems))
            .apply(instance, (
                    id,
                    variant,
                    displayNameKey,
                    dragonModelData,
                    dragonEquipment,
                    tamingItemList,
                    foodItemList
                    ) -> new DragonVariant(
                            id,
                            variant,
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

    @NotNull
    public static DragonVariant getDefaultVariant(Identifier dragonId, World world) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", dragonId.toString());
        URDragonEntity dragon = (URDragonEntity) EntityType.getEntityFromData(NbtReadView.create(UselessReptile.ERROR_REPORTER, world.getRegistryManager(), nbtCompound), world, SpawnReason.TRIGGERED).get();
        dragon.discard();
        return dragon.getWorld().getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT)
                .stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragon.getDragonId()) && dragonVariant.name().equals(dragon.getDefaultVariant()))
                .findFirst()
                .orElseThrow();
    }

    @Nullable
    public static DragonVariant getByVariant(Identifier dragonId, String variant, World world) {
        Registry<DragonVariant> registry = world.getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragonId) && dragonVariant.name().equals(variant))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static DragonVariant getByCustomName(Identifier dragonId, String name, World world) {
        Registry<DragonVariant> registry = world.getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT_CUSTOM_NAME);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragonId) && dragonVariant.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static DragonVariant getDragonVariant(Identifier dragonId, String name, String variant, World world) {
        DragonVariant dragonVariant = null;

        if (name != null) dragonVariant = getByCustomName(dragonId, name, world);
        if (dragonVariant != null) return dragonVariant;

        dragonVariant = getByVariant(dragonId, variant, world);
        if (dragonVariant != null) return dragonVariant;

        return getDefaultVariant(dragonId, world);
    }

    public record TamingItem(Codecs.TagEntryId item, Pair<Integer, Integer> tamingProgressIncrease) {
        private static final Codec<Pair<Integer, Integer>> PAIR_CODEC = Codec.pair(
                Codecs.NON_NEGATIVE_INT.fieldOf("min").codec(),
                Codecs.POSITIVE_INT.fieldOf("max").codec()
        );
        private static final Codec<Pair<Integer, Integer>> WITH_ALTERNATIVE = Codec.withAlternative(
                Codec.INT_STREAM
                        .comapFlatMap(
                                stream -> Util.decodeFixedLengthArray(stream, 2).map(values -> new Pair<>(values[0], values[1])),
                                pair -> IntStream.of(pair.getFirst(), pair.getSecond())
                        )
                        .stable(),
                PAIR_CODEC
        );

        public static final Codec<TamingItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codecs.TAG_ENTRY_ID.fieldOf("item").forGetter(TamingItem::item),
                        WITH_ALTERNATIVE.fieldOf("taming_progress_increase").forGetter(TamingItem::tamingProgressIncrease)
                ).apply(instance, TamingItem::new)
        );
    }

    public record FoodItem(Codecs.TagEntryId item, int healingAmount) {
        public static final Codec<FoodItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codecs.TAG_ENTRY_ID.fieldOf("item").forGetter(FoodItem::item),
                        Codecs.NON_NEGATIVE_INT.fieldOf("healing_amount").forGetter(FoodItem::healingAmount)
                ).apply(instance, FoodItem::new)
        );
    }
}
