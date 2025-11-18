package nordmods.uselessreptile.common.entity.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record ShootingPoint(Vec3 pos, Vec3 rotation) {
    public static final Codec<ShootingPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Vec3.CODEC.fieldOf("pos").forGetter(ShootingPoint::pos),
            Vec3.CODEC.fieldOf("rotation").forGetter(ShootingPoint::rotation)
        ).apply(inst, ShootingPoint::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf,ShootingPoint> PACKET_CODEC =  StreamCodec.of(
            (buf, value) -> {
                buf.writeVec3(value.pos());
                buf.writeVec3(value.rotation());
            },
            buf -> {
                Vec3 pos = buf.readVec3();
                Vec3 rot = buf.readVec3();
                return new ShootingPoint(pos, rot);
            }
    );
}
