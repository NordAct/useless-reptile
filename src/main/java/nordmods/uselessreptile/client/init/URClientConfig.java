package nordmods.uselessreptile.client.init;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class URClientConfig {
    private static final ConfigClassHandler<URClientConfig> CONFIG = ConfigClassHandler.createBuilder(URClientConfig.class)
            .id(new Identifier(UselessReptile.MODID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile_client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "CAMERA SETTINGS")
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
    @SerialEntry(comment = "DRAGON APPEARANCE")
    public boolean disableNamedEntityModels = false;
    @SerialEntry
    public boolean disableEmissiveTextures = false;
    @SerialEntry
    public boolean attackBoxesInDebug = false;

    public static URClientConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }

    public static URClientConfig getDefaults() {
        return CONFIG.defaults();
    }

    public static void save() {
        CONFIG.save();
    }
}
