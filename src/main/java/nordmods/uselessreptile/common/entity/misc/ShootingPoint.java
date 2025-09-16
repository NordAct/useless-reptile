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
            buf.writeDouble(value.pos().x);
            buf.writeDouble(value.pos().y);
            buf.writeDouble(value.pos().z);
            buf.writeDouble(value.rotation().x);
            buf.writeDouble(value.rotation().y);
            buf.writeDouble(value.rotation().z);
        }

        @Override
        public ShootingPoint decode(ByteBuf buf) {
            double posX = buf.readDouble();
            double posY = buf.readDouble();
            double posZ = buf.readDouble();
            double rotationX = buf.readDouble();
            double rotationY = buf.readDouble();
            double rotationZ = buf.readDouble();
            return new ShootingPoint(new Vec3d(posX, posY, posZ), new Vec3d(rotationX, rotationY, rotationZ));
        }
    };
}