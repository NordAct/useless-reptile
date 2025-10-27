package nordmods.uselessreptile.client.util.duck;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;

import java.util.List;

public interface ShootingPointCommandRendererHelper {
    default List<ShootingPointCommandRenderer.ShootingPointCommand> useless_reptile$getShootingPointCommands() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$submitShootingPoint(MatrixStack matrices, LivingEntityRenderState renderState, ShootingPoint shootingPoint) {
        throw new AssertionError("Implemented in mixin");
    }
}
