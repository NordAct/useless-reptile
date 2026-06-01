package nordmods.uselessreptile.common.dragon_ability.holder;

import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class TriggerableAbilityHolder extends DragonAbilityHolder {
    private boolean wasTriggered;
    public TriggerableAbilityHolder(DragonAbility ability, URDragonEntity entity) {
        super(ability, entity);
    }

    public boolean wasTriggered() {
        return wasTriggered;
    }

    public void setWasTriggered(boolean wasTriggered) {
        this.wasTriggered = wasTriggered;
    }
}
