package nordmods.uselessreptile.common.entity.base;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class URDragonPart extends EntityPart {
    public final String name;
    private float heightMod = 1;
    private float widthMod = 1;
    private final float damageMultiplier;

    public URDragonPart(URDragonEntity owner, String name) {
        this(owner, name, 1);
    }

    public URDragonPart(URDragonEntity owner, String name, float damageMultiplier) {
        super(owner, 1, 1);
        this.name = name;
        this.damageMultiplier = damageMultiplier;
        refreshDimensions();
    }

    public URDragonEntity getOwner() {
        return (URDragonEntity) owner;
    }

    @Nullable
    public static URDragonEntity getPartParent(Player user) {
        HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(user, entity -> entity instanceof URDragonPart, user.entityInteractionRange());
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof URDragonPart part) return part.getOwner();
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HEIGHT_MODIFIER, 1f);
        builder.define(WIDTH_MODIFIER, 1f);
    }

    public static final EntityDataAccessor<Float> HEIGHT_MODIFIER = SynchedEntityData.defineId(URDragonPart.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> WIDTH_MODIFIER = SynchedEntityData.defineId(URDragonPart.class, EntityDataSerializers.FLOAT);

    public float getHeightMod() {return entityData.get(HEIGHT_MODIFIER);}
    public void setHeightMod(float state) {entityData.set(HEIGHT_MODIFIER, state);}

    public float getWidthMod() {return entityData.get(WIDTH_MODIFIER);}
    public void setWidthMod(float state) {entityData.set(WIDTH_MODIFIER, state);}

    public boolean checkInvulnerability(ServerLevel world, DamageSource damageSource) {
        boolean riderOwner = false;
        if (damageSource.getEntity() instanceof Player player)
            riderOwner = player.getVehicle() == getOwner() && getOwner().getOwner() == player;
        return riderOwner || getOwner().isInvulnerableTo(world, damageSource);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (checkInvulnerability(world, source)) return false;
        return super.hurtServer(world, source, amount * damageMultiplier);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(widthMod, heightMod);
    }

    @Override
    public boolean isPickable() {
        return getOwner().isPickable();
    }

    @Override
    protected @NonNull Component getTypeName() {
        return getOwner().getTypeName();
    }

    public void setScale(float destinationHeight, float destinationWidth) {
        destinationWidth *= getOwner().getScale();
        destinationHeight *= getOwner().getScale();
        float widthMod = getWidthMod();
        float heightMod = getHeightMod();
        float widthDiff = widthMod - destinationWidth;
        float heightDiff = heightMod - destinationHeight;

        if (widthDiff != 0) {
            if (widthDiff > getOwner().getWidthModTransSpeed()) widthMod -= getOwner().getWidthModTransSpeed();
            else if (widthDiff < -getOwner().getWidthModTransSpeed()) widthMod += getOwner().getWidthModTransSpeed();
            else widthMod = destinationWidth;
        }

        if (heightDiff != 0) {
            if (heightDiff > getOwner().getHeightModTransSpeed()) heightMod -= getOwner().getHeightModTransSpeed();
            else if (heightDiff < -getOwner().getHeightModTransSpeed()) heightMod += getOwner().getHeightModTransSpeed();
            else heightMod = destinationHeight;
        }

        setHeightMod(heightMod);
        setWidthMod(widthMod);

        this.heightMod = heightMod;
        this.widthMod = widthMod;
        refreshDimensions();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setScale(Vec2 scale) {
        setScale(scale.x, scale.y);
    }

    public void setRelativePos(double x, double y, double z) {
        setRelativePos(x * getOwner().getScale(), y * getOwner().getScale(), z * getOwner().getScale(), 0, getOwner().getYRot());
    }

    public void setRelativePos(Vector3f vector3f) {
        setRelativePos(vector3f.x, vector3f.y, vector3f.z);
    }
}
