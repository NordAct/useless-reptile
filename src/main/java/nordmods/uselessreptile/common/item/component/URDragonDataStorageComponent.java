package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import nordmods.uselessreptile.UselessReptile;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;

public record URDragonDataStorageComponent(List<CustomData> entityData) {
    public static final Codec<URDragonDataStorageComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            CustomData.CODEC.listOf().fieldOf("dragons").forGetter(URDragonDataStorageComponent::entityData))
                    .apply(instance, URDragonDataStorageComponent::new));

    public static final StreamCodec<ByteBuf, URDragonDataStorageComponent> PACKET_CODEC = StreamCodec.composite(
            CustomData.STREAM_CODEC.apply(ByteBufCodecs.list()), URDragonDataStorageComponent::entityData,
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

    public static CustomData createData(Entity entity) {
        TagValueOutput nbtWriteView = TagValueOutput.createWithContext(UselessReptile.ERROR_REPORTER, entity.registryAccess());
        entity.saveAsPassenger(nbtWriteView);
        IGNORED_NBT.forEach(nbtWriteView::discard);
        return CustomData.of(nbtWriteView.buildResult());
    }
    @Nullable
    public static Entity createEntity(CustomData nbtComponent, Level world) {
        Objects.requireNonNull(nbtComponent);
        CompoundTag nbtCompound = nbtComponent.copyTag();
        IGNORED_NBT.forEach(nbtCompound::remove);
        return EntityType.loadEntityRecursive(nbtCompound, world, EntitySpawnReason.SPAWN_ITEM_USE, (entityx) -> entityx);
    }
}
