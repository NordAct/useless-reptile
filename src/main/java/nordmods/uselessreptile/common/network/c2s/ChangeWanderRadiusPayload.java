package nordmods.uselessreptile.common.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public record ChangeWanderRadiusPayload(URDragonEntity.WanderRadius wanderRadius, int id) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("change_wander_radius");
    public static final Type<ChangeWanderRadiusPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeWanderRadiusPayload> PACKET_CODEC =
            StreamCodec.of(ChangeWanderRadiusPayload::write, ChangeWanderRadiusPayload::read);

    private static ChangeWanderRadiusPayload read(RegistryFriendlyByteBuf buffer) {
        URDragonEntity.WanderRadius wanderRadius = buffer.readEnum(URDragonEntity.WanderRadius.class);
        int id = buffer.readInt();
        return new ChangeWanderRadiusPayload(wanderRadius, id);
    }

    private static void write(RegistryFriendlyByteBuf buf, ChangeWanderRadiusPayload packet) {
        buf.writeEnum(packet.wanderRadius);
        buf.writeInt(packet.id);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}

