package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record EquipmentModelData(Optional<Identifier> parent, Map<Identifier, Equipment> equipment) {
    public static final Codec<EquipmentModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(EquipmentModelData::parent),
                    Codec.unboundedMap(Identifier.CODEC, Equipment.CODEC).fieldOf("equipment").forGetter(EquipmentModelData::equipment))
            .apply(instance, EquipmentModelData::new));

    //TODO
    // allow attribute modifiers
    // make tooltip that will display for which variant which attributes it'll increase
    //      tooltip should be scrollable (like pages) and group based on display name and similarity of attribute modifiers
    public record Equipment(ModelData modelData, Optional<List<String>> hidBones, DragonInventory.Slot slot, Optional<Integer> maxPassengers) {
        public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        ModelData.CODEC.fieldOf("model_data").forGetter(Equipment::modelData),
                        ExtraCodecs.NON_EMPTY_STRING.listOf().optionalFieldOf("hid_bones").forGetter(Equipment::hidBones),
                        DragonInventory.Slot.CODEC.fieldOf("slot").forGetter(Equipment::slot),
                        Codec.INT.optionalFieldOf("max_passengers").forGetter(Equipment::maxPassengers)
                )
                .apply(instance, Equipment::new));
    }
}
