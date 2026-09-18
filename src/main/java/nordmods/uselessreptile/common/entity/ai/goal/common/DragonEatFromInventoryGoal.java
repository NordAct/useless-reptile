package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonEatFromInventoryGoal<T extends URDragonEntity> extends DragonConsumeItemFromInventoryGoal<T>{
    public DragonEatFromInventoryGoal(T mob) {
        super(mob);
    }

    @Override
    public void tick() {
        mob.tickEatFromInventoryTimer();
        super.tick();
    }

    @Override
    public boolean canUse() {
        if (!mob.isTame()) return false;
        return mob.getHealth() < mob.getMaxHealth();
    }

    @Override
    protected void beforeItemConsumed(ItemStack stack) {
        mob.heal(mob.getFoodItem(stack).healingAmount());
        if (mob.getOwner() instanceof ServerPlayer serverPlayer)
            URDragonEntity.grantTriggerableAdvancement(serverPlayer, UselessReptile.id("dragon/eat_from_inventory"));
    }

    @Override
    protected boolean canConsume() {
        return mob.getEatFromInventoryTimer() == 0;
    }

    @Override
    protected boolean isConsumableItem(ItemStack stack) {
        return mob.getFoodItem(stack) != null;
    }
}
