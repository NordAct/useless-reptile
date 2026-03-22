package nordmods.uselessreptile.common.network.c2s;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import org.jspecify.annotations.NonNull;

public record SetVariantChangingOrbVariantPayload(DragonVariantType<?> variantType, String variant) implements CustomPacketPayload{
    public static final Identifier ID = UselessReptile.id("set_variant_changing_orb_variant");
    public static final Type<SetVariantChangingOrbVariantPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SetVariantChangingOrbVariantPayload> PACKET_CODEC = StreamCodec.of(
            (byteBuf, payload) -> {
                Identifier.STREAM_CODEC.encode(byteBuf, payload.variantType.getId());
                ByteBufCodecs.STRING_UTF8.encode(byteBuf, payload.variant);
            },
            (byteBuf) -> {
                Identifier id = Identifier.STREAM_CODEC.decode(byteBuf);
                String variant = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
                return new SetVariantChangingOrbVariantPayload(id, variant);
            });

    public static void send(DragonVariantType<?> type, String variant) {
        ClientPlayNetworking.send(new SetVariantChangingOrbVariantPayload(type, variant));
    }

    public SetVariantChangingOrbVariantPayload(Identifier typeId, String variant) {
        this(DragonVariantType.fromId(typeId), variant);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
