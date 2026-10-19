package nordmods.uselessreptile.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.gui.DragonAbilityElement;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.util.URMobCategory;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(URConfig.CONFIG, ((defaults, config, builder) -> builder
                .title(Component.translatable("config.uselessreptile.title"))
                .category(gameplayCategory())
                .category(clientCategory())
                .category(mobAttributesCategory())
                .save(ModMenuIntegration::saveAll)))
                .generateScreen(parentScreen);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::configScreen;
    }

    private static Component requiresRestart() {
        return Component.translatable("config.uselessreptile.requires_restart.@Tooltip").withStyle(ChatFormatting.RED);
    }

    private static Component spawnGroupTooltip(MobCategory spawnGroup) {
        String entries = "";
        Language language = Language.getInstance();
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE.stream().filter(entityType -> entityType.getCategory() == spawnGroup).toList()) {
            String entry = language.getOrDefault(entityType.getDescriptionId());
            entries = entries.concat(entry).concat(", ");
        }
        entries = entries.substring(0, entries.length() - 2);

        return Component.translatable("config.uselessreptile.option.categoryCapacity.@Tooltip", entries);
    }

    private static void saveAll() {
        URClientConfig.CONFIG.save();
        URConfig.CONFIG.save();
        URMobAttributesConfig.CONFIG.save();
    }

    private static ConfigCategory gameplayCategory() {
        URConfig config = URConfig.getConfig();
        URConfig defaults = URConfig.CONFIG.defaults();

        ConfigCategory.Builder gameplayCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.uselessreptile.category.gameplay"));

        //groups
        OptionGroup.Builder inWorldSpawnGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.inWorldSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.inWorldSpawn.@Tooltip")).build());
        OptionGroup.Builder spawnGroupsGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.mobCategories"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.mobCategories.@Tooltip")).build());
        OptionGroup.Builder groupSizeGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.groupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.groupSize.@Tooltip")).build());
        OptionGroup.Builder dragonBehaviourGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.dragonBehaviour"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.dragonBehaviour.@Tooltip")).build());

        //options
        Option<Boolean> naturalWyvernSpawn = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.naturalWyvernSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.naturalSpawn.@Tooltip"), requiresRestart()).build())
                .binding(defaults.naturalWyvernSpawn,
                        () -> config.naturalWyvernSpawn,
                        val -> config.naturalWyvernSpawn = val)
                .customController(BooleanController::new)
                .build();

        Option<Boolean> naturalMoleclawSpawn = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.naturalMoleclawSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.naturalSpawn.@Tooltip"), requiresRestart()).build())
                .binding(defaults.naturalMoleclawSpawn,
                        () -> config.naturalMoleclawSpawn,
                        val -> config.naturalMoleclawSpawn = val)
                .customController(BooleanController::new)
                .build();

        Option<Boolean> naturalPikehornSpawn = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.naturalRiverPikehornSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.naturalSpawn.@Tooltip"), requiresRestart()).build())
                .binding(defaults.naturalRiverPikehornSpawn,
                        () -> config.naturalRiverPikehornSpawn,
                        val -> config.naturalRiverPikehornSpawn = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> naturalLightningChaserSpawn = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.naturalLightningChaserSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.naturalLightningChaserSpawn.@Tooltip"), requiresRestart()).build())
                .binding(defaults.naturalLightningChaserSpawn,
                        () -> config.naturalLightningChaserSpawn,
                        val -> config.naturalLightningChaserSpawn = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> naturalMagmamuncherSpawn = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.naturalMagmamuncherSpawn"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.naturalSpawn.@Tooltip"), requiresRestart()).build())
                .binding(defaults.naturalMagmamuncherSpawn,
                        () -> config.naturalMagmamuncherSpawn,
                        val -> config.naturalMagmamuncherSpawn = val)
                .customController(BooleanController::new)
                .build();
        Option<Integer> lightningChaserThunderstormSpawnChance = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.lightningChaserThunderstormSpawnChance"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.lightningChaserThunderstormSpawnChance.@Tooltip")).build())
                .binding(defaults.lightningChaserThunderstormSpawnChance,
                        () -> config.lightningChaserThunderstormSpawnChance,
                        val -> config.lightningChaserThunderstormSpawnChance = val)
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build();
        Option<Integer> lightningChaserThunderstormSpawnTimerCooldown = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.lightningChaserThunderstormSpawnTimerCooldown"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.lightningChaserThunderstormSpawnTimerCooldown.@Tooltip")).build())
                .binding(defaults.lightningChaserThunderstormSpawnTimerCooldown,
                        () -> config.lightningChaserThunderstormSpawnTimerCooldown,
                        val -> config.lightningChaserThunderstormSpawnTimerCooldown = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> dragonSpawnGroupCapacity = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.dragonCategoryCapacity"))
                .description(OptionDescription.createBuilder()
                        .text(spawnGroupTooltip(URMobCategory.DRAGON.mobCategory), requiresRestart()).build())
                .binding(defaults.dragonCategoryCapacity,
                        () -> config.dragonCategoryCapacity,
                        val -> config.dragonCategoryCapacity = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();
        Option<Integer> smallDragonSpawnGroupCapacity = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.smallDragonCategoryCapacity"))
                .description(OptionDescription.createBuilder()
                        .text(spawnGroupTooltip(URMobCategory.SMALL_DRAGON.mobCategory), requiresRestart()).build())
                .binding(defaults.smallDragonCategoryCapacity,
                        () -> config.smallDragonCategoryCapacity,
                        val -> config.smallDragonCategoryCapacity = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();
        Option<Integer> undergroundDragonSpawnGroupCapacity = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.undergroundDragonCategoryCapacity"))
                .description(OptionDescription.createBuilder()
                        .text(spawnGroupTooltip(URMobCategory.UNDERGROUND_DRAGON.mobCategory), requiresRestart()).build())
                .binding(defaults.undergroundDragonCategoryCapacity,
                        () -> config.undergroundDragonCategoryCapacity,
                        val -> config.undergroundDragonCategoryCapacity = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> wyvernMinGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.wyvernMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMinGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.wyvernMinGroupSize,
                        () -> config.wyvernMinGroupSize,
                        val -> config.wyvernMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> wyvernMaxGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.wyvernMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMaxGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.wyvernMaxGroupSize,
                        () -> config.wyvernMaxGroupSize,
                        val -> config.wyvernMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> moleclawMinGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.moleclawMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMinGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.moleclawMinGroupSize,
                        () -> config.moleclawMinGroupSize,
                        val -> config.moleclawMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> moleclawMaxGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.moleclawMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMaxGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.moleclawMaxGroupSize,
                        () -> config.moleclawMaxGroupSize,
                        val -> config.moleclawMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> riverPikehornMinGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.riverPikehornMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMinGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.riverPikehornMinGroupSize,
                        () -> config.riverPikehornMinGroupSize,
                        val -> config.riverPikehornMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> riverPikehornMaxGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.riverPikehornMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMaxGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.riverPikehornMaxGroupSize,
                        () -> config.riverPikehornMaxGroupSize,
                        val -> config.riverPikehornMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserMinGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.lightningChaserMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMinGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.lightningChaserMinGroupSize,
                        () -> config.lightningChaserMinGroupSize,
                        val -> config.lightningChaserMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserMaxGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.lightningChaserMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMaxGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.lightningChaserMaxGroupSize,
                        () -> config.lightningChaserMaxGroupSize,
                        val -> config.lightningChaserMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> magmamuncherMinGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.magmamuncherMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMinGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.magmamuncherMinGroupSize,
                        () -> config.magmamuncherMinGroupSize,
                        val -> config.magmamuncherMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> magmamuncherMaxGroupSize = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.magmamuncherMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.dragonMaxGroupSize.@Tooltip"), requiresRestart()).build())
                .binding(defaults.magmamuncherMaxGroupSize,
                        () -> config.magmamuncherMaxGroupSize,
                        val -> config.magmamuncherMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<URConfig.DragonGriefing> moleclawGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.moleclawGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.moleclawGriefing,
                        () -> config.moleclawGriefing,
                        val -> config.moleclawGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<URConfig.DragonGriefing> lightningChaserGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.lightningChaserGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.lightningChaserGriefing,
                        () -> config.lightningChaserGriefing,
                        val -> config.lightningChaserGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<URConfig.DragonGriefing> magmamuncherGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.magmamuncherGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.magmamuncherGriefing,
                        () -> config.magmamuncherGriefing,
                        val -> config.magmamuncherGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<URConfig.DragonGriefing> riverPikehornGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.riverPikehornGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.riverPikehornGriefing,
                        () -> config.riverPikehornGriefing,
                        val -> config.riverPikehornGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<URConfig.DragonGriefing> wyvernGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.wyvernGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.wyvernGriefing,
                        () -> config.wyvernGriefing,
                        val -> config.wyvernGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<Integer> blockDropChance = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.blockDropChance"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.blockDropChance.@Tooltip")).build())
                .binding(defaults.blockDropChance,
                        () -> config.blockDropChance,
                        val -> config.blockDropChance = val)
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build();
        Option<Boolean> allowDragonTeleport = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.allowDragonTeleport"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.allowDragonTeleport.@Tooltip")).build())
                .binding(defaults.allowDragonTeleport,
                        () -> config.allowDragonTeleport,
                        val -> config.allowDragonTeleport = val)
                .customController(BooleanController::new)
                .build();
        Option<Float> magmamuncherFireResistanceTimeMultiplier = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.magmamuncherFireResistanceTimeMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.magmamuncherFireResistanceTimeMultiplier.@Tooltip")).build())
                .binding(defaults.magmamuncherFireResistanceTimeMultiplier,
                        () -> config.magmamuncherFireResistanceTimeMultiplier,
                        val -> config.magmamuncherFireResistanceTimeMultiplier = val)
                .customController(opt -> new FloatFieldController(opt, 0, Float.MAX_VALUE))
                .build();

        inWorldSpawnGroup.option(naturalWyvernSpawn);
        inWorldSpawnGroup.option(naturalMoleclawSpawn);
        inWorldSpawnGroup.option(naturalPikehornSpawn);
        inWorldSpawnGroup.option(naturalLightningChaserSpawn);
        inWorldSpawnGroup.option(naturalMagmamuncherSpawn);
        inWorldSpawnGroup.option(lightningChaserThunderstormSpawnChance);
        inWorldSpawnGroup.option(lightningChaserThunderstormSpawnTimerCooldown);

        spawnGroupsGroup.option(dragonSpawnGroupCapacity);
        spawnGroupsGroup.option(undergroundDragonSpawnGroupCapacity);
        spawnGroupsGroup.option(smallDragonSpawnGroupCapacity);

        groupSizeGroup.option(wyvernMinGroupSize);
        groupSizeGroup.option(wyvernMaxGroupSize);
        groupSizeGroup.option(moleclawMinGroupSize);
        groupSizeGroup.option(moleclawMaxGroupSize);
        groupSizeGroup.option(riverPikehornMinGroupSize);
        groupSizeGroup.option(riverPikehornMaxGroupSize);
        groupSizeGroup.option(lightningChaserMinGroupSize);
        groupSizeGroup.option(lightningChaserMaxGroupSize);
        groupSizeGroup.option(magmamuncherMinGroupSize);
        groupSizeGroup.option(magmamuncherMaxGroupSize);

        dragonBehaviourGroup.option(moleclawGriefing);
        dragonBehaviourGroup.option(lightningChaserGriefing);
        dragonBehaviourGroup.option(magmamuncherGriefing);
        dragonBehaviourGroup.option(riverPikehornGriefing);
        dragonBehaviourGroup.option(wyvernGriefing);
        dragonBehaviourGroup.option(blockDropChance);
        dragonBehaviourGroup.option(allowDragonTeleport);
        dragonBehaviourGroup.option(magmamuncherFireResistanceTimeMultiplier);

        gameplayCategory.group(inWorldSpawnGroup.build());
        gameplayCategory.group(spawnGroupsGroup.build());
        gameplayCategory.group(groupSizeGroup.build());
        gameplayCategory.group(dragonBehaviourGroup.build());

        return gameplayCategory.build();
    }

    private static ConfigCategory clientCategory() {
        URClientConfig clientConfig = URClientConfig.getConfig();
        URClientConfig clientDefaults = URClientConfig.CONFIG.defaults();

        //category
        ConfigCategory.Builder clientCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.uselessreptile.category.client"));

        //group
        OptionGroup.Builder cameraGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.camera"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.camera.@Tooltip")).build());
        OptionGroup.Builder dragonAppearanceGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.dragonAppearance"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.dragonAppearance.@Tooltip")).build());
        OptionGroup.Builder guiGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.gui"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.gui.@Tooltip")).build());


        Option<Float> cameraDistanceOffset = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.cameraDistanceOffset"))
                .binding(clientDefaults.cameraDistanceOffset,
                        () -> clientConfig.cameraDistanceOffset,
                        val -> clientConfig.cameraDistanceOffset = val)
                .customController(opt -> new FloatSliderController(opt, -5, 5, 0.05f))
                .build();
        Option<Float> cameraVerticalOffset = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.cameraVerticalOffset"))
                .binding(clientDefaults.cameraVerticalOffset,
                        () -> clientConfig.cameraVerticalOffset,
                        val -> clientConfig.cameraVerticalOffset = val)
                .customController(opt -> new FloatSliderController(opt, -5, 5, 0.05f))
                .build();
        Option<Float> cameraHorizontalOffset = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.cameraHorizontalOffset"))
                .binding(clientDefaults.cameraHorizontalOffset,
                        () -> clientConfig.cameraHorizontalOffset,
                        val -> clientConfig.cameraHorizontalOffset = val)
                .customController(opt -> new FloatSliderController(opt, -5, 5, 0.05f))
                .build();
        Option<Boolean> enableCameraOffset = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.enableCameraOffset"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.enableCameraOffset.@Tooltip")).build())
                .binding(clientDefaults.enableCameraOffset,
                        () -> clientConfig.enableCameraOffset,
                        val -> clientConfig.enableCameraOffset = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> enableCameraRoll = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.enableCameraRoll"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.enableCameraRoll.@Tooltip")).build())
                .binding(clientDefaults.enableCameraRoll,
                        () -> clientConfig.enableCameraRoll,
                        val -> clientConfig.enableCameraRoll = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> enableCrosshair = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.enableCrosshair"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.enableCrosshair.@Tooltip")).build())
                .binding(clientConfig.enableCrosshair,
                        () -> clientConfig.enableCrosshair,
                        val -> clientConfig.enableCrosshair = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> autoThirdPerson = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.autoThirdPerson"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.autoThirdPerson.@Tooltip")).build())
                .binding(clientDefaults.autoThirdPerson,
                        () -> clientConfig.autoThirdPerson,
                        val -> clientConfig.autoThirdPerson = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> upDownCameraControl = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.upDownCameraControl"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.upDownCameraControl.@Tooltip")).build())
                .binding(clientDefaults.upDownCameraControl,
                        () -> clientConfig.upDownCameraControl,
                        val -> clientConfig.upDownCameraControl = val)
                .customController(BooleanController::new)
                .build();
        Option<Float> upDownCameraPitchThreshold = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.upDownCameraPitchThreshold"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.upDownCameraPitchThreshold.@Tooltip")).build())
                .binding(clientDefaults.upDownCameraPitchThreshold,
                        () -> clientConfig.upDownCameraPitchThreshold,
                        val -> clientConfig.upDownCameraPitchThreshold = val)
                .customController(opt -> new FloatSliderController(opt, 1, 89, 1))
                .build();

        Option<Boolean> disableNamedTextures = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.disableNamedEntityModels"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.disableNamedEntityModels.@Tooltip")).build())
                .binding(clientDefaults.disableNamedEntityModels,
                        () -> clientConfig.disableNamedEntityModels,
                        val -> clientConfig.disableNamedEntityModels = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> disableEmissiveTextures = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.disableEmissiveTextures"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.disableEmissiveTextures.@Tooltip")).build())
                .binding(clientDefaults.disableEmissiveTextures,
                        () -> clientConfig.disableEmissiveTextures,
                        val -> clientConfig.disableEmissiveTextures = val)
                .customController(BooleanController::new)
                .build();
        Option<Boolean> hideEquipmentInfo = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.hideEquipmentInfo"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.hideEquipmentInfo.@Tooltip")).build())
                .binding(clientDefaults.hideEquipmentInfo,
                        () -> clientConfig.hideEquipmentInfo,
                        val -> clientConfig.hideEquipmentInfo = val)
                .customController(BooleanController::new)
                .build();

        Option<Boolean> attackBoxesInDebug = Option.<Boolean>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.attackBoxesInDebug"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.attackBoxesInDebug.@Tooltip")).build())
                .binding(clientDefaults.attackBoxesInDebug,
                        () -> clientConfig.attackBoxesInDebug,
                        val -> clientConfig.attackBoxesInDebug = val)
                .customController(BooleanController::new)
                .build();
        Option<URClientConfig.PassengerVisibility> renderPassengers = Option.<URClientConfig.PassengerVisibility>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.renderPassengers"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.renderPassengers.@Tooltip")).build())
                .binding(clientDefaults.renderPassengers,
                        () -> clientConfig.renderPassengers,
                        val -> clientConfig.renderPassengers = val)
                .customController(opt -> new EnumController<>(opt, URClientConfig.PassengerVisibility.class))
                .build();

        Option<Integer> primaryAbilityIconOffsetX = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.primaryAbilityIconOffsetX"))
                .binding(clientDefaults.primaryAbilityIconOffsetX,
                        () -> clientConfig.primaryAbilityIconOffsetX,
                        val -> clientConfig.primaryAbilityIconOffsetX = val)
                .customController(IntegerFieldController::new)
                .build();
        Option<Integer> primaryAbilityIconOffsetY = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.primaryAbilityIconOffsetY"))
                .binding(clientDefaults.primaryAbilityIconOffsetY,
                        () -> clientConfig.primaryAbilityIconOffsetY,
                        val -> clientConfig.primaryAbilityIconOffsetY = val)
                .customController(IntegerFieldController::new)
                .build();
        Option<Integer> secondaryAbilityIconOffsetX = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.secondaryAbilityIconOffsetX"))
                .binding(clientDefaults.secondaryAbilityIconOffsetX,
                        () -> clientConfig.secondaryAbilityIconOffsetX,
                        val -> clientConfig.secondaryAbilityIconOffsetX = val)
                .customController(IntegerFieldController::new)
                .build();
        Option<Integer> secondaryAbilityIconOffsetY = Option.<Integer>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.secondaryAbilityIconOffsetY"))
                .binding(clientDefaults.secondaryAbilityIconOffsetY,
                        () -> clientConfig.secondaryAbilityIconOffsetY,
                        val -> clientConfig.secondaryAbilityIconOffsetY = val)
                .customController(IntegerFieldController::new)
                .build();
        Option<DragonAbilityElement.AbilityIconOffsetPreset> abilityIconOffsetsPreset = Option.<DragonAbilityElement.AbilityIconOffsetPreset>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.abilityIconOffsetsPreset"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.primaryAbilityIconOffsetX.@Tooltip")).build())
                .binding(clientDefaults.abilityIconOffsetsPreset,
                        () -> clientConfig.abilityIconOffsetsPreset,
                        val -> clientConfig.abilityIconOffsetsPreset = val)
                .customController(opt -> new EnumController<>(opt, DragonAbilityElement.AbilityIconOffsetPreset.class))
                .addListener((option, _) -> {
                    boolean custom = option.pendingValue() == DragonAbilityElement.AbilityIconOffsetPreset.CUSTOM;
                    primaryAbilityIconOffsetX.setAvailable(custom);
                    primaryAbilityIconOffsetY.setAvailable(custom);
                    secondaryAbilityIconOffsetX.setAvailable(custom);
                    secondaryAbilityIconOffsetY.setAvailable(custom);
                })
                .build();

        cameraGroup.option(cameraDistanceOffset);
        cameraGroup.option(cameraVerticalOffset);
        cameraGroup.option(cameraHorizontalOffset);
        cameraGroup.option(enableCameraOffset);
        cameraGroup.option(enableCameraRoll);
        cameraGroup.option(enableCrosshair);
        cameraGroup.option(autoThirdPerson);
        cameraGroup.option(upDownCameraControl);
        cameraGroup.option(upDownCameraPitchThreshold);

        dragonAppearanceGroup.option(disableNamedTextures);
        dragonAppearanceGroup.option(disableEmissiveTextures);
        dragonAppearanceGroup.option(hideEquipmentInfo);
        dragonAppearanceGroup.option(renderPassengers);
        dragonAppearanceGroup.option(attackBoxesInDebug);

        guiGroup.option(abilityIconOffsetsPreset);
        guiGroup.option(primaryAbilityIconOffsetX);
        guiGroup.option(primaryAbilityIconOffsetY);
        guiGroup.option(secondaryAbilityIconOffsetX);
        guiGroup.option(secondaryAbilityIconOffsetY);

        clientCategory.group(cameraGroup.build());
        clientCategory.group(dragonAppearanceGroup.build());
        clientCategory.group(guiGroup.build());

        return clientCategory.build();
    }

    private static ConfigCategory mobAttributesCategory() {
        URMobAttributesConfig config = URMobAttributesConfig.getConfig();
        URMobAttributesConfig defaults = URMobAttributesConfig.CONFIG.defaults();

        ConfigCategory.Builder mobAttributesCategory = ConfigCategory.createBuilder()
                .name(Component.translatable("config.uselessreptile.category.mobAttributes"));

        OptionGroup.Builder globalMultipliersGroup = OptionGroup.createBuilder()
                .name(Component.translatable("config.uselessreptile.group.globalMultipliers"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.group.globalMultipliers.@Tooltip")).build());

        Option<Float> riddenDragonGroundSpeedMultiplier = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.riddenDragonGroundSpeedMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.riddenDragonGroundSpeedMultiplier.@Tooltip")).build())
                .binding(defaults.riddenDragonGroundSpeedMultiplier,
                        () -> config.riddenDragonGroundSpeedMultiplier,
                        val -> config.riddenDragonGroundSpeedMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> riddenDragonFlyingSpeedMultiplier = Option.<Float>createBuilder()
                .name(Component.translatable("config.uselessreptile.option.riddenDragonFlyingSpeedMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("config.uselessreptile.option.riddenDragonFlyingSpeedMultiplier.@Tooltip")).build())
                .binding(defaults.riddenDragonFlyingSpeedMultiplier,
                        () -> config.riddenDragonFlyingSpeedMultiplier,
                        val -> config.riddenDragonFlyingSpeedMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        globalMultipliersGroup.option(riddenDragonGroundSpeedMultiplier);
        globalMultipliersGroup.option(riddenDragonFlyingSpeedMultiplier);
        mobAttributesCategory.group(globalMultipliersGroup.build());

        return mobAttributesCategory.build();
    }
}
