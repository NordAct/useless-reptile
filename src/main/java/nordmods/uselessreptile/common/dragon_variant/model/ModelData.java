package nordmods.uselessreptile.common.dragon_variant.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record ModelData(ResourceLocation texture, ResourceLocation model, Optional<ResourceLocation> animation, boolean cull, boolean translucent) {
    public static final Codec<ModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ModelData::texture),
            ResourceLocation.CODEC.fieldOf("model").forGetter(ModelData::model),
            ResourceLocation.CODEC.optionalFieldOf("animation").forGetter(ModelData::animation),
            Codec.BOOL.optionalFieldOf("cull", true).forGetter(ModelData::cull),
            Codec.BOOL.optionalFieldOf("translucent", false).forGetter(ModelData::translucent))
            .apply(instance, ModelData::new));
}
