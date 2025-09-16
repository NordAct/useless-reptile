package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public class DragonConsumeFoodFromInventoryGoal extends Goal {
    private final URDragonEntity dragon;

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
        if (dragon.getEatFromInventoryTimer() == 0) {
            for (int i = 0; i <= URDragonScreenHandler.maxStorageSize; i++) {
                ItemStack itemStack = dragon.getStackFromSlot(i);
                if (dragon.isFavoriteFood(itemStack)) {
                    dragon.consumeGivenItem(dragon, itemStack, SoundEvents.ENTITY_GENERIC_EAT, null);
                    dragon.heal(dragon.getHealthRegenerationFromFood());
                    break;
                }
            }
        }
    }
}
