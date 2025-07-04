package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;

public class DragonConsumeFoodFromInventoryGoal extends Goal {
    protected final URDragonEntity dragon;

    public DragonConsumeFoodFromInventoryGoal(URDragonEntity dragon) {
        this.dragon = dragon;
    }

    @Override
    public boolean canStart() {
        if (!dragon.isTamed()) return false;
        return dragon.getHealth() < dragon.getMaxHealth();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        dragon.tickEatFromInventoryTimer();
        if (canConsume()) {
            for (int i = DragonInventory.INVENTORY_START_INDEX; i <= dragon.getInventory().size(); i++) {
                ItemStack itemStack = dragon.getStackFromSlot(i);
                if (dragon.isFavoriteFood(itemStack)) {
                    dragon.consumeGivenItem(dragon, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value());
                    afterItemConsumed(itemStack);
                    break;
                }
            }
        }
    }

    protected void afterItemConsumed(ItemStack stack) {
        dragon.heal(dragon.getHealthRegenerationFromFood());
    }

    protected boolean canConsume() {
        return dragon.getEatFromInventoryTimer() == 0;
    }
}
