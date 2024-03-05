package nordmods.uselessreptile.common.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class URMobAttributesConfig {
    public static final ConfigClassHandler<URMobAttributesConfig> CONFIG = ConfigClassHandler.createBuilder(URMobAttributesConfig.class)
            .id(new Identifier(UselessReptile.MODID, "config_mob_attributes"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("uselessreptile_mob_attributes.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "GLOBAL MULTIPLIERS")
    public float dragonDamageMultiplier = 1;
    public float dragonKnockbackMultiplier = 1;
    @SerialEntry
    public float dragonHealthMultiplier = 1;
    @SerialEntry
    public float dragonArmorMultiplier = 1;
    @SerialEntry
    public float dragonArmorToughnessMultiplier = 1;
    @SerialEntry
    public float dragonGroundSpeedMultiplier = 1;
    @SerialEntry
    public float dragonFlyingSpeedMultiplier = 1;

    @SerialEntry(comment = "Note on vanilla attribute value ranges (can be bypassed with other mods):"
            + "\ndamage: [0;2048]"
            + "\nknockback: [0;5]"
            + "\nhealth: [1;1024]"
            + "\narmor: [0;30]"
            + "\narmor toughness: [0;20]"
            + "\nground and flying speed: [0;1024]"
            + "\nSWAMP WYVERN ATTRIBUTES ")
    public float wyvernDamage = 6.0f;
    @SerialEntry
    public float wyvernKnockback = 0.3f;
    @SerialEntry
    public float wyvernHealth = 35.0f;
    @SerialEntry
    public float wyvernArmor = 4.0f;
    @SerialEntry
    public float wyvernArmorToughness = 2.0f;
    @SerialEntry
    public float wyvernGroundSpeed = 0.2f;
    @SerialEntry
    public float wyvernFlyingSpeed = 0.7f;

    @SerialEntry(comment = "MOLECLAW ATTRIBUTES")
    public float moleclawDamage = 8.0f;
    @SerialEntry
    public float moleclawKnockback = 0.5f;
    @SerialEntry
    public float moleclawHealth = 50.0f;
    @SerialEntry
    public float moleclawArmor = 8.0f;
    @SerialEntry
    public float moleclawArmorToughness = 4.0f;
    @SerialEntry
    public float moleclawGroundSpeed = 0.25f;

    @SerialEntry(comment = "RIVER PIKEHORN ATTRIBUTES")
    public float pikehornDamage = 3.0f;
    @SerialEntry
    public float pikehornKnockback = 0f;
    @SerialEntry
    public float pikehornHealth = 14.0f;
    @SerialEntry
    public float pikehornArmor = 0f;
    @SerialEntry
    public float pikehornArmorToughness = 0f;
    @SerialEntry
    public float pikehornGroundSpeed = 0.2f;
    @SerialEntry
    public float pikehornFlyingSpeed = 0.8f;

    @SerialEntry(comment = "LIGHTNING CHASER ATTRIBUTES")
    public float lightningChaserDamage = 6.0f;
    @SerialEntry
    public float lightningChaserKnockback = 0.3f;
    @SerialEntry
    public float lightningChaserHealth = 45.0f;
    @SerialEntry
    public float lightningChaserArmor = 6f;
    @SerialEntry
    public float lightningChaserArmorToughness = 6.0f;
    @SerialEntry
    public float lightningChaserGroundSpeed = 0.25f;
    @SerialEntry
    public float lightningChaserFlyingSpeed = 0.8f;

    public static URMobAttributesConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }
}
