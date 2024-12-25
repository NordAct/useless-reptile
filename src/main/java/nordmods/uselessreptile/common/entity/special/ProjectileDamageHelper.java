package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.attribute.EntityAttributes;


/**
 * Helper interface for easier damage calculation for projectiles
 */
public interface ProjectileDamageHelper extends Ownable {
    default float getResultingDamage() {
        if (getOwner() instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(EntityAttributes.ATTACK_DAMAGE))
            return (float) (livingEntity.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) * getDamageScaling());
        return getDefaultDamage();
    }

    /**
     * @return damage if owner couldn't be found
     */
    float getDefaultDamage();

    /**
     * @return scale factor of owner's damage
     */
    float getDamageScaling();
}
