package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;

public abstract class DragonConsumeItemFromInventoryGoal extends Goal {
    protected final URDragonEntity dragon;

    public DragonConsumeItemFromInventoryGoal(URDragonEntity dragon) {
        this.dragon = dragon;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (canConsume()) {
            for (int i = DragonInventory.INVENTORY_START_INDEX; i <= dragon.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = dragon.getStackFromSlot(i);
                if (isConsumableItem(itemStack)) {
                    beforeItemConsumed(itemStack);
                    dragon.consumeGivenItem(dragon, itemStack, SoundEvents.GENERIC_EAT.value(), null);
                    break;
                }
            }
        }
    }

    protected abstract void beforeItemConsumed(ItemStack stack);

    protected abstract boolean canConsume();

    protected abstract boolean isConsumableItem(ItemStack stack);
}
