package nordmods.uselessreptile.common.entity.multipart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

public class URDragonPart extends Entity{
    public final URDragonEntity owner;
    private float heightMod = 1;
    private float widthMod = 1;
    private final float damageMultiplier;

    public URDragonPart(URDragonEntity owner) {
        super(owner.getType(), owner.getWorld());
        this.owner = owner;
        this.damageMultiplier = 1;
    }

    public URDragonPart(URDragonEntity owner, float damageMultiplier) {
        super(owner.getType(), owner.getWorld());
        this.owner = owner;
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(HEIGHT_MODIFIER, 1f);
        dataTracker.startTracking(WIDTH_MODIFIER, 1f);
    }

    public static final TrackedData<Float> HEIGHT_MODIFIER = DataTracker.registerData(URDragonPart.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> WIDTH_MODIFIER = DataTracker.registerData(URDragonPart.class, TrackedDataHandlerRegistry.FLOAT);

    public float getHeightMod() {return dataTracker.get(HEIGHT_MODIFIER);}
    public void setHeightMod(float state) {dataTracker.set(HEIGHT_MODIFIER, state);}

    public float getWidthMod() {return dataTracker.get(WIDTH_MODIFIER);}
    public void setWidthMod(float state) {dataTracker.set(WIDTH_MODIFIER, state);}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        return owner.damage(source, amount * damageMultiplier);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        boolean riderOwner = false;
        if (damageSource.getSource() instanceof PlayerEntity player)
            riderOwner = player.getVehicle() == owner && owner.getOwner() == player;
        return owner.isInvulnerableTo(damageSource) || riderOwner;
    }

    @Override
    public boolean isPartOf(Entity entity) {
        return this == entity || owner == entity;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (getWorld().isClient() && !owner.isTamed()) return ActionResult.CONSUME;
        return ActionResult.SUCCESS;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(widthMod, heightMod);
    }

    @Override
    public boolean isAlive() {
        return owner.isAlive();
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return this.owner.getPickBlockStack();
    }

    public void setScale(float destinationHeight, float destinationWidth) {
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
        calculateDimensions();
    }

    public void setScale(Vec2f vec2f) {
        setScale(vec2f.x, vec2f.y);
    }

    public void setRelativePos(double x, double y, double z) {
        Vec3d rot = getRotationVector(0, owner.getYaw());
        setPosition(owner.getX() + x * rot.z + z * rot.x,
                owner.getY() + y,
                owner.getZ() + z * rot.z - x * rot.x);
        Vec3d vec3ds = new Vec3d(getX(), getY(),getZ());
        prevX = vec3ds.x;
        prevY = vec3ds.y;
        prevZ = vec3ds.z;
        lastRenderX = vec3ds.x;
        lastRenderY = vec3ds.y;
        lastRenderZ = vec3ds.z;
    }

    public void setRelativePos(Vec3f vector3f) {
        setRelativePos(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public void setRelativePos(Vec3d vec3d) {
        setRelativePos(vec3d.x, vec3d.y, vec3d.z);
    }
}
