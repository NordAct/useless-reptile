package nordmods.uselessreptile.common.init;

import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class URConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("uselessreptile.json");
    public static final GsonConfigInstance<URConfig> CONFIG = new GsonConfigInstance<>(URConfig.class, CONFIG_PATH);

    @ConfigEntry
    public int wyvernSpawnWeight = 3;
    @ConfigEntry
    public int moleclawSpawnWeight = 500;
    @ConfigEntry
    public int pikehornSpawnWeight = 1;
    @ConfigEntry
    public DragonGriefing allowDragonGriefing = DragonGriefing.ALL;
    @ConfigEntry
    public int blockDropChance = 100;
    @ConfigEntry
    public float dragonDamageMultiplier = 1;
    @ConfigEntry
    public float dragonHealthMultiplier = 1;
    @ConfigEntry
    public double cameraDistanceOffset = 2;
    @ConfigEntry
    public double cameraVerticalOffset = 0;
    @ConfigEntry
    public double cameraHorizontalOffset = -1.5;
    @ConfigEntry
    public boolean enableCameraOffset = true;
    @ConfigEntry
    public boolean enableCrosshair = true;
    @ConfigEntry
    public boolean autoThirdPerson = true;
    @ConfigEntry
    public boolean disableNamedEntityModels = false;
    @ConfigEntry
    public boolean disableEmissiveTextures = false;

    public static URConfig getConfig() {
        return CONFIG.getConfig();
    }
    public static float getHealthMultiplier() {
        return URConfig.getConfig().dragonHealthMultiplier;
    }

    public static float getDamageMultiplier() {
        return URConfig.getConfig().dragonDamageMultiplier;
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
