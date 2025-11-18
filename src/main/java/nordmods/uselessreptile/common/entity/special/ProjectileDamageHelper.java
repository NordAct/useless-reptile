package nordmods.uselessreptile.common.entity.special;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;


/**
 * Helper interface for easier damage calculation for projectiles
 */
public interface ProjectileDamageHelper extends TraceableEntity {
    default float getResultingDamage() {
        if (getOwner() instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
            return (float) (livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * getDamageScaling());
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
