package nordmods.uselessreptile.common.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.*;

public class URMobAttributesConfig {
    public static final ConfigClassHandler<URMobAttributesConfig> CONFIG = ConfigClassHandler.createBuilder(URMobAttributesConfig.class)
            .id(UselessReptile.id("config_mob_attributes"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile_mob_attributes.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "GLOBAL MULTIPLIERS")
    public float riddenDragonGroundSpeedMultiplier = 1;
    @SerialEntry
    public float riddenDragonFlyingSpeedMultiplier = 1;

    public static URMobAttributesConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }
}
