package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.items.FluteItem;

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
        boolean mainCanGather = main.hasNbt() && main.getNbt().getInt(FluteItem.MODE_TAG) != 1 && main.getNbt().getInt(FluteItem.MODE_TAG) != 2 || !main.hasNbt();
        boolean offhandCanGather = offhand.hasNbt() && offhand.getNbt().getInt(FluteItem.MODE_TAG) != 1 && offhand.getNbt().getInt(FluteItem.MODE_TAG) != 2  || !main.hasNbt();

        return player.getItemCooldownManager().isCoolingDown(URItems.FLUTE) && (mainCanGather || offhandCanGather);
    }
}
