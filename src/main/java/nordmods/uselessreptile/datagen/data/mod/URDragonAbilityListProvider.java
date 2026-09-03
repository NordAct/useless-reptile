package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.*;
import nordmods.uselessreptile.common.dragon_ability.data.*;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.datagen.data.URAbstractDataProvider;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonAbilityListProvider extends URAbstractDataProvider<List<DragonAbility>> {
    public URDragonAbilityListProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, DragonAbility.CODEC.listOf(), "uselessreptile/abilities");
    }

    @Override
    public void addEntries(HolderLookup.Provider provider) {
        addEntry(
                UselessReptile.id("wyvern"),
                List.of(
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                       1.5f,
                                       true,
                                       List.of(
                                               new CommonDragonAbilityData.ConditionedAnimation(
                                                       URDragonEntity.AnimationController.ATTACK,
                                                       List.of("attack.melee1", "attack.melee2", "attack.melee3"),
                                                       true,
                                                       List.of(
                                                               new FlyingMovementUseCondition(
                                                                       Optional.of(false),
                                                                       Optional.empty(),
                                                                       Optional.empty()
                                                               )
                                                       )
                                               ),
                                               new CommonDragonAbilityData.ConditionedAnimation(
                                                       URDragonEntity.AnimationController.MAIN,
                                                       List.of("fly.attack"),
                                                       true,
                                                       List.of(
                                                               new FlyingMovementUseCondition(
                                                                       Optional.of(true),
                                                                       Optional.empty(),
                                                                       Optional.empty()
                                                               )
                                                       )
                                               )
                                       ),
                                        List.of(),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0, 0.71f),
                                false,
                                List.of(),
                                false,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3, 4,
                                                new Vec3(0, -0.5, 1.75),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of(
                                                        new FlyingMovementUseCondition(
                                                                Optional.of(false),
                                                                Optional.empty(),
                                                                Optional.empty()
                                                        )
                                                )
                                        ),
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3.5f, 5.5f,
                                                new Vec3(0, -1, 1.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of(
                                                        new FlyingMovementUseCondition(
                                                                Optional.of(true),
                                                                Optional.empty(),
                                                                Optional.empty()
                                                        ),
                                                        new MovementUseCondition(
                                                                Optional.of(false),
                                                                Optional.empty(),
                                                                Optional.empty()
                                                        )
                                                )
                                        ),
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3.5f, 5.5f,
                                                new Vec3(0, -1, 1.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of(
                                                        new FlyingMovementUseCondition(
                                                                Optional.of(true),
                                                                Optional.empty(),
                                                                Optional.empty()
                                                        ),
                                                        new MovementUseCondition(
                                                                Optional.empty(),
                                                                Optional.of(true),
                                                                Optional.empty()
                                                        )
                                                )
                                        ),
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3.5f, 4.5f,
                                                new Vec3(0, -1, 1.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of(
                                                        new FlyingMovementUseCondition(
                                                                Optional.of(true),
                                                                Optional.empty(),
                                                                Optional.empty()
                                                        ),
                                                        new MovementUseCondition(
                                                                Optional.of(true),
                                                                Optional.of(false),
                                                                Optional.empty()
                                                        )
                                                )
                                        )
                                )
                        ),
                        new ShotAttackAbility(
                                new CommonDragonAbilityData(
                                        4f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.fly.range"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.of(false),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.of(false),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.empty(),
                                                                        Optional.of(true),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(false),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                )
                                        ),
                                        List.of(),
                                        Optional.of(URRideableDragonEntity.AttackType.PRIMARY)
                                ),
                                new TriggerableAbility.Data(0, 0.7f),
                                UREntities.ACID_BLAST,
                                new CompoundTag(),
                                ShotAttackAbility.AnchorPoint.MULTIPART_BOX,
                                Optional.of("head"),
                                new Vec3(0, -0.25, 1),
                                5, 3, 5
                        )
                )
        );
        addEntry(
                UselessReptile.id("moleclaw"),
                List.of(
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.75f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.normal1", "attack.normal2"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
                                        List.of(),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0.5f, 1.6f),
                                true,
                                List.of(),
                                false,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3, 3.5f,
                                                new Vec3(0, -0.25f, 2),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of()
                                        )
                                )
                        ),
                        new BlockBreakingMeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        3f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.strong"),
                                                        true,
                                                        List.of(new MoleclawUseCondition(false))
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.strong.panic"),
                                                        true,
                                                        List.of(new MoleclawUseCondition(true))
                                                )
                                        ),
                                        List.of(),
                                        Optional.of(URRideableDragonEntity.AttackType.PRIMARY)
                                ),
                                new TriggerableAbility.Data(0.5f, 1.6f),
                                true,
                                List.of(),
                                false,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                2.5f, 3.5f,
                                                new Vec3(0, 0, 1.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.SMOOTH,
                                                List.of(new RideableUseCondition(false))
                                        ),
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                2.5f, 4.25f,
                                                new Vec3(0, 0.25, 1.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.SNAPPED,
                                                List.of(new RideableUseCondition(true))
                                        )
                                )
                        )
                )
        );
        addEntry(
                UselessReptile.id("river_pikehorn"),
                List.of(
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack1", "attack2", "attack3"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
                                        List.of(),
                                        Optional.empty()
                                ),
                                new TriggerableAbility.Data(0, 0.58f),
                                false,
                                List.of(),
                                false,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                1.8f, 0.7f,
                                                Vec3.ZERO,
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of()
                                        )
                                )
                        )
                )
        );
        addEntry(
                UselessReptile.id("magmamuncher"),
                List.of(
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack1", "attack2", "attack3"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
                                        List.of(),
                                        Optional.empty()
                                ),
                                new TriggerableAbility.Data(0, 0.75f),
                                false,
                                List.of(),
                                true,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                1.7f, 0.35f,
                                                Vec3.ZERO,
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of()
                                        )
                                )
                        )
                )
        );
        addEntry(
                UselessReptile.id("lightning_chaser"),
                List.of(
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.melee1", "attack.melee2", "attack.melee3"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
                                        List.of(
                                                new FlyingMovementUseCondition(
                                                        Optional.of(false),
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        ),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0, 1),
                                false,
                                List.of(),
                                false,
                                List.of(
                                        new MeleeAttackAbility.ConditionedAttackBox(
                                                3, 3,
                                                new Vec3(0, -0.1, 2.5),
                                                MeleeAttackAbility.ConditionedAttackBox.VerticalAttackBoxMovement.NONE,
                                                List.of()
                                        )
                                )
                        ),
                        new LightningBreathAttackAbility(
                                new CommonDragonAbilityData(
                                        5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(false),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range.fly"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.of(false),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range.fly.idle"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.of(true),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                ),
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.ATTACK,
                                                        List.of("attack.range.fly.idle"),
                                                        true,
                                                        List.of(
                                                                new FlyingMovementUseCondition(
                                                                        Optional.of(true),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                ),
                                                                new MovementUseCondition(
                                                                        Optional.of(false),
                                                                        Optional.empty(),
                                                                        Optional.empty()
                                                                )
                                                        )
                                                )
                                        ),
                                        List.of(),
                                        Optional.of(URRideableDragonEntity.AttackType.PRIMARY)
                                ),
                                new TriggerableAbility.Data(0.35f, 1),
                                ShotAttackAbility.AnchorPoint.MULTIPART_BOX,
                                Optional.of("head"),
                                Vec3.ZERO,
                                1,
                                0x00FFFFFF,
                                50,
                                10
                        ),
                        new ShockwaveAttackAbility(
                                new CommonDragonAbilityData(
                                        20f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.MAIN,
                                                        List.of("fly.shockwave"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
                                        List.of(
                                                new FlyingMovementUseCondition(
                                                        Optional.of(true),
                                                        Optional.empty(),
                                                        Optional.empty()
                                                )
                                        ),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0.5f, 1.13f),
                                new CompoundTag(),
                                40, 0.8f,
                                1, 1,
                                0x00FFFFFF
                        )
                )
        );
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Abilities";
    }
}
