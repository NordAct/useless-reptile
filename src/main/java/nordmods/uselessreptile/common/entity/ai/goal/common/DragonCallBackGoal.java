package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonCallBackGoal extends Goal {
    protected final URDragonEntity entity;
    protected LivingEntity owner;
    protected double maxCallDistance = 65536;
    protected boolean isFollowing = false;
    protected int updateCountdownTicks;
    protected int ticksToStop;

    public DragonCallBackGoal(URDragonEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public void start() {
        updateCountdownTicks = 0;
        isFollowing = true;
        owner = entity.getOwner();
        entity.setIsSitting(false);
        ticksToStop = 0;
        entity.setTarget(null);
    }

    @Override
    public boolean canStart() {
        if (!entity.isTamed()) return false;
        if (entity.isLeashed() || entity.hasVehicle()) return false;
        PlayerEntity player = (PlayerEntity) entity.getOwner();
        if (player == null) return false;
        if (isFollowing) return true;
        if (entity.squaredDistanceTo(player) > maxCallDistance) return false;
        ItemStack main = player.getMainHandStack();
        if (entity.isInstrument(main)) {
            String sound = entity.getInstrument(main);
            return player.isUsingItem() && sound.equals(entity.getBoundedInstrumentSound());
        }
        ItemStack offhand = player.getOffHandStack();
        if (entity.isInstrument(offhand)) {
            String sound = entity.getInstrument(offhand);
            return player.isUsingItem() && sound.equals(entity.getBoundedInstrumentSound());
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if (!owner.isAlive()) return false;
        return canStart();
    }

    @Override
    public void stop() {
        owner = null;
        entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        entity.getLookControl().lookAt(owner, entity.getRotationSpeed(), entity.getPitchLimit());
        entity.setSprinting(true);
        double distance = entity.squaredDistanceTo(owner);
        double maxDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);

        if (entity.isSitting()) stopMoving();

        if (distance < maxDistance && owner.isOnGround()) stopMoving();

        if (--updateCountdownTicks <= 0) {
            updateCountdownTicks = getTickCount(10);
            entity.getNavigation().startMovingTo(owner, 1);
        }
    }

    protected void stopMoving() {
        isFollowing = false;}
}
