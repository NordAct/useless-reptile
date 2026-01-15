package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record EquipmentModelData(Optional<Identifier> parent, List<Equipment>equipment) {
    public static final Codec<EquipmentModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(EquipmentModelData::parent),
                    Equipment.CODEC.listOf().fieldOf("equipment").forGetter(EquipmentModelData::equipment))
            .apply(instance, EquipmentModelData::new));

    //TODO
    // allow specifying slot and attribute modifiers
    // make tooltip that will display for which variant which attributes it'll increase
    //      tooltip should be scrollable (like pages) and group based on display name and similarity of attribute modifiers
    public record Equipment(Identifier item, ModelData modelData, Optional<Integer> maxPassengers, Optional<List<String>> hidBones) {
        public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Identifier.CODEC.fieldOf("item").forGetter(Equipment::item),
                        ModelData.CODEC.fieldOf("model_data").forGetter(Equipment::modelData),
                        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_passengers").forGetter(Equipment::maxPassengers),
                        ExtraCodecs.NON_EMPTY_STRING.listOf().optionalFieldOf("hid_bones").forGetter(Equipment::hidBones)
                )
                .apply(instance, Equipment::new));
    }
}
