package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class DragonWalkNodeEvaluator extends WalkNodeEvaluator {
    public DragonWalkNodeEvaluator() {}

    @Override
    protected boolean isNeighborValid(@Nullable Node neighbor, Node current) {
        if (!super.isNeighborValid(neighbor, current)) return false;
        if (neighbor == null) return false;
        double halfWidth = this.mob.getBbWidth() / 2f + 0.4f;
        AABB box = new AABB(
                neighbor.x + 0.5 - halfWidth,
                neighbor.y,
                neighbor.z + 0.5 - halfWidth,
                neighbor.x + 0.5 + halfWidth,
                neighbor.y + mob.getBbHeight(),
                neighbor.z + 0.5 + halfWidth
        );
        return !this.hasCollisions(box);
    }
}
