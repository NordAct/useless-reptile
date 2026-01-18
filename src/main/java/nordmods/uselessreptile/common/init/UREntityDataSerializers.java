package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class UREntityDataSerializers {
    public static final EntityDataSerializer<URDragonEntity.Order> ORDER = EntityDataSerializer.forValueType(URDragonEntity.Order.STREAM_CODEC);
    public static final EntityDataSerializer<URDragonEntity.WanderRadius> WANDER_RADIUS = EntityDataSerializer.forValueType(URDragonEntity.WanderRadius.STREAM_CODEC);

    public static void init() {
        FabricTrackedDataRegistry.register(UselessReptile.id("wanderRadius"), ORDER);
        FabricTrackedDataRegistry.register(UselessReptile.id("wander_radius"), WANDER_RADIUS);
    }
}
