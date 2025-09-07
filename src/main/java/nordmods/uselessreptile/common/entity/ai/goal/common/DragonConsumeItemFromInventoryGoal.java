package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;

public abstract class DragonConsumeItemFromInventoryGoal extends Goal {
    protected final URDragonEntity dragon;

    public DragonConsumeItemFromInventoryGoal(URDragonEntity dragon) {
        this.dragon = dragon;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (canConsume()) {
            for (int i = DragonInventory.INVENTORY_START_INDEX; i <= dragon.getInventory().size(); i++) {
                ItemStack itemStack = dragon.getStackFromSlot(i);
                if (isConsumableItem(itemStack)) {
                    beforeItemConsumed(itemStack);
                    dragon.consumeGivenItem(dragon, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value(), null);
                    break;
                }
            }
        }
    }

    protected abstract void beforeItemConsumed(ItemStack stack);

    protected abstract boolean canConsume();

    protected abstract boolean isConsumableItem(ItemStack stack);
}
