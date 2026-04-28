package nordmods.uselessreptile.common.entity.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ShotAttackAbility extends TriggerableAbility {
    protected final EntityType<?> projectileEntityType;
    protected final CompoundTag projectileEntityNbt;
    protected final AnchorPoint anchorPoint;
    protected final Optional<String> multipartBoxName;
    protected final Vec3 anchorPointOffset;
    protected final int count;
    protected final float speed;
    protected final float spread;

    public static final MapCodec<ShotAttackAbility> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonAbilityData.MAP_CODEC.forGetter(ShotAttackAbility::getCommonAbilityData),
            Data.MAP_CODEC.forGetter(ShotAttackAbility::getTriggerableAbilityData),
            EntityType.CODEC.fieldOf("projectile_entity_type").forGetter(c -> c.projectileEntityType),
            CompoundTag.CODEC.fieldOf("projectile_entity_nbt").forGetter(c -> c.projectileEntityNbt),
            StringRepresentable.fromEnum(AnchorPoint::values).fieldOf("anchor_point").forGetter(c -> c.anchorPoint),
            Codec.STRING.optionalFieldOf("multipart_box_name").forGetter(c -> c.multipartBoxName),
            Vec3.CODEC.fieldOf("anchor_point_offset").forGetter(c -> c.anchorPointOffset),
            ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(c -> c.count),
            ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("speed").forGetter(c -> c.speed),
            ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("spread").forGetter(c -> c.spread)
    ).apply(i, ShotAttackAbility::new));

    public ShotAttackAbility(CommonDragonAbilityData commonAbilityData, Data triggerableAbilityData, EntityType<?> projectileEntityType, CompoundTag projectileEntityNbt, AnchorPoint anchorPoint, Optional<String> multipartBoxName, Vec3 anchorPointOffset, int count, float speed, float spread) {
        if (anchorPoint == AnchorPoint.MULTIPART_BOX && multipartBoxName.isEmpty())
            throw new IllegalStateException("Multipart box name must be specified for multipart_box anchor point");

        super(commonAbilityData, triggerableAbilityData);
        this.projectileEntityType = projectileEntityType;
        this.projectileEntityNbt = projectileEntityNbt;
        this.anchorPoint = anchorPoint;
        this.multipartBoxName = multipartBoxName;
        this.anchorPointOffset = anchorPointOffset;
        this.count = count;
        this.speed = speed;
        this.spread = spread;
    }

    @Override
    public void trigger(URDragonEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        Vec3 rot = getRot(entity);
        Vec3 pos = getPos(entity);
        for (int i = 0; i < count; i++) {
            Projectile projectile = createProjectile(level);
            projectile.setPos(pos);
            projectile.shoot(rot.x, rot.y, rot.z, speed, spread);
            level.addFreshEntity(projectile);
        }
    }

    protected Vec3 getPos(URDragonEntity entity) {
        return switch (anchorPoint) {
            case EYES -> entity.getEyePosition().add(anchorPointOffset.yRot(entity.getYawWithAdjustment()).xRot(entity.getXRot()));
            case ENTITY_POS -> entity.position().add(anchorPointOffset.yRot(entity.getYawWithAdjustment()).xRot(entity.getXRot()));
            case MULTIPART_BOX -> {
                if (entity instanceof MultipartEntity multipartEntity) {
                    yield Arrays.stream(multipartEntity.getParts())
                            .filter(entityPart -> entityPart instanceof URDragonPart dragonPart && dragonPart.name.equals(multipartBoxName.orElseThrow()))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Couldn't find multipart box with name " + multipartBoxName.orElseThrow()))
                            .position()
                            .add(anchorPointOffset.yRot(entity.getYawWithAdjustment()).xRot(entity.getXRot()));
                } else {
                    throw new IllegalStateException("Cannot use multipart_box anchor for non MultipartEntity entities");
                }
            }
            case SHOOTING_POINT -> {
                ShooterDragon shooterDragon = (ShooterDragon) entity;
                yield new Vec3(shooterDragon.getShootingPoint().position())
                        .add(
                                anchorPointOffset
                                        .yRot(shooterDragon.getShootingPointDesiredYaw())
                                        .xRot(shooterDragon.getShootingPointDesiredPitch())
                        );
            }
        };
    }

    protected Vec3 getRot(URDragonEntity entity) {
        return switch (anchorPoint) {
            case EYES, ENTITY_POS, MULTIPART_BOX -> entity.calculateViewVector(entity.getXRot(), entity.getYawWithAdjustment());
            case SHOOTING_POINT -> {
                if (entity instanceof ShooterDragon shooterDragon) {
                    yield new Vec3(shooterDragon.getShootingPoint().rotation());
                } else {
                    throw new IllegalStateException("Cannot use shooting_point anchor for non ShooterDragon entities");
                }
            }
        };
    }

    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.SHOT_ATTACK;
    }

    @Override
    public boolean canUseUncontrolled(URDragonEntity entity) {
        return super.canUseUncontrolled(entity) && entity.getTarget() != null && entity.getLookControl().isLookingAtTarget();
    }

    protected Projectile createProjectile(ServerLevel level) {
        Entity presumablyProjectile = EntityType.loadEntityRecursive(projectileEntityType, projectileEntityNbt, level, EntitySpawnReason.TRIGGERED, (e) -> e);
        if (presumablyProjectile instanceof Projectile projectile) {
            return projectile;
        } else throw new IllegalStateException(getType().getId() + " cannot spawn non Projectile entities");
    }

    public enum AnchorPoint implements StringRepresentable {
        EYES("eyes"),
        ENTITY_POS("entity_pos"),
        MULTIPART_BOX("multipart_box"),
        SHOOTING_POINT("shooting_point")
        ;

        private final String name;

        AnchorPoint(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }
}
