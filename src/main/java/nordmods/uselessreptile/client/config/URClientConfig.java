package nordmods.uselessreptile.client.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nordmods.uselessreptile.UselessReptile;

public class URClientConfig {
    public static final ConfigClassHandler<URClientConfig> CONFIG = ConfigClassHandler.createBuilder(URClientConfig.class)
            .id(UselessReptile.id("config_client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile_client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "CAMERA SETTINGS")
    public float cameraDistanceOffset = 2;
    @SerialEntry
    public float cameraVerticalOffset = 0;
    @SerialEntry
    public float cameraHorizontalOffset = -1.5f;
    @SerialEntry
    public boolean enableCameraOffset = true;
    @SerialEntry
    public boolean enableCameraRoll = true;
    @SerialEntry
    public boolean enableCrosshair = true;
    @SerialEntry
    public boolean autoThirdPerson = true;
    @SerialEntry(comment = "allows to fly up or down while riding dragon by changing camera's pitch")
    public boolean upDownCameraControl = false;
    @SerialEntry
    public float upDownCameraPitchThreshold = 30;
    @SerialEntry(comment = "DRAGON APPEARANCE")
    public boolean disableNamedEntityModels = false;
    @SerialEntry
    public boolean disableEmissiveTextures = false;
    @SerialEntry(comment = "hides information in item tooltip about which dragons can wear specific items")
    public boolean hideEquipmentInfo = false;
    @SerialEntry(comment = "defines if passengers on dragons can be rendered \npossible valuses: NONE, SELF, OTHERS, ALL")
    public PassengerVisibility renderPassengers = PassengerVisibility.ALL;
    @SerialEntry(comment = "displays area in which dragon can deal damage for melee attacks")
    public boolean attackBoxesInDebug = false;


    public static URClientConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }

    public enum PassengerVisibility {
        NONE(false, false),
        SELF(true, false),
        OTHERS(false, true),
        ALL(true, true),
        ;
        private final boolean renderSelf;
        private final boolean renderOthers;

        PassengerVisibility(boolean renderSelf, boolean renderOthers) {
            this.renderSelf = renderSelf;
            this.renderOthers = renderOthers;
        }

        public boolean canRenderSelf() {
            return renderSelf;
        }

        public boolean canRenderOthers() {
            return renderOthers;
        }
    }
}
