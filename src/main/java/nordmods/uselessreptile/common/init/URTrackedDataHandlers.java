package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.entity.data.TrackedDataHandler;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;

public class URTrackedDataHandlers {
    public static final TrackedDataHandler<ShootingPoint> SHOOTING_POINT = TrackedDataHandler.create(ShootingPoint.PACKET_CODEC);

    public static void init() {
        FabricTrackedDataRegistry.register(UselessReptile.id("shooting_point"), SHOOTING_POINT);
    }
}
