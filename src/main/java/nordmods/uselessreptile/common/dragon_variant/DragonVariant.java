package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;
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
}
