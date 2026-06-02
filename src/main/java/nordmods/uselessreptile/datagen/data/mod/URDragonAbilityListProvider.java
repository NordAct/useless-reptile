package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.dragon_ability.MeleeAttackAbility;
import nordmods.uselessreptile.common.dragon_ability.ShotAttackAbility;
import nordmods.uselessreptile.common.dragon_ability.TriggerableAbility;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.data.FlyingMovementUseCondition;
import nordmods.uselessreptile.common.dragon_ability.data.MovementUseCondition;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.datagen.data.URAbstractDataProvider;

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
                                new TriggerableAbility.Data(0, 0.71f),
                                false,
                                new Vec3(0, -0.5, 1.75),
                                3, 4, false
                        ),
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.MAIN,
                                                        List.of("fly.attack"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
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
                                        ),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0, 1),
                                false,
                                new Vec3(0, -1, 1.5),
                                3.5f, 5.5f, false
                        ),
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.MAIN,
                                                        List.of("fly.attack"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
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
                                        ),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0, 1),
                                false,
                                new Vec3(0, -1, 1.5),
                                3.5f, 5.5f, false
                        ),
                        new MeleeAttackAbility(
                                new CommonDragonAbilityData(
                                        1.5f,
                                        true,
                                        List.of(
                                                new CommonDragonAbilityData.ConditionedAnimation(
                                                        URDragonEntity.AnimationController.MAIN,
                                                        List.of("fly.attack"),
                                                        true,
                                                        List.of()
                                                )
                                        ),
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
                                        ),
                                        Optional.of(URRideableDragonEntity.AttackType.SECONDARY)
                                ),
                                new TriggerableAbility.Data(0, 1),
                                false,
                                new Vec3(0, -1.5, 1.5),
                                3.5f, 4.5f, false
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
                                ShotAttackAbility.AnchorPoint.SHOOTING_POINT,
                                Optional.empty(),
                                new Vec3(0, -0.25, 1),
                                5, 3, 5
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "Dragon Abilities";
    }
}
