package nordmods.uselessreptile.common.entity.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

public record ShootingPoint(Vec3d pos, Vec3d rotation) {
    public static final Codec<ShootingPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Vec3d.CODEC.fieldOf("pos").forGetter(ShootingPoint::pos),
            Vec3d.CODEC.fieldOf("rotation").forGetter(ShootingPoint::rotation)
        ).apply(inst, ShootingPoint::new)
    );

    public static final PacketCodec<RegistryByteBuf,ShootingPoint> PACKET_CODEC =  PacketCodec.ofStatic(
            (buf, value) -> {
                buf.writeVec3d(value.pos());
                buf.writeVec3d(value.rotation());
            },
            buf -> {
                Vec3d pos = buf.readVec3d();
                Vec3d rot = buf.readVec3d();
                return new ShootingPoint(pos, rot);
            }
    );
}
