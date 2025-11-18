package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record DragonModel(ModelData modelData, Optional<List<Sound>> sounds) {
    public static final Codec<DragonModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ModelData.CODEC.fieldOf("model_data").forGetter(DragonModel::modelData),
                    Sound.CODEC.listOf().optionalFieldOf("sounds").forGetter(DragonModel::sounds))
            .apply(instance, DragonModel::new));

    public record Sound(String name, ResourceLocation id, Optional<Float> volume, Optional<Float> pitch, Optional<Float> pitchDeviation) {
        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Sound::name),
                    ResourceLocation.CODEC.fieldOf("id").forGetter(Sound::id),
                    Codec.FLOAT.optionalFieldOf("volume").forGetter(Sound::volume),
                    Codec.FLOAT.optionalFieldOf("pitch").forGetter(Sound::pitch),
                    Codec.FLOAT.optionalFieldOf("pitch_deviation").forGetter(Sound::pitchDeviation)
                ).apply(instance, Sound::new)
        );
    }
}
