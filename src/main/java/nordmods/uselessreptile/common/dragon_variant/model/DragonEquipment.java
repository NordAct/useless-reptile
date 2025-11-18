package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record DragonEquipment(Optional<ResourceLocation> parent, List<Equipment>equipment) {
    public static final Codec<DragonEquipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(DragonEquipment::parent),
                    Equipment.CODEC.listOf().fieldOf("equipment").forGetter(DragonEquipment::equipment))
            .apply(instance, DragonEquipment::new));

    //TODO
    // allow specifying slot and attribute modifiers
    // make dragons use equipment lists for defining what they can equip instead of tags
    // make tooltip that will display for which variant which attributes it'll increase
    //      tooltip should be scrollable (like pages) and group based on display name and similarity of attribute modifiers
    // add types for equipment (armor and saddle) to make for saddle possible to configure amount of passengers and their positions
    // rework passenger renderer to make multiple passengers possible
    public record Equipment(ResourceLocation item, ModelData modelData) {
        public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ResourceLocation.CODEC.fieldOf("item").forGetter(Equipment::item),
                        ModelData.CODEC.fieldOf("model_data").forGetter(Equipment::modelData))
                .apply(instance, Equipment::new));
    }
}
