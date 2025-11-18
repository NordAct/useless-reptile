package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public record KeyInputC2SPacket(
        boolean jump,
        boolean forward,
        boolean back,
        boolean sprint,
        boolean secondaryAttack,
        boolean primaryAttack,
        boolean down,
        boolean freeLook,
        int id
) implements CustomPacketPayload{
    public static final ResourceLocation ID = UselessReptile.id("key_input");
    public static final Type<KeyInputC2SPacket> PACKET_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyInputC2SPacket> PACKET_CODEC =
            StreamCodec.of(KeyInputC2SPacket::write, KeyInputC2SPacket::read);

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id);
            if (entity instanceof URRideableDragonEntity dragon && dragon.canBeControlledByRider() && context.player().getVehicle() == entity) {
                dragon.updateInputs(packet.forward, packet.back, packet.jump, packet.down, packet.secondaryAttack, packet.primaryAttack, packet.sprint, packet.freeLook);
            }
        });
    }

    private static KeyInputC2SPacket read(RegistryFriendlyByteBuf buffer) {
        boolean jump = buffer.readBoolean();
        boolean forward = buffer.readBoolean();
        boolean back = buffer.readBoolean();
        boolean sprint = buffer.readBoolean();
        boolean secondaryAttack = buffer.readBoolean();
        boolean primaryAttack = buffer.readBoolean();
        boolean down = buffer.readBoolean();
        boolean freeLook = buffer.readBoolean();
        int id = buffer.readInt();
        return new KeyInputC2SPacket(jump, forward, back, sprint, secondaryAttack, primaryAttack, down, freeLook, id);
    }

    private static void write(RegistryFriendlyByteBuf buf, KeyInputC2SPacket packet) {
        buf.writeBoolean(packet.jump);
        buf.writeBoolean(packet.forward);
        buf.writeBoolean(packet.back);
        buf.writeBoolean(packet.sprint);
        buf.writeBoolean(packet.secondaryAttack);
        buf.writeBoolean(packet.primaryAttack);
        buf.writeBoolean(packet.down);
        buf.writeBoolean(packet.freeLook);
        buf.writeInt(packet.id);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
