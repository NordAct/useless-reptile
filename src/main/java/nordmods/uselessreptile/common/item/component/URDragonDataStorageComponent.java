package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record URDragonDataStorageComponent(List<NbtComponent> entityData) {
    public static final Codec<URDragonDataStorageComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            NbtComponent.CODEC.listOf().fieldOf("dragons").forGetter(URDragonDataStorageComponent::entityData))
                    .apply(instance, URDragonDataStorageComponent::new));

    public static final PacketCodec<ByteBuf, URDragonDataStorageComponent> PACKET_CODEC = PacketCodec.tuple(
            NbtComponent.PACKET_CODEC.collect(PacketCodecs.toList()), URDragonDataStorageComponent::entityData,
            URDragonDataStorageComponent::new);

    public static final URDragonDataStorageComponent DEFAULT = new URDragonDataStorageComponent(List.of());

    private static final List<String> IGNORED_NBT = Arrays.asList(
            "Air",
            "ArmorDropChances",
            "ArmorItems",
            "Brain",
            "DeathTime",
            "FallDistance",
            "FallFlying",
            "Fire",
            "HandDropChances",
            "HandItems",
            "HurtByTimestamp",
            "HurtTime",
            "LeftHanded",
            "Motion",
            "NoGravity",
            "OnGround",
            "PortalCooldown",
            "Pos",
            "Rotation",
            "SleepingX",
            "SleepingY",
            "SleepingZ",
            "Passengers",
            "Sitting",
            "BoundedInstrumentSound",
            "HomePoint",
            "IsFlying",
            "leash");

    public static NbtComponent createData(Entity entity) {
        NbtCompound nbtCompound = new NbtCompound();
        entity.saveSelfNbt(nbtCompound);
        Objects.requireNonNull(nbtCompound);
        IGNORED_NBT.forEach(nbtCompound::remove);
        return NbtComponent.of(nbtCompound);
    }
    @Nullable
    public static Entity createEntity(NbtComponent nbtComponent, World world) {
        Objects.requireNonNull(nbtComponent);
        NbtCompound nbtCompound = nbtComponent.copyNbt();
        IGNORED_NBT.forEach(nbtCompound::remove);
        return EntityType.loadEntityWithPassengers(nbtCompound, world, (entityx) -> entityx);
    }
}
