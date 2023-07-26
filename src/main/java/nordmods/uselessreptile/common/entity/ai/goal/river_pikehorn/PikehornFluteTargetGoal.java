package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.items.FluteItem;

public class PikehornFluteTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {

    private final RiverPikehornEntity mob;
    public PikehornFluteTargetGoal(RiverPikehornEntity mob, Class<T> targetClass) {
        super(mob, targetClass, false);
        this.mob = mob;
    }

    public boolean canStart() {
        if (!mob.isTamed()) return false;
        PlayerEntity player = (PlayerEntity) mob.getOwner();
        if (player == null) return false;

        ItemStack main = player.getMainHandStack();
        ItemStack offhand = player.getOffHandStack();
        boolean mainCanTarget = main.hasNbt() && main.getNbt().getInt(FluteItem.MODE_TAG) == 2;
        boolean offhandCanTarget = offhand.hasNbt() && offhand.getNbt().getInt(FluteItem.MODE_TAG) == 2;
        if (!(player.getItemCooldownManager().isCoolingDown(URItems.FLUTE) && (mainCanTarget || offhandCanTarget))) return false;

        float range = 4096;
        Vec3d rot = player.getRotationVec(1);
        EntityHitResult hitResult = ProjectileUtil
                .raycast(player,
                        player.getCameraPosVec(1),
                        player.getCameraPosVec(1).add(rot.multiply(range)),
                        player.getBoundingBox().stretch(rot.multiply(range)).expand(1.0, 1.0, 1.0),
                        entity -> !entity.isSpectator() && entity.canHit(), range);

        if (hitResult != null) targetEntity = (LivingEntity) hitResult.getEntity();

        return targetEntity != null;
    }
}
