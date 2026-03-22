package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;

public record DragonVariantComponent(DragonVariantType<?> type, String variant) {
    public static final DragonVariantComponent DEFAULT = new DragonVariantComponent(URDragonVariantTypes.WYVERN, "green");
    public static final Codec<DragonVariantComponent> CODEC =  RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(c -> c.type().getId()),
            Codec.STRING.fieldOf("variant").forGetter(DragonVariantComponent::variant)
    ).apply(instance, DragonVariantComponent::new));
    public static final StreamCodec<ByteBuf, DragonVariantComponent> PACKET_CODEC = StreamCodec.of(
            (byteBuf, component) -> {
                Identifier.STREAM_CODEC.encode(byteBuf, component.type.getId());
                ByteBufCodecs.STRING_UTF8.encode(byteBuf, component.variant);
            },
            (byteBuf) -> {
                Identifier id = Identifier.STREAM_CODEC.decode(byteBuf);
                String variant = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
                return new DragonVariantComponent(id, variant);
            });

    public DragonVariantComponent(Identifier typeId, String variant) {
        this(DragonVariantType.fromId(typeId), variant);
    }
}
