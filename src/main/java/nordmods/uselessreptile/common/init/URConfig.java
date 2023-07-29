package nordmods.uselessreptile.common.init;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class URConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("uselessreptile.json");
    private static final GsonConfigInstance<URConfig> CONFIG = GsonConfigInstance.createBuilder(URConfig.class).setPath(CONFIG_PATH).build();

    @ConfigEntry public int wyvernSpawnWeight = 3;
    @ConfigEntry public int moleclawSpawnWeight = 500;
    @ConfigEntry public int pikehornSpawnWeight = 1;
    @ConfigEntry public DragonGriefing allowDragonGriefing = DragonGriefing.ALL;
    @ConfigEntry public int blockDropChance = 100;
    @ConfigEntry public boolean disableNamedTextures = false;
    @ConfigEntry public float dragonDamageMultiplier = 1;
    @ConfigEntry public float dragonHealthMultiplier = 1;
    @ConfigEntry public double cameraDistanceOffset = 2;
    @ConfigEntry public double cameraVerticalOffset = 0;
    @ConfigEntry public double cameraHorizontalOffset = -1.5;
    @ConfigEntry public boolean enableCameraOffset = true;
    @ConfigEntry public boolean enableCrosshair = true;
    @ConfigEntry public boolean autoThirdPerson = true;
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> {
            //category
            ConfigCategory.Builder dragonCategory = ConfigCategory.createBuilder()
                    .name(key("category.dragon"));
            ConfigCategory.Builder cameraCategory = ConfigCategory.createBuilder()
                    .name(key("category.camera"));

            //options - dragon
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


            Option<DragonGriefing> allowDragonGriefing = Option.<DragonGriefing>createBuilder()
                    .name(key("option.allowDragonGriefing"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.allowDragonGriefing.@Tooltip")).build())
                    .binding(defaults.allowDragonGriefing,
                            () -> config.allowDragonGriefing,
                            val -> config.allowDragonGriefing = val)
                    .customController(opt -> new EnumController<>(opt, DragonGriefing.class))
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

            Option<Boolean> disableNamedTextures = Option.<Boolean>createBuilder()
                    .name(key("option.disableNamedTextures"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.disableNamedTextures.@Tooltip")).build())
                    .binding(defaults.disableNamedTextures,
                            () -> config.disableNamedTextures,
                            val -> config.disableNamedTextures = val)
                    .customController(TickBoxController::new)
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

            dragonCategory.option(wyvernSpawnWeight);
            dragonCategory.option(moleclawSpawnWeight);
            dragonCategory.option(pikehornSpawnWeight);
            dragonCategory.option(allowDragonGriefing);
            dragonCategory.option(blockDropChance);
            dragonCategory.option(disableNamedTextures);
            dragonCategory.option(dragonDamageMultiplier);
            dragonCategory.option(dragonHealthMultiplier);

            //category - camera
            Option<Double> cameraDistanceOffset = Option.<Double>createBuilder()
                    .name(key("option.cameraDistanceOffset"))
                    .binding(defaults.cameraDistanceOffset,
                            () -> config.cameraDistanceOffset,
                            val -> config.cameraDistanceOffset = val)
                    .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Double> cameraVerticalOffset = Option.<Double>createBuilder()
                    .name(key("option.cameraVerticalOffset"))
                    .binding(
                            defaults.cameraVerticalOffset,
                            () -> config.cameraVerticalOffset,
                            val -> config.cameraVerticalOffset = val)
                    .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Double> cameraHorizontalOffset = Option.<Double>createBuilder()
                    .name(key("option.cameraHorizontalOffset"))
                    .binding(defaults.cameraHorizontalOffset,
                            () -> config.cameraHorizontalOffset,
                            val -> config.cameraHorizontalOffset = val)
                    .customController(opt -> new DoubleSliderController(opt, -5, 5, 0.05))
                    .build();

            Option<Boolean> enableCameraOffset = Option.<Boolean>createBuilder()
                    .name(key("option.enableCameraOffset"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.enableCameraOffset.@Tooltip")).build())
                    .binding(defaults.enableCameraOffset,
                            () -> config.enableCameraOffset,
                            val -> config.enableCameraOffset = val)
                    .customController(TickBoxController::new)
                    .build();

            Option<Boolean> enableCrosshair = Option.<Boolean>createBuilder()
                    .name(key("option.enableCrosshair"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.enableCrosshair.@Tooltip")).build())
                    .binding(defaults.enableCrosshair,
                            () -> config.enableCrosshair,
                            val -> config.enableCrosshair = val)
                    .customController(TickBoxController::new)
                    .build();

            Option<Boolean> autoThirdPerson = Option.<Boolean>createBuilder()
                    .name(key("option.autoThirdPerson"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.autoThirdPerson.@Tooltip")).build())
                    .binding(defaults.autoThirdPerson,
                            () -> config.autoThirdPerson,
                            val -> config.autoThirdPerson = val)
                    .customController(TickBoxController::new)
                    .build();

            cameraCategory.option(cameraDistanceOffset);
            cameraCategory.option(cameraVerticalOffset);
            cameraCategory.option(cameraHorizontalOffset);
            cameraCategory.option(enableCameraOffset);
            cameraCategory.option(enableCrosshair);
            cameraCategory.option(autoThirdPerson);

            return builder
                    .title(key("title"))
                    .category(dragonCategory.build())
                    .category(cameraCategory.build());
        })).generateScreen(parentScreen);
    }

    public static URConfig getConfig() {
        return CONFIG.getConfig();
    }
    public static float getHealthMultiplier() {
        return URConfig.getConfig().dragonHealthMultiplier;
    }

    public static float getDamageMultiplier() {
        return URConfig.getConfig().dragonDamageMultiplier;
    }

    private static Text key(String id) {
        return Text.translatable("config.uselessreptile." + id);
    }

    public enum DragonGriefing {
        ALL(true, true),
        TAMED(false, true),
        DISABLED(false, false);

        private final boolean untamedBreaking;
        private final boolean tamedBreaking;

        DragonGriefing(boolean untamedBreaking, boolean tamedBreaking) {
            this.untamedBreaking = untamedBreaking;
            this.tamedBreaking = tamedBreaking;
        }

        public boolean canUntamedBreak() {
            return untamedBreaking;
        }

        public boolean canTamedBreak() {
            return tamedBreaking;
        }
    }

    public static void init(){
        CONFIG.load();
    }
}