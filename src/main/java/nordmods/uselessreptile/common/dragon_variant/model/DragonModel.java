package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record DragonModel(ModelData modelData, Optional<List<Sound>> sounds) {
    public static final Codec<DragonModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ModelData.CODEC.fieldOf("model_data").forGetter(DragonModel::modelData),
                    Sound.CODEC.listOf().optionalFieldOf("sounds").forGetter(DragonModel::sounds))
            .apply(instance, DragonModel::new));

    public record Sound(String name, Identifier id, Optional<Float> volume, Optional<Float> pitch) {
        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Sound::name),
                    Identifier.CODEC.fieldOf("id").forGetter(Sound::id),
                    Codec.FLOAT.optionalFieldOf("volume").forGetter(Sound::volume),
                    Codec.FLOAT.optionalFieldOf("pitch").forGetter(Sound::pitch))
                .apply(instance, Sound::new)
        );
    }
}
