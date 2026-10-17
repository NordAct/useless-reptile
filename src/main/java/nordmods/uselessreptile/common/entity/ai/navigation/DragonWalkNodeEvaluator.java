package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DragonWalkNodeEvaluator extends WalkNodeEvaluator {
    public DragonWalkNodeEvaluator() {}

    @Override
    protected boolean isNeighborValid(@Nullable Node neighbor, @NonNull Node current) {
        if (!super.isNeighborValid(neighbor, current)) return false;
        if (fitsWithinBlock()) return true; //small dragons do pathfind correctly already
        if (neighbor == null) return false;
        double halfWidth = this.mob.getBbWidth() / 2f + 0.4f;
        if (neighbor.y > current.y) { //if going up, check if can do that from current node
            AABB box = new AABB(
                    current.x + 0.5 - halfWidth,
                    current.y,
                    current.z + 0.5 - halfWidth,
                    current.x + 0.5 + halfWidth,
                    neighbor.y + mob.getBbHeight(),
                    current.z + 0.5 + halfWidth
            );
            if (hasCollisions(box)) return false;
        } else if (neighbor.y < current.y) { //if going down, check if can actually go down from current node
            AABB box = new AABB(
                    neighbor.x + 0.5 - halfWidth,
                    neighbor.y,
                    neighbor.z + 0.5 - halfWidth,
                    neighbor.x + 0.5 + halfWidth,
                    current.y + mob.getBbHeight(),
                    neighbor.z + 0.5 + halfWidth
            );
            if (hasCollisions(box)) return false;
        }
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

    public boolean fitsWithinBlock() {
        return entityWidth <= 1 && entityHeight <= 1 && entityDepth <= 1;
    }
}
