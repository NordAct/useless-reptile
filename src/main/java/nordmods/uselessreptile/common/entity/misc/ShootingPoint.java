package nordmods.uselessreptile.common.entity.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public record ShootingPoint(Vector3f position, Vector3f rotation) {
    public static final Codec<ShootingPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.VECTOR3F.fieldOf("position").forGetter(ShootingPoint::position),
            ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter(ShootingPoint::rotation)
        ).apply(inst, (position, rotation) -> new ShootingPoint((Vector3f) position, (Vector3f) rotation))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf,ShootingPoint> PACKET_CODEC =  StreamCodec.of(
            (buf, value) -> {
                buf.writeVector3f(value.position());
                buf.writeVector3f(value.rotation());
            },
            buf -> {
                Vector3f pos = buf.readVector3f();
                Vector3f rot = buf.readVector3f();
                return new ShootingPoint(pos, rot);
            }
    );
}
