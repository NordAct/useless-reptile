package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistryKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


//TODO:
//  hunt targets
//  untamed targets
//  tamed targets
//  taming items
//  per slot equipment items
//  healing/food items
//  effect immunities
//  damage immunities

//TODO also move custom name registry to clientside
public record DragonVariant(Identifier dragonId, String name, Optional<String> displayNameKey, Identifier dragonModelData, Identifier dragonEquipment, Optional<Identifier> spawnConditions, Optional<Identifier> variantAttributeModifiers) {
    public static final Codec<DragonVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment),
                    Identifier.CODEC.optionalFieldOf("spawn_conditions").forGetter(DragonVariant::spawnConditions),
                    Identifier.CODEC.optionalFieldOf("attribute_modifiers").forGetter(DragonVariant::variantAttributeModifiers))
            .apply(instance, DragonVariant::new));

    public static final Codec<DragonVariant> CODEC_NO_SERVER_INFO = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DragonVariant::dragonId),
                    Codec.STRING.fieldOf("name").forGetter(DragonVariant::name),
                    Codec.STRING.optionalFieldOf("display_name_key").forGetter(DragonVariant::displayNameKey),
                    Identifier.CODEC.fieldOf("dragon_model").forGetter(DragonVariant::dragonModelData),
                    Identifier.CODEC.fieldOf("equipment").forGetter(DragonVariant::dragonEquipment))
            .apply(instance, (id, variant, displayNameKey, dragonModelData, dragonEquipment) -> new DragonVariant(id, variant, displayNameKey, dragonModelData, dragonEquipment, Optional.empty(), Optional.empty())));

    @NotNull
    public static DragonVariant getDefaultVariant(URDragonEntity dragon) {
        return dragon.getWorld().getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT)
                .stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(dragon.getDragonId()) && dragonVariant.name().equals(dragon.getDefaultVariant()))
                .findFirst()
                .orElseThrow();
    }

    @Nullable
    public static DragonVariant getByVariant(URDragonEntity dragon) {
        Identifier id = dragon.getDragonId();
        String name = dragon.getVariant();
        Registry<DragonVariant> registry = dragon.getWorld().getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(id) && dragonVariant.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static DragonVariant getByCustomName(URDragonEntity dragon) {
        Identifier id = dragon.getDragonId();
        String name = dragon.getName().getString();
        Registry<DragonVariant> registry = dragon.getWorld().getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_VARIANT_CUSTOM_NAME);
        return registry.stream()
                .filter(dragonVariant -> dragonVariant.dragonId().equals(id) && dragonVariant.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static DragonVariant getDragonVariant(URDragonEntity dragon) {
        DragonVariant variant = null;

        if (dragon.hasCustomName()) variant = getByCustomName(dragon);
        if (variant != null) return variant;

        variant = getByVariant(dragon);
        if (variant != null) return variant;

        return getDefaultVariant(dragon);
    }
}
