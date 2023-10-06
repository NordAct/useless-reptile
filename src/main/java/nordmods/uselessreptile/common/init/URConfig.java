package nordmods.uselessreptile.common.init;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.controllers.TickBoxController;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

import java.lang.Boolean;

public class URConfig {
    private static final ConfigClassHandler<URConfig> CONFIG = ConfigClassHandler.createBuilder(URConfig.class)
            .id(new Identifier(UselessReptile.MODID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile.json"))
                    .setJson5(false)
                    .build())
            .build();

    @SerialEntry
    public int wyvernSpawnWeight = 3;
    @SerialEntry
    public int moleclawSpawnWeight = 500;
    @SerialEntry
    public int pikehornSpawnWeight = 1;
    @SerialEntry
    public DragonGriefing allowDragonGriefing = DragonGriefing.ALL;
    @SerialEntry
    public int blockDropChance = 100;
    @SerialEntry
    public float dragonDamageMultiplier = 1;
    @SerialEntry
    public float dragonHealthMultiplier = 1;
    @SerialEntry
    public double cameraDistanceOffset = 2;
    @SerialEntry
    public double cameraVerticalOffset = 0;
    @SerialEntry
    public double cameraHorizontalOffset = -1.5;
    @SerialEntry
    public boolean enableCameraOffset = true;
    @SerialEntry
    public boolean enableCrosshair = true;
    @SerialEntry
    public boolean autoThirdPerson = true;
    @SerialEntry
    public boolean disableNamedEntityModels = false;
    @SerialEntry
    public boolean disableEmissiveTextures = false;
    public static Screen configScreen(Screen parentScreen) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> {
            //category
            ConfigCategory.Builder gameplayCategory = ConfigCategory.createBuilder()
                    .name(key("category.gameplay"));

            ConfigCategory.Builder clientCategory = ConfigCategory.createBuilder()
                    .name(key("category.client"));

            //group
            OptionGroup.Builder spawnWeightGroup = OptionGroup.createBuilder()
                    .name(key("group.spawnWeight"))
                    .description(OptionDescription.createBuilder()
                            .text(key("group.spawnWeight.@Tooltip")).build());

            OptionGroup.Builder dragonBehaviourGroup = OptionGroup.createBuilder()
                    .name(key("group.dragonBehaviour"))
                    .description(OptionDescription.createBuilder()
                            .text(key("group.dragonBehaviour.@Tooltip")).build());

            OptionGroup.Builder cameraGroup = OptionGroup.createBuilder()
                    .name(key("group.camera"))
                    .description(OptionDescription.createBuilder()
                            .text(key("group.camera.@Tooltip")).build());

            OptionGroup.Builder dragonAppearanceGroup = OptionGroup.createBuilder()
                    .name(key("group.dragonAppearance"))
                    .description(OptionDescription.createBuilder()
                            .text(key("group.dragonAppearance.@Tooltip")).build());

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

            Option<java.lang.Boolean> disableNamedTextures = Option.<java.lang.Boolean>createBuilder()
                    .name(key("option.disableNamedEntityModels"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.disableNamedEntityModels.@Tooltip")).build())
                    .binding(defaults.disableNamedEntityModels,
                            () -> config.disableNamedEntityModels,
                            val -> config.disableNamedEntityModels = val)
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

            spawnWeightGroup.option(wyvernSpawnWeight);
            spawnWeightGroup.option(moleclawSpawnWeight);
            spawnWeightGroup.option(pikehornSpawnWeight);

            dragonBehaviourGroup.option(allowDragonGriefing);
            dragonBehaviourGroup.option(blockDropChance);
            dragonBehaviourGroup.option(dragonDamageMultiplier);
            dragonBehaviourGroup.option(dragonHealthMultiplier);

            gameplayCategory.group(spawnWeightGroup.build());
            gameplayCategory.group(dragonBehaviourGroup.build());

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

            Option<java.lang.Boolean> enableCameraOffset = Option.<java.lang.Boolean>createBuilder()
                    .name(key("option.enableCameraOffset"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.enableCameraOffset.@Tooltip")).build())
                    .binding(defaults.enableCameraOffset,
                            () -> config.enableCameraOffset,
                            val -> config.enableCameraOffset = val)
                    .customController(TickBoxController::new)
                    .build();

            Option<java.lang.Boolean> enableCrosshair = Option.<java.lang.Boolean>createBuilder()
                    .name(key("option.enableCrosshair"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.enableCrosshair.@Tooltip")).build())
                    .binding(defaults.enableCrosshair,
                            () -> config.enableCrosshair,
                            val -> config.enableCrosshair = val)
                    .customController(TickBoxController::new)
                    .build();

            Option<java.lang.Boolean> autoThirdPerson = Option.<java.lang.Boolean>createBuilder()
                    .name(key("option.autoThirdPerson"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.autoThirdPerson.@Tooltip")).build())
                    .binding(defaults.autoThirdPerson,
                            () -> config.autoThirdPerson,
                            val -> config.autoThirdPerson = val)
                    .customController(TickBoxController::new)
                    .build();

            Option<java.lang.Boolean> disableEmissiveTextures = Option.<Boolean>createBuilder()
                    .name(key("option.disableEmissiveTextures"))
                    .description(OptionDescription.createBuilder()
                            .text(key("option.disableEmissiveTextures.@Tooltip")).build())
                    .binding(defaults.disableEmissiveTextures,
                            () -> config.disableEmissiveTextures,
                            val -> config.disableEmissiveTextures = val)
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

            clientCategory.group(cameraGroup.build());
            clientCategory.group(dragonAppearanceGroup.build());

            return builder
                    .title(key("title"))
                    .category(gameplayCategory.build())
                    .category(clientCategory.build());
        })).generateScreen(parentScreen);
    }

    public static URConfig getConfig() {
        return CONFIG.instance();
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
