package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record DragonEquipment(Optional<Identifier> parent, List<Equipment>equipment) {
    public static final Codec<DragonEquipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(DragonEquipment::parent),
                    Equipment.CODEC.listOf().fieldOf("equipment").forGetter(DragonEquipment::equipment))
            .apply(instance, DragonEquipment::new));


    public record Equipment(Identifier item, ModelData modelData) {
        public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Identifier.CODEC.fieldOf("item").forGetter(Equipment::item),
                        ModelData.CODEC.fieldOf("model_data").forGetter(Equipment::modelData))
                .apply(instance, Equipment::new));
    }
}
