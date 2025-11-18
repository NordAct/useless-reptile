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
import net.minecraft.world.phys.Vec2;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class URDragonPart extends EntityPart {
    public final URDragonEntity owner;
    private float heightMod = 1;
    private float widthMod = 1;
    private final float damageMultiplier;

    public URDragonPart(URDragonEntity owner) {
        this(owner, 1);
    }

    public URDragonPart(URDragonEntity owner, float damageMultiplier) {
        super(owner, 1, 1);
        this.owner = owner;
        this.damageMultiplier = damageMultiplier;
        refreshDimensions();
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
            riderOwner = player.getVehicle() == owner && owner.getOwner() == player;
        return riderOwner || owner.isInvulnerableTo(world, damageSource);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (checkInvulnerability(world, source)) return false;
        return super.hurtServer(world, source, amount * damageMultiplier);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(widthMod, heightMod);
    }

    @Override
    public boolean isPickable() {
        return owner.isPickable();
    }

    @Override
    protected @NotNull Component getTypeName() {
        return owner.getTypeName();
    }

    public void setScale(float destinationHeight, float destinationWidth) {
        destinationWidth *= owner.getScale();
        destinationHeight *= owner.getScale();
        float widthMod = getWidthMod();
        float heightMod = getHeightMod();
        float widthDiff = widthMod - destinationWidth;
        float heightDiff = heightMod - destinationHeight;

        if (widthDiff != 0) {
            if (widthDiff > owner.getWidthModTransSpeed()) widthMod -= owner.getWidthModTransSpeed();
            else if (widthDiff < -owner.getWidthModTransSpeed()) widthMod += owner.getWidthModTransSpeed();
            else widthMod = destinationWidth;
        }

        if (heightDiff != 0) {
            if (heightDiff > owner.getHeightModTransSpeed()) heightMod -= owner.getHeightModTransSpeed();
            else if (heightDiff < -owner.getHeightModTransSpeed()) heightMod += owner.getHeightModTransSpeed();
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
        setRelativePos(x * owner.getScale(), y * owner.getScale(), z * owner.getScale(), 0, owner.getYRot());
    }

    public void setRelativePos(Vector3f vector3f) {
        setRelativePos(vector3f.x, vector3f.y, vector3f.z);
    }
}
