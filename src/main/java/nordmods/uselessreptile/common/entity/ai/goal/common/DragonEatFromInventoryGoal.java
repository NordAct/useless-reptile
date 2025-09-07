package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonEatFromInventoryGoal extends DragonConsumeItemFromInventoryGoal{
    public DragonEatFromInventoryGoal(URDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public void tick() {
        dragon.tickEatFromInventoryTimer();
        super.tick();
    }

    @Override
    public boolean canStart() {
        if (!dragon.isTamed()) return false;
        return dragon.getHealth() < dragon.getMaxHealth();
    }

    @Override
    protected void beforeItemConsumed(ItemStack stack) {
        dragon.heal(dragon.getFoodItem(stack).healingAmount());
    }

    @Override
    protected boolean canConsume() {
        return dragon.getEatFromInventoryTimer() == 0;
    }

    @Override
    protected boolean isConsumableItem(ItemStack stack) {
        return dragon.getFoodItem(stack) != null;
    }
}
