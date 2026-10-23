package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public record CommonDragonVariantData(
        String name,
        String variantNameKey,
        Optional<String> displayNameKey,
        Holder<DragonModelData> dragonModelData,
        Holder<EquipmentModelData> dragonEquipment,
        Holder<List<DragonSpawnConditions>> spawnConditions,
        Holder<Map<Holder<Attribute>, Float>> baseAttributeValues,
        Holder<List<DragonAbility>> abilities,
        Holder<InteractionSettings> interactionSettings,
        Holder<TargetingSettings> targetingSettings
) {
    public static final Codec<Map<Holder<Attribute>, Float>> BASE_ATTRIBUTE_VALUES_DIRECT_CODEC = Codec.unboundedMap(Attribute.CODEC, Codec.FLOAT);
    public static final Codec<Holder<Map<Holder<Attribute>, Float>>> BASE_ATTRIBUTE_VALUES_CODEC = RegistryFileCodec.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, BASE_ATTRIBUTE_VALUES_DIRECT_CODEC);

    public static final MapCodec<CommonDragonVariantData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(CommonDragonVariantData::name),
            Codec.STRING.fieldOf("variant_name_key").forGetter(CommonDragonVariantData::variantNameKey),
            Codec.STRING.optionalFieldOf("display_name_key").forGetter(CommonDragonVariantData::displayNameKey),
            DragonModelData.CODEC.fieldOf("dragon_model").forGetter(CommonDragonVariantData::dragonModelData),
            EquipmentModelData.CODEC.fieldOf("equipment").forGetter(CommonDragonVariantData::dragonEquipment),
            DragonSpawnConditions.CODEC.fieldOf("spawn_conditions").forGetter(CommonDragonVariantData::spawnConditions),
            BASE_ATTRIBUTE_VALUES_CODEC.fieldOf("base_attribute_values").forGetter(CommonDragonVariantData::baseAttributeValues),
            DragonAbility.CODEC.fieldOf("abilities").forGetter(CommonDragonVariantData::abilities),
            InteractionSettings.CODEC.fieldOf("interaction_settings").forGetter(CommonDragonVariantData::interactionSettings),
            TargetingSettings.CODEC.fieldOf("targeting_settings").forGetter(CommonDragonVariantData::targetingSettings)
    ).apply(instance, CommonDragonVariantData::new));

    public static final Codec<CommonDragonVariantData> CODEC = MAP_CODEC.codec();

    @ApiStatus.Internal //idk where else to put it
    public static final ConcurrentHashMap<Holder<Item>, Set<Component>> EQUIPMENT_INFO_MAP = new ConcurrentHashMap<>();
}
