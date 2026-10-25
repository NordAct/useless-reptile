package nordmods.uselessreptile.common.init;

import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;

public class UREntityDataSerializers {
    public static final EntityDataSerializer<URDragonEntity.Order> ORDER = EntityDataSerializer.forValueType(URDragonEntity.Order.STREAM_CODEC);
    public static final EntityDataSerializer<URDragonEntity.WanderRadius> WANDER_RADIUS = EntityDataSerializer.forValueType(URDragonEntity.WanderRadius.STREAM_CODEC);
    public static final EntityDataSerializer<URDragonEntity.TurningState> TURNING_STATE = EntityDataSerializer.forValueType(URDragonEntity.TurningState.STREAM_CODEC);
    public static final EntityDataSerializer<FlyingDragon.TiltState> TILT_STATE = EntityDataSerializer.forValueType(FlyingDragon.TiltState.STREAM_CODEC);
    public static final EntityDataSerializer<Optional<Float>> OPTIONAL_FLOAT = EntityDataSerializer.forValueType(ByteBufCodecs.optional(ByteBufCodecs.FLOAT));
    public static final EntityDataSerializer<Int2FloatArrayMap> INT_TO_FLOAT_MAP = EntityDataSerializer.forValueType(
            ByteBufCodecs.map(
                    Int2FloatArrayMap::new,
                    ByteBufCodecs.INT,
                    ByteBufCodecs.FLOAT
            )
    );

    public static void init() {
        FabricEntityDataRegistry.register(UselessReptile.id("order"), ORDER);
        FabricEntityDataRegistry.register(UselessReptile.id("wander_radius"), WANDER_RADIUS);
        FabricEntityDataRegistry.register(UselessReptile.id("turning_state"), TURNING_STATE);
        FabricEntityDataRegistry.register(UselessReptile.id("tilt_state"), TILT_STATE);
        FabricEntityDataRegistry.register(UselessReptile.id("optional_float"), OPTIONAL_FLOAT);
        FabricEntityDataRegistry.register(UselessReptile.id("int_to_float_map"), INT_TO_FLOAT_MAP);
    }
}
