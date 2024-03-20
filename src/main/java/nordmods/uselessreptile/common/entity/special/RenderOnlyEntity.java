package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

//dummy entity for purely rendering purposes
public class RenderOnlyEntity <T extends URDragonEntity> extends Entity implements GeoEntity {
    public T owner;
    public RenderOnlyEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public RenderOnlyEntity(World world) {
        super(UREntities.RENDER_ONLY_ENTITY, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < 64.0 * getRenderDistanceMultiplier();
    }

    @Override
    public float getYaw() {
        return owner.getYaw();
    }

    @Override
    public float getYaw(float delta) {
        return owner.getYaw(delta);
    }

    @Override
    public float getHeadYaw() {
        return owner.getYaw();
    }

    @Override
    public float getBodyYaw() {
        return owner.getBodyYaw();
    }

    @Override
    public float getPitch() {
        return owner.getPitch();
    }

    @Override
    public float getLerpTargetYaw() {
        return owner.getLerpTargetYaw();
    }

    @Override
    public float getPitch(float delta) {
        return owner.getPitch(delta);
    }

    @Override
    public Vec2f getRotationClient() {
        return owner.getRotationClient();
    }


}
