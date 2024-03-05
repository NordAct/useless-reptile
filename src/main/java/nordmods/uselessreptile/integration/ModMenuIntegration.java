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
import nordmods.uselessreptile.client.init.URClientConfig;
import nordmods.uselessreptile.common.init.URConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(URConfig.CONFIG, ((defaults, config, builder) -> builder
                .title(key("title"))
                .category(gameplayCategory())
                .category(clientCategory())
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
                        .text(key("option.wyvernSpawnWeight.@Tooltip")).build())
                .binding(defaults.wyvernSpawnWeight,
                        () -> config.wyvernSpawnWeight,
                        val -> config.wyvernSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> moleclawSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.moleclawSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.moleclawSpawnWeight.@Tooltip")).build())
                .binding(defaults.moleclawSpawnWeight,
                        () -> config.moleclawSpawnWeight,
                        val -> config.moleclawSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();

        Option<Integer> pikehornSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.pikehornSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.pikehornSpawnWeight.@Tooltip")).build())
                .binding(defaults.pikehornSpawnWeight,
                        () -> config.pikehornSpawnWeight,
                        val -> config.pikehornSpawnWeight = val)
                .customController(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                .build();
        Option<Integer> lightningChaserSpawnWeight = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserSpawnWeight"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.lightningChaserSpawnWeight.@Tooltip")).build())
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
                .description(OptionDescription.createBuilder()
                        .text(key("option.wyvernMinGroupSize.@Tooltip")).build())
                .binding(defaults.wyvernMinGroupSize,
                        () -> config.wyvernMinGroupSize,
                        val -> config.wyvernMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> wyvernMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.wyvernMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.wyvernMaxGroupSize.@Tooltip")).build())
                .binding(defaults.wyvernMaxGroupSize,
                        () -> config.wyvernMaxGroupSize,
                        val -> config.wyvernMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> moleclawMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.moleclawMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.moleclawMinGroupSize.@Tooltip")).build())
                .binding(defaults.moleclawMinGroupSize,
                        () -> config.moleclawMinGroupSize,
                        val -> config.moleclawMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> moleclawMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.moleclawMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.moleclawMaxGroupSize.@Tooltip")).build())
                .binding(defaults.moleclawMaxGroupSize,
                        () -> config.moleclawMaxGroupSize,
                        val -> config.moleclawMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> pikehornMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.pikehornMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.pikehornMinGroupSize.@Tooltip")).build())
                .binding(defaults.pikehornMinGroupSize,
                        () -> config.pikehornMinGroupSize,
                        val -> config.pikehornMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> pikehornMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.pikehornMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.pikehornMaxGroupSize.@Tooltip")).build())
                .binding(defaults.pikehornMaxGroupSize,
                        () -> config.pikehornMaxGroupSize,
                        val -> config.pikehornMaxGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> lightningChaserMinGroupSize = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserMinGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.lightningChaserMinGroupSize.@Tooltip")).build())
                .binding(defaults.lightningChaserMinGroupSize,
                        () -> config.lightningChaserMinGroupSize,
                        val -> config.lightningChaserMinGroupSize = val)
                .customController(opt -> new IntegerFieldController(opt, 1, Integer.MAX_VALUE))
                .build();

        Option<Integer> lightningChaserMaxGroupSize = Option.<Integer>createBuilder()
                .name(key("option.lightningChaserMaxGroupSize"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.lightningChaserMaxGroupSize.@Tooltip")).build())
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


        Option<Float> dragonDamageMultiplier = Option.<Float>createBuilder()
                .name(key("option.dragonDamageMultiplier"))
                .description(OptionDescription.createBuilder()
                        .text(key("option.dragonDamageMultiplier.@Tooltip")).build())
                .binding(defaults.dragonDamageMultiplier,
                        () -> config.dragonDamageMultiplier,
                        val -> config.dragonDamageMultiplier = val)
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
        //todo move to stats group
        dragonBehaviourGroup.option(dragonDamageMultiplier);
        dragonBehaviourGroup.option(dragonHealthMultiplier);

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
}
