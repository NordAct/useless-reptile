package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import org.jspecify.annotations.NonNull;

public record OpenVariantChangingOrbScreenPayload(DragonVariantType<?> variantType, String variant) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("open_variant_changing_orb_screen");
    public static final Type<OpenVariantChangingOrbScreenPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenVariantChangingOrbScreenPayload> PACKET_CODEC = StreamCodec.of(
            (byteBuf, payload) -> {
                Identifier.STREAM_CODEC.encode(byteBuf, payload.variantType.getId());
                ByteBufCodecs.STRING_UTF8.encode(byteBuf, payload.variant);
            },
            (byteBuf) -> {
                Identifier id = Identifier.STREAM_CODEC.decode(byteBuf);
                String variant = ByteBufCodecs.STRING_UTF8.decode(byteBuf);
                return new OpenVariantChangingOrbScreenPayload(id, variant);
            });

    public static void send(ServerPlayer player, DragonVariantType<?> type, String variant) {
        ServerPlayNetworking.send(player, new OpenVariantChangingOrbScreenPayload(type, variant));
    }

    public OpenVariantChangingOrbScreenPayload(Identifier typeId, String variant) {
        this(DragonVariantType.fromId(typeId), variant);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
