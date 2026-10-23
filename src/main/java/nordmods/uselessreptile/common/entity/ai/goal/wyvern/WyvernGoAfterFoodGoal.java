package nordmods.uselessreptile.common.entity.ai.goal.wyvern;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import nordmods.uselessreptile.common.entity.Wyvern;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class WyvernGoAfterFoodGoal extends Goal {
    private final Wyvern mob;
    private ItemEntity food;

    public WyvernGoAfterFoodGoal(Wyvern mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !mob.isTame() && mob.getTarget() == null && (food = findFood()) != null;
    }

    @Override
    public void tick() {
        mob.setSprinting(true);
        mob.getNavigation().moveTo(food, 1);
    }

    @Nullable
    private ItemEntity findFood() {
        AABB box = mob.getBoundingBox().inflate(20);
        List<ItemEntity> drops = mob.level().getEntitiesOfClass(ItemEntity.class, box.setMinY(box.minY - 20), (item) -> {
            ItemStack itemStack = item.getItem();
            return mob.isFood(itemStack) && item.isAlive() && !item.hasPickUpDelay() && mob.getSensing().hasLineOfSight(item);
        });
        if (drops.isEmpty()) return null;
        return drops.getFirst();
    }
}
