package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record ModelData(Identifier texture, Identifier model, Optional<Identifier> animation, boolean cull, boolean translucent) {
    public static final Codec<ModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(ModelData::texture),
            Identifier.CODEC.fieldOf("model").forGetter(ModelData::model),
            Identifier.CODEC.optionalFieldOf("animation").forGetter(ModelData::animation),
            Codec.BOOL.optionalFieldOf("cull", true).forGetter(ModelData::cull),
            Codec.BOOL.optionalFieldOf("translucent", false).forGetter(ModelData::translucent))
            .apply(instance, ModelData::new));
}
