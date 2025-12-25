package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record DragonModelData(ModelData modelData, Optional<List<Sound>> sounds) {
    public static final Codec<DragonModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ModelData.CODEC.fieldOf("model_data").forGetter(DragonModelData::modelData),
                    Sound.CODEC.listOf().optionalFieldOf("sounds").forGetter(DragonModelData::sounds))
            .apply(instance, DragonModelData::new));

    public record Sound(String name, Identifier id, Optional<Float> volume, Optional<Float> pitch, Optional<Float> pitchDeviation) {
        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Sound::name),
                    Identifier.CODEC.fieldOf("id").forGetter(Sound::id),
                    Codec.FLOAT.optionalFieldOf("volume").forGetter(Sound::volume),
                    Codec.FLOAT.optionalFieldOf("pitch").forGetter(Sound::pitch),
                    Codec.FLOAT.optionalFieldOf("pitch_deviation").forGetter(Sound::pitchDeviation)
                ).apply(instance, Sound::new)
        );
    }
}
