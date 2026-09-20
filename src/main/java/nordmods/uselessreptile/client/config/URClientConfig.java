package nordmods.uselessreptile.client.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.gui.DragonAbilityElement;

public class URClientConfig {
    public static final ConfigClassHandler<URClientConfig> CONFIG = ConfigClassHandler.createBuilder(URClientConfig.class)
            .id(UselessReptile.id("config_client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile_client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "Note: you can edit this config in game by installing Mod Menu and opening config menu in it \nCAMERA SETTINGS")
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

    @SerialEntry(comment = "GUI (I highly recommend editing this one via in-game config screen)\npossible values: RIGHT, RIGHT_WITH_OFFHAND_OFFSET, LEFT, LEFT_WITH_OFFHAND_OFFSET, CUSTOM (values below will be used instead)")
    public DragonAbilityElement.AbilityIconOffsetPreset abilityIconOffsetsPreset = DragonAbilityElement.AbilityIconOffsetPreset.RIGHT;
    @SerialEntry
    public int primaryAbilityIconOffsetX = DragonAbilityElement.AbilityIconOffsetPreset.RIGHT.getPrimaryAbilityIconOffsetX();
    @SerialEntry
    public int primaryAbilityIconOffsetY = DragonAbilityElement.AbilityIconOffsetPreset.RIGHT.getPrimaryAbilityIconOffsetY();
    @SerialEntry
    public int secondaryAbilityIconOffsetX = DragonAbilityElement.AbilityIconOffsetPreset.RIGHT.getSecondaryAbilityIconOffsetX();
    @SerialEntry
    public int secondaryAbilityIconOffsetY = DragonAbilityElement.AbilityIconOffsetPreset.RIGHT.getSecondaryAbilityIconOffsetY();


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
