package nordmods.uselessreptile.common.entity.misc;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.uselessreptile.common.init.UREntities;
import org.jspecify.annotations.NonNull;

public class Placeholder extends Entity {
    public Placeholder(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }

    public Placeholder(Level level) {
        this(UREntities.PLACEHOLDER, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float f) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {}

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void stopRiding() {
        if (getRemovalReason() == null || !getRemovalReason().shouldDestroy()) this.discard();
        else super.stopRiding();
    }

    protected boolean canAddPassenger(@NonNull Entity entity) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    protected void addPassenger(@NonNull Entity entity) {}

    @Override
    public @NonNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public void tick() {
    }
}
