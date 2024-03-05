package nordmods.uselessreptile.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(URConfig.CONFIG, ((defaults, config, builder) -> builder
                .title(key("title"))
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

    private static Text key(String id) {
        return Text.translatable("config.uselessreptile." + id);
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
                .name(key("category.gameplay"));

        //groups
        OptionGroup.Builder spawnWeightGroup = OptionGroup.createBuilder()
                .name(key("group.spawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.spawnWeight.@Tooltip")).build());
        OptionGroup.Builder groupSizeGroup = OptionGroup.createBuilder()
                .name(key("group.groupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.groupSize.@Tooltip")).build());
        OptionGroup.Builder dragonBehaviourGroup = OptionGroup.createBuilder()
                .name(key("group.dragonBehaviour"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.dragonBehaviour.@Tooltip")).build());

        //options
        Option<Integer> wyvernSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.wyvernSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonSpawnWeight.@Tooltip")).build())
                .binding(defaults.wyvernSpawnWeight,
                        () -> config.wyvernSpawnWeight,
                        val -> config.wyvernSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> moleclawSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.moleclawSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonSpawnWeight.@Tooltip")).build())
                .binding(defaults.moleclawSpawnWeight,
                        () -> config.moleclawSpawnWeight,
                        val -> config.moleclawSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> pikehornSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.pikehornSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonSpawnWeight.@Tooltip")).build())
                .binding(defaults.pikehornSpawnWeight,
                        () -> config.pikehornSpawnWeight,
                        val -> config.pikehornSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonSpawnWeight.@Tooltip")).build())
                .binding(defaults.lightningChaserSpawnWeight,
                        () -> config.lightningChaserSpawnWeight,
                        val -> config.lightningChaserSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserThunderstormSpawnChance = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserThunderstormSpawnChance"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.lightningChaserThunderstormSpawnChance.@Tooltip")).build())
                .binding(defaults.lightningChaserThunderstormSpawnChance,
                        () -> config.lightningChaserThunderstormSpawnChance,
                        val -> config.lightningChaserThunderstormSpawnChance = val)
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build();

        Option<Integer> wyvernMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.wyvernMinGroupSize"))
                .binding(defaults.wyvernMinGroupSize,
                        () -> config.wyvernMinGroupSize,
                        val -> config.wyvernMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> wyvernMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.wyvernMaxGroupSize"))
                .binding(defaults.wyvernMaxGroupSize,
                        () -> config.wyvernMaxGroupSize,
                        val -> config.wyvernMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> moleclawMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.moleclawMinGroupSize"))
                .binding(defaults.moleclawMinGroupSize,
                        () -> config.moleclawMinGroupSize,
                        val -> config.moleclawMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> moleclawMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.moleclawMaxGroupSize"))
                .binding(defaults.moleclawMaxGroupSize,
                        () -> config.moleclawMaxGroupSize,
                        val -> config.moleclawMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> pikehornMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.pikehornMinGroupSize"))
                .binding(defaults.pikehornMinGroupSize,
                        () -> config.pikehornMinGroupSize,
                        val -> config.pikehornMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> pikehornMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.pikehornMaxGroupSize"))
                .binding(defaults.pikehornMaxGroupSize,
                        () -> config.pikehornMaxGroupSize,
                        val -> config.pikehornMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserMinGroupSize"))
                .binding(defaults.lightningChaserMinGroupSize,
                        () -> config.lightningChaserMinGroupSize,
                        val -> config.lightningChaserMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserMaxGroupSize"))
                .binding(defaults.lightningChaserMaxGroupSize,
                        () -> config.lightningChaserMaxGroupSize,
                        val -> config.lightningChaserMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<URConfig.DragonGriefing> allowDragonGriefing = Option.<URConfig.DragonGriefing>createBuilder()
                .name(key("option.allowDragonGriefing"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.allowDragonGriefing.@Tooltip")).build())
                .binding(defaults.allowDragonGriefing,
                        () -> config.allowDragonGriefing,
                        val -> config.allowDragonGriefing = val)
                .customController(opt -> new EnumController<>(opt, URConfig.DragonGriefing.class))
                .build();
        Option<Integer> blockDropChance = Option.<Integer>createBuilder()
                .name(key("option.blockDropChance"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.blockDropChance.@Tooltip")).build())
                .binding(defaults.blockDropChance,
                        () -> config.blockDropChance,
                        val -> config.blockDropChance = val)
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build();

        spawnWeightGroup.option(wyvernSpawnWeight);
        spawnWeightGroup.option(moleclawSpawnWeight);
        spawnWeightGroup.option(pikehornSpawnWeight);
        spawnWeightGroup.option(lightningChaserSpawnWeight);
        spawnWeightGroup.option(lightningChaserThunderstormSpawnChance);

        groupSizeGroup.option(wyvernMinGroupSize);
        groupSizeGroup.option(wyvernMaxGroupSize);
        groupSizeGroup.option(moleclawMinGroupSize);
        groupSizeGroup.option(moleclawMaxGroupSize);
        groupSizeGroup.option(pikehornMinGroupSize);
        groupSizeGroup.option(pikehornMaxGroupSize);
        groupSizeGroup.option(lightningChaserMinGroupSize);
        groupSizeGroup.option(lightningChaserMaxGroupSize);

        dragonBehaviourGroup.option(allowDragonGriefing);
        dragonBehaviourGroup.option(blockDropChance);

        gameplayCategory.group(spawnWeightGroup.build());
        gameplayCategory.group(groupSizeGroup.build());
        gameplayCategory.group(dragonBehaviourGroup.build());


        return gameplayCategory.build();
    }

    private static ConfigCategory clientCategory() {
        URClientConfig clientConfig = URClientConfig.getConfig();
        URClientConfig clientDefaults = URClientConfig.CONFIG.defaults();

        //category
        ConfigCategory.Builder clientCategory = ConfigCategory.createBuilder()
                .name(key("category.client"));

        //group
        OptionGroup.Builder cameraGroup = OptionGroup.createBuilder()
                .name(key("group.camera"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.camera.@Tooltip")).build());
        OptionGroup.Builder dragonAppearanceGroup = OptionGroup.createBuilder()
                .name(key("group.dragonAppearance"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.dragonAppearance.@Tooltip")).build());


        Option<Double> cameraDistanceOffset = Option.<Double>createBuilder()
                .name(key("option.cameraDistanceOffset"))
                .binding(clientDefaults.cameraDistanceOffset,
                        () -> clientConfig.cameraDistanceOffset,
                        val -> clientConfig.cameraDistanceOffset = val)
                .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                .build();
        Option<Double> cameraVerticalOffset = Option.<Double>createBuilder()
                .name(key("option.cameraVerticalOffset"))
                .binding(clientDefaults.cameraVerticalOffset,
                        () -> clientConfig.cameraVerticalOffset,
                        val -> clientConfig.cameraVerticalOffset = val)
                .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                .build();
        Option<Double> cameraHorizontalOffset = Option.<Double>createBuilder()
                .name(key("option.cameraHorizontalOffset"))
                .binding(clientDefaults.cameraHorizontalOffset,
                        () -> clientConfig.cameraHorizontalOffset,
                        val -> clientConfig.cameraHorizontalOffset = val)
                .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                .build();
        Option<Boolean> enableCameraOffset = Option.<Boolean>createBuilder()
                .name(key("option.enableCameraOffset"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.enableCameraOffset.@Tooltip")).build())
                .binding(clientDefaults.enableCameraOffset,
                        () -> clientConfig.enableCameraOffset,
                        val -> clientConfig.enableCameraOffset = val)
                .customController(TickBoxController::new)
                .build();
        Option<Boolean> enableCrosshair = Option.<Boolean>createBuilder()
                .name(key("option.enableCrosshair"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.enableCrosshair.@Tooltip")).build())
                .binding(clientConfig.enableCrosshair,
                        () -> clientConfig.enableCrosshair,
                        val -> clientConfig.enableCrosshair = val)
                .customController(TickBoxController::new)
                .build();
        Option<Boolean> autoThirdPerson = Option.<Boolean>createBuilder()
                .name(key("option.autoThirdPerson"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.autoThirdPerson.@Tooltip")).build())
                .binding(clientDefaults.autoThirdPerson,
                        () -> clientConfig.autoThirdPerson,
                        val -> clientConfig.autoThirdPerson = val)
                .customController(TickBoxController::new)
                .build();

        Option<Boolean> disableNamedTextures = Option.<Boolean>createBuilder()
                .name(key("option.disableNamedEntityModels"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.disableNamedEntityModels.@Tooltip")).build())
                .binding(clientDefaults.disableNamedEntityModels,
                        () -> clientConfig.disableNamedEntityModels,
                        val -> clientConfig.disableNamedEntityModels = val)
                .customController(TickBoxController::new)
                .build();
        Option<Boolean> disableEmissiveTextures = Option.<Boolean>createBuilder()
                .name(key("option.disableEmissiveTextures"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.disableEmissiveTextures.@Tooltip")).build())
                .binding(clientDefaults.disableEmissiveTextures,
                        () -> clientConfig.disableEmissiveTextures,
                        val -> clientConfig.disableEmissiveTextures = val)
                .customController(TickBoxController::new)
                .build();
        Option<Boolean> attackBoxesInDebug = Option.<Boolean>createBuilder()
                .name(key("option.attackBoxesInDebug"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.attackBoxesInDebug.@Tooltip")).build())
                .binding(clientDefaults.attackBoxesInDebug,
                        () -> clientConfig.attackBoxesInDebug,
                        val -> clientConfig.attackBoxesInDebug = val)
                .customController(TickBoxController::new)
                .build();

        cameraGroup.option(cameraDistanceOffset);
        cameraGroup.option(cameraVerticalOffset);
        cameraGroup.option(cameraHorizontalOffset);
        cameraGroup.option(enableCameraOffset);
        cameraGroup.option(enableCrosshair);
        cameraGroup.option(autoThirdPerson);

        dragonAppearanceGroup.option(disableNamedTextures);
        dragonAppearanceGroup.option(disableEmissiveTextures);
        dragonAppearanceGroup.option(attackBoxesInDebug);

        clientCategory.group(cameraGroup.build());
        clientCategory.group(dragonAppearanceGroup.build());

        return clientCategory.build();
    }

    private static ConfigCategory mobAttributesCategory() {
        URMobAttributesConfig config = URMobAttributesConfig.getConfig();
        URMobAttributesConfig defaults = URMobAttributesConfig.CONFIG.defaults();

        ConfigCategory.Builder mobAttributesCategory = ConfigCategory.createBuilder()
                .name(key("category.mobAttributes"));

        OptionGroup.Builder globalMultipliersGroup = OptionGroup.createBuilder()
                .name(key("group.globalMultipliers"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.globalMultipliers.@Tooltip")).build());

        Option<Float> dragonDamageMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonDamageMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonDamageMultiplier.@Tooltip")).build())
                .binding(defaults.dragonDamageMultiplier,
                        () -> config.dragonDamageMultiplier,
                        val -> config.dragonDamageMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonKnockbackMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonKnockbackMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonKnockbackMultiplier.@Tooltip")).build())
                .binding(defaults.dragonKnockbackMultiplier,
                        () -> config.dragonKnockbackMultiplier,
                        val -> config.dragonKnockbackMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonHealthMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonHealthMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonHealthMultiplier.@Tooltip")).build())
                .binding(defaults.dragonHealthMultiplier,
                        () -> config.dragonHealthMultiplier,
                        val -> config.dragonHealthMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonArmorMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonArmorMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonArmorMultiplier.@Tooltip")).build())
                .binding(defaults.dragonArmorMultiplier,
                        () -> config.dragonArmorMultiplier,
                        val -> config.dragonArmorMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonArmorToughnessMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonArmorToughnessMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonArmorToughnessMultiplier.@Tooltip")).build())
                .binding(defaults.dragonArmorToughnessMultiplier,
                        () -> config.dragonArmorToughnessMultiplier,
                        val -> config.dragonArmorToughnessMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonGroundSpeedMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonGroundSpeedMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonGroundSpeedMultiplier.@Tooltip")).build())
                .binding(defaults.dragonGroundSpeedMultiplier,
                        () -> config.dragonGroundSpeedMultiplier,
                        val -> config.dragonGroundSpeedMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        Option<Float> dragonFlyingSpeedMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonFlyingSpeedMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonFlyingSpeedMultiplier.@Tooltip")).build())
                .binding(defaults.dragonFlyingSpeedMultiplier,
                        () -> config.dragonFlyingSpeedMultiplier,
                        val -> config.dragonFlyingSpeedMultiplier = val)
                .customController(FloatFieldController::new)
                .build();

        globalMultipliersGroup.option(dragonDamageMultiplier);
        globalMultipliersGroup.option(dragonKnockbackMultiplier);
        globalMultipliersGroup.option(dragonHealthMultiplier);
        globalMultipliersGroup.option(dragonArmorMultiplier);
        globalMultipliersGroup.option(dragonArmorToughnessMultiplier);
        globalMultipliersGroup.option(dragonGroundSpeedMultiplier);
        globalMultipliersGroup.option(dragonFlyingSpeedMultiplier);
        mobAttributesCategory.group(globalMultipliersGroup.build());

        addWyvernAttributesGroup(mobAttributesCategory, config, defaults);
        addMoleclawAttributesGroup(mobAttributesCategory, config, defaults);
        addPikehornAttributesGroup(mobAttributesCategory, config, defaults);
        addLightningChaserAttributesGroup(mobAttributesCategory, config, defaults);

        return mobAttributesCategory.build();
    }

    private static void addWyvernAttributesGroup(ConfigCategory.Builder category, URMobAttributesConfig config, URMobAttributesConfig defaults) {
        OptionGroup.Builder wyvernAttributesGroup = OptionGroup.createBuilder()
                .name(key("group.wyvernAttributes"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.wyvernAttributes.@Tooltip")).build());

        Option<Float> wyvernDamage = Option.<Float>createBuilder()
                .name(key("option.dragonDamage"))
                .binding(defaults.wyvernDamage,
                        () -> config.wyvernDamage,
                        val -> config.wyvernDamage = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernKnockback = Option.<Float>createBuilder()
                .name(key("option.dragonKnockback"))
                .binding(defaults.wyvernKnockback,
                        () -> config.wyvernKnockback,
                        val -> config.wyvernKnockback = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernHealth = Option.<Float>createBuilder()
                .name(key("option.dragonHealth"))
                .binding(defaults.wyvernHealth,
                        () -> config.wyvernHealth,
                        val -> config.wyvernHealth = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernArmor = Option.<Float>createBuilder()
                .name(key("option.dragonArmor"))
                .binding(defaults.wyvernArmor,
                        () -> config.wyvernArmor,
                        val -> config.wyvernArmor = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernArmorToughness = Option.<Float>createBuilder()
                .name(key("option.dragonArmorToughness"))
                .binding(defaults.wyvernArmorToughness,
                        () -> config.wyvernArmorToughness,
                        val -> config.wyvernArmorToughness = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernGroundSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonGroundSpeed"))
                .binding(defaults.wyvernGroundSpeed,
                        () -> config.wyvernGroundSpeed,
                        val -> config.wyvernGroundSpeed = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> wyvernFlyingSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonFlyingSpeed"))
                .binding(defaults.wyvernFlyingSpeed,
                        () -> config.wyvernFlyingSpeed,
                        val -> config.wyvernFlyingSpeed = val)
                .customController(FloatFieldController::new)
                .build();

        wyvernAttributesGroup.option(wyvernDamage);
        wyvernAttributesGroup.option(wyvernKnockback);
        wyvernAttributesGroup.option(wyvernHealth);
        wyvernAttributesGroup.option(wyvernArmor);
        wyvernAttributesGroup.option(wyvernArmorToughness);
        wyvernAttributesGroup.option(wyvernGroundSpeed);
        wyvernAttributesGroup.option(wyvernFlyingSpeed);
        category.group(wyvernAttributesGroup.build());
    }

    private static void addMoleclawAttributesGroup(ConfigCategory.Builder category, URMobAttributesConfig config, URMobAttributesConfig defaults) {
        OptionGroup.Builder moleclawAttributesGroup = OptionGroup.createBuilder()
                .name(key("group.moleclawAttributes"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.moleclawAttributes.@Tooltip")).build());

        Option<Float> moleclawDamage = Option.<Float>createBuilder()
                .name(key("option.dragonDamage"))
                .binding(defaults.moleclawDamage,
                        () -> config.moleclawDamage,
                        val -> config.moleclawDamage = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> moleclawKnockback = Option.<Float>createBuilder()
                .name(key("option.dragonKnockback"))
                .binding(defaults.moleclawKnockback,
                        () -> config.moleclawKnockback,
                        val -> config.moleclawKnockback = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> moleclawHealth = Option.<Float>createBuilder()
                .name(key("option.dragonHealth"))
                .binding(defaults.moleclawHealth,
                        () -> config.moleclawHealth,
                        val -> config.moleclawHealth = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> moleclawArmor = Option.<Float>createBuilder()
                .name(key("option.dragonArmor"))
                .binding(defaults.moleclawArmor,
                        () -> config.moleclawArmor,
                        val -> config.moleclawArmor = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> moleclawArmorToughness = Option.<Float>createBuilder()
                .name(key("option.dragonArmorToughness"))
                .binding(defaults.moleclawArmorToughness,
                        () -> config.moleclawArmorToughness,
                        val -> config.moleclawArmorToughness = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> moleclawGroundSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonGroundSpeed"))
                .binding(defaults.moleclawGroundSpeed,
                        () -> config.moleclawGroundSpeed,
                        val -> config.moleclawGroundSpeed = val)
                .customController(FloatFieldController::new)
                .build();

        moleclawAttributesGroup.option(moleclawDamage);
        moleclawAttributesGroup.option(moleclawKnockback);
        moleclawAttributesGroup.option(moleclawHealth);
        moleclawAttributesGroup.option(moleclawArmor);
        moleclawAttributesGroup.option(moleclawArmorToughness);
        moleclawAttributesGroup.option(moleclawGroundSpeed);
        category.group(moleclawAttributesGroup.build());
    }

    private static void addPikehornAttributesGroup(ConfigCategory.Builder category, URMobAttributesConfig config, URMobAttributesConfig defaults) {
        OptionGroup.Builder pikehornAttributesGroup = OptionGroup.createBuilder()
                .name(key("group.pikehornAttributes"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.pikehornAttributes.@Tooltip")).build());

        Option<Float> pikehornDamage = Option.<Float>createBuilder()
                .name(key("option.dragonDamage"))
                .binding(defaults.pikehornDamage,
                        () -> config.pikehornDamage,
                        val -> config.pikehornDamage = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornKnockback = Option.<Float>createBuilder()
                .name(key("option.dragonKnockback"))
                .binding(defaults.pikehornKnockback,
                        () -> config.pikehornKnockback,
                        val -> config.pikehornKnockback = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornHealth = Option.<Float>createBuilder()
                .name(key("option.dragonHealth"))
                .binding(defaults.pikehornHealth,
                        () -> config.pikehornHealth,
                        val -> config.pikehornHealth = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornArmor = Option.<Float>createBuilder()
                .name(key("option.dragonArmor"))
                .binding(defaults.pikehornArmor,
                        () -> config.pikehornArmor,
                        val -> config.pikehornArmor = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornArmorToughness = Option.<Float>createBuilder()
                .name(key("option.dragonArmorToughness"))
                .binding(defaults.pikehornArmorToughness,
                        () -> config.pikehornArmorToughness,
                        val -> config.pikehornArmorToughness = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornGroundSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonGroundSpeed"))
                .binding(defaults.pikehornGroundSpeed,
                        () -> config.pikehornGroundSpeed,
                        val -> config.pikehornGroundSpeed = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> pikehornFlyingSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonFlyingSpeed"))
                .binding(defaults.pikehornFlyingSpeed,
                        () -> config.pikehornFlyingSpeed,
                        val -> config.pikehornFlyingSpeed = val)
                .customController(FloatFieldController::new)
                .build();

        pikehornAttributesGroup.option(pikehornDamage);
        pikehornAttributesGroup.option(pikehornKnockback);
        pikehornAttributesGroup.option(pikehornHealth);
        pikehornAttributesGroup.option(pikehornArmor);
        pikehornAttributesGroup.option(pikehornArmorToughness);
        pikehornAttributesGroup.option(pikehornGroundSpeed);
        pikehornAttributesGroup.option(pikehornFlyingSpeed);
        category.group(pikehornAttributesGroup.build());
    }

    private static void addLightningChaserAttributesGroup(ConfigCategory.Builder category, URMobAttributesConfig config, URMobAttributesConfig defaults) {
        OptionGroup.Builder lightningChaserAttributesGroup = OptionGroup.createBuilder()
                .name(key("group.lightningChaserAttributes"))
                .description(OptionDescription.createBuilder()
                        .text(key("group.lightningChaserAttributes.@Tooltip")).build());

        Option<Float> lightningChaserDamage = Option.<Float>createBuilder()
                .name(key("option.dragonDamage"))
                .binding(defaults.lightningChaserDamage,
                        () -> config.lightningChaserDamage,
                        val -> config.lightningChaserDamage = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserKnockback = Option.<Float>createBuilder()
                .name(key("option.dragonKnockback"))
                .binding(defaults.lightningChaserKnockback,
                        () -> config.lightningChaserKnockback,
                        val -> config.lightningChaserKnockback = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserHealth = Option.<Float>createBuilder()
                .name(key("option.dragonHealth"))
                .binding(defaults.lightningChaserHealth,
                        () -> config.lightningChaserHealth,
                        val -> config.lightningChaserHealth = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserArmor = Option.<Float>createBuilder()
                .name(key("option.dragonArmor"))
                .binding(defaults.lightningChaserArmor,
                        () -> config.lightningChaserArmor,
                        val -> config.lightningChaserArmor = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserArmorToughness = Option.<Float>createBuilder()
                .name(key("option.dragonArmorToughness"))
                .binding(defaults.lightningChaserArmorToughness,
                        () -> config.lightningChaserArmorToughness,
                        val -> config.lightningChaserArmorToughness = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserGroundSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonGroundSpeed"))
                .binding(defaults.lightningChaserGroundSpeed,
                        () -> config.lightningChaserGroundSpeed,
                        val -> config.lightningChaserGroundSpeed = val)
                .customController(FloatFieldController::new)
                .build();
        Option<Float> lightningChaserFlyingSpeed = Option.<Float>createBuilder()
                .name(key("option.dragonFlyingSpeed"))
                .binding(defaults.lightningChaserFlyingSpeed,
                        () -> config.lightningChaserFlyingSpeed,
                        val -> config.lightningChaserFlyingSpeed = val)
                .customController(FloatFieldController::new)
                .build();

        lightningChaserAttributesGroup.option(lightningChaserDamage);
        lightningChaserAttributesGroup.option(lightningChaserKnockback);
        lightningChaserAttributesGroup.option(lightningChaserHealth);
        lightningChaserAttributesGroup.option(lightningChaserArmor);
        lightningChaserAttributesGroup.option(lightningChaserArmorToughness);
        lightningChaserAttributesGroup.option(lightningChaserGroundSpeed);
        lightningChaserAttributesGroup.option(lightningChaserFlyingSpeed);
        category.group(lightningChaserAttributesGroup.build());
    }
}
