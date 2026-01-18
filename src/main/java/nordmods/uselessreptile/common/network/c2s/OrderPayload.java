package nordmods.uselessreptile.common.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public record OrderPayload(URDragonEntity.Order order, int id) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("wanderRadius");
    public static final Type<OrderPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OrderPayload> PACKET_CODEC =
            StreamCodec.of(OrderPayload::write, OrderPayload::read);

    private static OrderPayload read(RegistryFriendlyByteBuf buffer) {
        URDragonEntity.Order order = buffer.readEnum(URDragonEntity.Order.class);
        int id = buffer.readInt();
        return new OrderPayload(order, id);
    }

    private static void write(RegistryFriendlyByteBuf buf, OrderPayload packet) {
        buf.writeEnum(packet.order);
        buf.writeInt(packet.id);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}

