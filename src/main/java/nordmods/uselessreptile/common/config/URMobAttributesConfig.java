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
    @SerialEntry
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

    @SerialEntry(comment = """
            Note on vanilla attribute value ranges (can be bypassed with other mods):
            Damage: [0;2048]
            Knockback: [0;5]
            Health: [1;1024]
            Armor: [0;30]
            Armor toughness: [0;20]
            Ground and flying speed: [0;1024]
            Other values are technically not attributes and added by the mod itself and have no limitations.
            Attack cooldowns and acceleration duration are measured in ticks, rotation speed - in degrees.
            Movement speed is measured in corgi-sized meteors
            
            SWAMP WYVERN ATTRIBUTES\s""")
    public float wyvernDamage = 6.0f;
    @SerialEntry
    public float wyvernKnockback = 0.3f;
    @SerialEntry
    public float wyvernHealth = 50.0f;
    @SerialEntry
    public float wyvernArmor = 4.0f;
    @SerialEntry
    public float wyvernArmorToughness = 2.0f;
    @SerialEntry
    public float wyvernGroundSpeed = 0.2f;
    @SerialEntry
    public float wyvernFlyingSpeed = 0.7f;
    @SerialEntry
    public int wyvernBaseSecondaryAttackCooldown = 30;
    @SerialEntry
    public int wyvernBasePrimaryAttackCooldown = 80;
    @SerialEntry
    public int wyvernBaseAccelerationDuration = 400;
    @SerialEntry
    public float wyvernRotationSpeedGround = 8;
    @SerialEntry
    public float wyvernRotationSpeedAir = 4;
    @SerialEntry
    public float wyvernVerticalSpeed = 0.4f;
    @SerialEntry
    public float wyvernRegenerationFromFood = 4;

    @SerialEntry(comment = "MOLECLAW ATTRIBUTES")
    public float moleclawDamage = 8.0f;
    @SerialEntry
    public float moleclawKnockback = 0.5f;
    @SerialEntry
    public float moleclawHealth = 80.0f;
    @SerialEntry
    public float moleclawArmor = 8.0f;
    @SerialEntry
    public float moleclawArmorToughness = 4.0f;
    @SerialEntry
    public float moleclawGroundSpeed = 0.25f;
    @SerialEntry
    public int moleclawBaseSecondaryAttackCooldown = 30;
    @SerialEntry
    public int moleclawBasePrimaryAttackCooldown = 60;
    @SerialEntry
    public float moleclawRotationSpeedGround = 6;
    @SerialEntry
    public float moleclawRegenerationFromFood = 2;

    @SerialEntry(comment = "RIVER PIKEHORN ATTRIBUTES")
    public float pikehornDamage = 3.0f;
    @SerialEntry
    public float pikehornKnockback = 0f;
    @SerialEntry
    public float pikehornHealth = 20.0f;
    @SerialEntry
    public float pikehornArmor = 0f;
    @SerialEntry
    public float pikehornArmorToughness = 0f;
    @SerialEntry
    public float pikehornGroundSpeed = 0.2f;
    @SerialEntry
    public float pikehornFlyingSpeed = 0.8f;
    @SerialEntry
    public int pikehornBasePrimaryAttackCooldown = 20;
    @SerialEntry
    public int pikehornBaseAccelerationDuration = 100;
    @SerialEntry
    public float pikehornRotationSpeedGround = 10;
    @SerialEntry
    public float pikehornRotationSpeedAir = 10;
    @SerialEntry
    public float pikehornVerticalSpeed = 0.2f;
    @SerialEntry
    public float pikehornRegenerationFromFood = 3;

    @SerialEntry(comment = "LIGHTNING CHASER ATTRIBUTES")
    public float lightningChaserDamage = 6.0f;
    @SerialEntry
    public float lightningChaserKnockback = 0.3f;
    @SerialEntry
    public float lightningChaserHealth = 70.0f;
    @SerialEntry
    public float lightningChaserArmor = 6f;
    @SerialEntry
    public float lightningChaserArmorToughness = 6.0f;
    @SerialEntry
    public float lightningChaserGroundSpeed = 0.25f;
    @SerialEntry
    public float lightningChaserFlyingSpeed = 0.8f;
    @SerialEntry
    public int lightningChaserBaseSecondaryAttackCooldown = 30;
    @SerialEntry
    public int lightningChaserBasePrimaryAttackCooldown = 30;
    @SerialEntry
    public int lightningChaserBaseAccelerationDuration = 800;
    @SerialEntry
    public float lightningChaserRotationSpeedGround = 9;
    @SerialEntry
    public float lightningChaserRotationSpeedAir = 7;
    @SerialEntry
    public float lightningChaserVerticalSpeed = 0.3f;
    @SerialEntry
    public float lightningChaserRegenerationFromFood = 4;

    public static URMobAttributesConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }
}
