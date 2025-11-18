package nordmods.uselessreptile.client.util.duck;

import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface ShootingPointCommandRendererHelper {
    default List<ShootingPointCommandRenderer.ShootingPointCommand> useless_reptile$getShootingPointCommands() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$submitShootingPoint(PoseStack matrices, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {
        throw new AssertionError("Implemented in mixin");
    }
}
