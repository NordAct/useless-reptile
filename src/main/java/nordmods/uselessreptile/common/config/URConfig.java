package nordmods.uselessreptile.common.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nordmods.uselessreptile.UselessReptile;

public class URConfig {
    public static final ConfigClassHandler<URConfig> CONFIG = ConfigClassHandler.createBuilder(URConfig.class)
            .id(UselessReptile.id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile.json5"))
                    .setJson5(true)
                    .build())
            .build();

    //COMMON
    @SerialEntry(comment = "IN WORLD SPAWN \nNatural spawn means dragon can spawn randomly in same manner as any other normal mob if conditions are met")
    public boolean naturalWyvernSpawn = true;
    @SerialEntry
    public boolean naturalMoleclawSpawn = true;
    @SerialEntry
    public boolean naturalRiverPikehornSpawn = true;
    @SerialEntry(comment = "Note: Lightning Chaser doesn't have variants for natural spawn defined and thus won't appear by default." +
            "\nYou have to add name spawns manually via datapack if you want it to spawn naturally")
    public boolean naturalLightningChaserSpawn = false;
    @SerialEntry(comment = "Defines a chance of Lightning Chaser spawning near player during thunderstorms each 30 seconds (1200 ticks)")
    public int lightningChaserThunderstormSpawnChance = 10;
    @SerialEntry(comment = "Amount of time (in ticks) that must pass before Lightning Chaser can attempt to spawn near the same player")
    public int lightningChaserThunderstormSpawnTimerCooldown = 24000;
    @SerialEntry(comment = "SPAWN GROUP CAPACITIES")
    public int dragonSpawnGroupCapacity = 2;
    @SerialEntry
    public int smallDragonSpawnGroupCapacity = 12;
    @SerialEntry
    public int undergroundDragonSpawnGroupCapacity = 6;
    @SerialEntry(comment = "GROUP SIZES")
    public int wyvernMinGroupSize = 1;
    @SerialEntry
    public int wyvernMaxGroupSize = 1;
    @SerialEntry
    public int moleclawMinGroupSize = 1;
    @SerialEntry
    public int moleclawMaxGroupSize = 1;
    @SerialEntry
    public int riverPikehornMinGroupSize = 1;
    @SerialEntry
    public int riverPikehornMaxGroupSize = 3;
    @SerialEntry
    public int lightningChaserMinGroupSize = 1;
    @SerialEntry
    public int lightningChaserMaxGroupSize = 1;
    @SerialEntry(comment = "BEHAVIOUR \nPossible values: ALL, TAMED, DISABLED")
    public DragonGriefing moleclawGriefing = DragonGriefing.ALL;
    @SerialEntry
    public DragonGriefing lightningChaserGriefing = DragonGriefing.ALL;
    @SerialEntry
    public int blockDropChance = 100;
    @SerialEntry(comment = "allows dragon to teleport to owner whenever it tries to follow")
    public boolean allowDragonTeleport = true;
    @SerialEntry
    public boolean dragonMadness = false;

    public static URConfig getConfig() {
        return CONFIG.instance();
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
