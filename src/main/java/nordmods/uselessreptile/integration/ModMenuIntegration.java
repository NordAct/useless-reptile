package nordmods.uselessreptile.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.init.URConfig;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(URConfig.CONFIG, ((defaults, config, builder) -> {
            //category
            ConfigCategory.Builder gameplayCategory = ConfigCategory.createBuilder()
                    .name(key("category.gameplay"));

            ConfigCategory.Builder clientCategory = ConfigCategory.createBuilder()
                    .name(key("category.client"));

            //group
            OptionGroup.Builder spawnWeightGroup = OptionGroup.createBuilder()
                    .name(key("group.spawnWeight"))
                    .tooltip(key("group.spawnWeight.@Tooltip"));

            OptionGroup.Builder dragonBehaviourGroup = OptionGroup.createBuilder()
                    .name(key("group.dragonBehaviour"))
                    .tooltip(key("group.dragonBehaviour.@Tooltip"));

            OptionGroup.Builder cameraGroup = OptionGroup.createBuilder()
                    .name(key("group.camera"))
                    .tooltip(key("group.camera.@Tooltip"));

            OptionGroup.Builder dragonAppearanceGroup = OptionGroup.createBuilder()
                    .name(key("group.dragonAppearance"))
                    .tooltip(key("group.dragonAppearance.@Tooltip"));

            //options
            Option<Integer> wyvernSpawnWeight = Option.<Integer>createBuilder(Integer.class)
                    .name(key("option.wyvernSpawnWeight"))
                    .tooltip(key("option.wyvernSpawnWeight.@Tooltip"))
                    .binding(defaults.wyvernSpawnWeight,
                            () -> config.wyvernSpawnWeight,
                            val -> config.wyvernSpawnWeight = val)
                    .controller(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                    .build();

            Option<Integer> moleclawSpawnWeight = Option.createBuilder(Integer.class)
                    .name(key("option.moleclawSpawnWeight"))
                    .tooltip(key("option.moleclawSpawnWeight.@Tooltip"))
                    .binding(defaults.moleclawSpawnWeight,
                            () -> config.moleclawSpawnWeight,
                            val -> config.moleclawSpawnWeight = val)
                    .controller(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                    .build();

            Option<Integer> pikehornSpawnWeight = Option.createBuilder(Integer.class)
                    .name(key("option.pikehornSpawnWeight"))
                    .tooltip(key("option.pikehornSpawnWeight.@Tooltip"))
                    .binding(defaults.pikehornSpawnWeight,
                            () -> config.pikehornSpawnWeight,
                            val -> config.pikehornSpawnWeight = val)
                    .controller(opt -> new IntegerFieldController(opt, 0, Integer.MAX_VALUE))
                    .build();

            Option<URConfig.DragonGriefing> allowDragonGriefing = Option.createBuilder(URConfig.DragonGriefing.class)
                    .name(key("option.allowDragonGriefing"))
                    .tooltip(key("option.allowDragonGriefing.@Tooltip"))
                    .binding(defaults.allowDragonGriefing,
                            () -> config.allowDragonGriefing,
                            val -> config.allowDragonGriefing = val)
                    .controller(EnumController::new)
                    .build();

            Option<Integer> blockDropChance = Option.createBuilder(Integer.class)
                    .name(key("option.blockDropChance"))
                    .tooltip(key("option.blockDropChance.@Tooltip"))
                    .binding(defaults.blockDropChance,
                            () -> config.blockDropChance,
                            val -> config.blockDropChance = val)
                    .controller(opt -> new IntegerSliderController(opt, 0, 100, 1))
                    .build();

            Option<Boolean> disableNamedTextures = Option.createBuilder(Boolean.class)
                    .name(key("option.disableNamedEntityModels"))
                    .tooltip(key("option.disableNamedEntityModels.@Tooltip"))
                    .binding(defaults.disableNamedEntityModels,
                            () -> config.disableNamedEntityModels,
                            val -> config.disableNamedEntityModels = val)
                    .controller(TickBoxController::new)
                    .build();

            Option<Float> dragonDamageMultiplier = Option.createBuilder(Float.class)
                    .name(key("option.dragonDamageMultiplier"))
                    .tooltip(key("option.dragonDamageMultiplier.@Tooltip"))
                    .binding(defaults.dragonDamageMultiplier,
                            () -> config.dragonDamageMultiplier,
                            val -> config.dragonDamageMultiplier = val)
                    .controller(FloatFieldController::new)
                    .build();

            Option<Float> dragonHealthMultiplier = Option.<Float>createBuilder(Float.class)
                    .name(key("option.dragonHealthMultiplier"))
                    .tooltip(key("option.dragonHealthMultiplier.@Tooltip"))
                    .binding(defaults.dragonHealthMultiplier,
                            () -> config.dragonHealthMultiplier,
                            val -> config.dragonHealthMultiplier = val)
                    .controller(FloatFieldController::new)
                    .build();

            spawnWeightGroup.option(wyvernSpawnWeight);
            spawnWeightGroup.option(moleclawSpawnWeight);
            spawnWeightGroup.option(pikehornSpawnWeight);

            dragonBehaviourGroup.option(allowDragonGriefing);
            dragonBehaviourGroup.option(blockDropChance);
            dragonBehaviourGroup.option(dragonDamageMultiplier);
            dragonBehaviourGroup.option(dragonHealthMultiplier);

            gameplayCategory.group(spawnWeightGroup.build());
            gameplayCategory.group(dragonBehaviourGroup.build());

            Option<Double> cameraDistanceOffset = Option.createBuilder(Double.class)
                    .name(key("option.cameraDistanceOffset"))
                    .binding(defaults.cameraDistanceOffset,
                            () -> config.cameraDistanceOffset,
                            val -> config.cameraDistanceOffset = val)
                    .controller(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Double> cameraVerticalOffset = Option.createBuilder(Double.class)
                    .name(key("option.cameraVerticalOffset"))
                    .binding(
                            defaults.cameraVerticalOffset,
                            () -> config.cameraVerticalOffset,
                            val -> config.cameraVerticalOffset = val)
                    .controller(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Double> cameraHorizontalOffset = Option.createBuilder(Double.class)
                    .name(key("option.cameraHorizontalOffset"))
                    .binding(defaults.cameraHorizontalOffset,
                            () -> config.cameraHorizontalOffset,
                            val -> config.cameraHorizontalOffset = val)
                    .controller(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Boolean> enableCameraOffset = Option.createBuilder(Boolean.class)
                    .name(key("option.enableCameraOffset"))
                    .tooltip(key("option.enableCameraOffset.@Tooltip"))
                    .binding(defaults.enableCameraOffset,
                            () -> config.enableCameraOffset,
                            val -> config.enableCameraOffset = val)
                    .controller(TickBoxController::new)
                    .build();

            Option<Boolean> enableCrosshair = Option.createBuilder(Boolean.class)
                    .name(key("option.enableCrosshair"))
                    .tooltip(key("option.enableCrosshair.@Tooltip"))
                    .binding(defaults.enableCrosshair,
                            () -> config.enableCrosshair,
                            val -> config.enableCrosshair = val)
                    .controller(TickBoxController::new)
                    .build();

            Option<Boolean> autoThirdPerson = Option.createBuilder(Boolean.class)
                    .name(key("option.autoThirdPerson"))
                    .tooltip(key("option.autoThirdPerson.@Tooltip"))
                    .binding(defaults.autoThirdPerson,
                            () -> config.autoThirdPerson,
                            val -> config.autoThirdPerson = val)
                    .controller(TickBoxController::new)
                    .build();

            Option<Boolean> disableEmissiveTextures = Option.createBuilder(Boolean.class)
                    .name(key("option.disableEmissiveTextures"))
                    .tooltip(key("option.disableEmissiveTextures.@Tooltip"))
                    .binding(defaults.disableEmissiveTextures,
                            () -> config.disableEmissiveTextures,
                            val -> config.disableEmissiveTextures = val)
                    .controller(TickBoxController::new)
                    .build();

            cameraGroup.option(cameraDistanceOffset);
            cameraGroup.option(cameraVerticalOffset);
            cameraGroup.option(cameraHorizontalOffset);
            cameraGroup.option(enableCameraOffset);
            cameraGroup.option(enableCrosshair);
            cameraGroup.option(autoThirdPerson);

            dragonAppearanceGroup.option(disableNamedTextures);
            dragonAppearanceGroup.option(disableEmissiveTextures);

            clientCategory.group(cameraGroup.build());
            clientCategory.group(dragonAppearanceGroup.build());

            return builder
                    .title(key("title"))
                    .category(gameplayCategory.build())
                    .category(clientCategory.build());
        })).generateScreen(parentScreen);
    }

    private static Text key(String id) {
        return Text.translatable("config.uselessreptile." + id);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::configScreen;
    }
}
