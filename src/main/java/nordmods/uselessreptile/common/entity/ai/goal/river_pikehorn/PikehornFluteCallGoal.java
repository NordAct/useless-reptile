package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.FluteItem;

public class PikehornFluteCallGoal extends FlyingDragonCallBackGoal<RiverPikehornEntity> {

    public PikehornFluteCallGoal(RiverPikehornEntity entity) {
        super(entity);
        maxCallDistance = 4096;
    }

    @Override
    public boolean canStart() {
        if (!entity.isTamed()) return false;
        if (entity.isLeashed() || entity.hasVehicle() || entity.isSitting()) return false;
        PlayerEntity player = (PlayerEntity) entity.getOwner();
        if (player == null) return false;
        if (isFollowing) return true;
        if (entity.squaredDistanceTo(player) > maxCallDistance) return false;

        ItemStack main = player.getMainHandStack();
        ItemStack offhand = player.getOffHandStack();
        boolean mainCanGather = main.getItem() instanceof FluteItem fluteItem && fluteItem.getFluteMode(main) == 0;
        boolean offhandCanGather = offhand.getItem() instanceof FluteItem fluteItem && fluteItem.getFluteMode(offhand) == 0;

        return player.getItemCooldownManager().isCoolingDown(URItems.FLUTE) && (mainCanGather || offhandCanGather);
    }
}
