package nordmods.uselessreptile.common.entity.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

public record ShootingPoint(Vec3d pos, Vec3d rotation) {
    public static final Codec<ShootingPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Vec3d.CODEC.fieldOf("pos").forGetter(ShootingPoint::pos),
            Vec3d.CODEC.fieldOf("rotation").forGetter(ShootingPoint::rotation)
        ).apply(inst, ShootingPoint::new)
    );
    public static final PacketCodec<ByteBuf,ShootingPoint> PACKET_CODEC = new PacketCodec<>() {

        @Override
        public void encode(ByteBuf buf, ShootingPoint value) {
            Vec3d.PACKET_CODEC.encode(buf, value.pos());
            Vec3d.PACKET_CODEC.encode(buf, value.rotation());
        }

        @Override
        public ShootingPoint decode(ByteBuf buf) {
            Vec3d pos = Vec3d.PACKET_CODEC.decode(buf);
            Vec3d rot = Vec3d.PACKET_CODEC.decode(buf);
            return new ShootingPoint(pos, rot);
        }
    };
}
