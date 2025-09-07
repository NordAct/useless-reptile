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

    @SerialEntry(comment = """
            Base dragon attributes.
            Note that in vanilla game attribute values are limited by certain range (this can be changed by other mods):
            Damage: [0;2048]
            Knockback: [0;5]
            Health: [1;1024]
            Armor: [0;30]
            Armor toughness: [0;20]
            Ground, flying and vertical speed: [0;1024]
            Rotation speeds: [0;180]
            Attack cooldowns: [0;2048]
            Acceleration duration: [0;2048]
            Attack cooldowns and acceleration duration are measured in ticks, rotation speeds - in degrees. Movement speed is measured in corgi-sized meteors
            
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
    public float wyvernGroundSpeed = WyvernEntity.BASE_GROUND_SPEED;
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
    public float moleclawGroundSpeed = MoleclawEntity.BASE_GROUND_SPEED;
    @SerialEntry
    public int moleclawBaseSecondaryAttackCooldown = 30;
    @SerialEntry
    public int moleclawBasePrimaryAttackCooldown = 60;
    @SerialEntry
    public float moleclawRotationSpeedGround = 6;

    @SerialEntry(comment = "RIVER PIKEHORN ATTRIBUTES")
    public float riverPikehornDamage = 3.0f;
    @SerialEntry
    public float riverPikehornKnockback = 0f;
    @SerialEntry
    public float riverPikehornHealth = 20.0f;
    @SerialEntry
    public float riverPikehornArmor = 0f;
    @SerialEntry
    public float riverPikehornArmorToughness = 0f;
    @SerialEntry
    public float riverPikehornGroundSpeed = RiverPikehornEntity.BASE_GROUND_SPEED;
    @SerialEntry
    public float riverPikehornFlyingSpeed = 0.8f;
    @SerialEntry
    public int riverPikehornBasePrimaryAttackCooldown = 20;
    @SerialEntry
    public int riverPikehornBaseAccelerationDuration = 200;
    @SerialEntry
    public float riverPikehornRotationSpeedGround = 10;
    @SerialEntry
    public float riverPikehornRotationSpeedAir = 16;
    @SerialEntry
    public float riverPikehornVerticalSpeed = 0.2f;

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
    public float lightningChaserGroundSpeed = LightningChaserEntity.BASE_GROUND_SPEED;
    @SerialEntry
    public float lightningChaserFlyingSpeed = 0.9f;
    @SerialEntry
    public int lightningChaserBaseSecondaryAttackCooldown = 30;
    @SerialEntry
    public int lightningChaserBasePrimaryAttackCooldown = 100;
    @SerialEntry
    public int lightningChaserBaseSpecialAttackCooldown = 300;
    @SerialEntry
    public int lightningChaserBaseAccelerationDuration = 800;
    @SerialEntry
    public float lightningChaserRotationSpeedGround = 9;
    @SerialEntry
    public float lightningChaserRotationSpeedAir = 7;
    @SerialEntry
    public float lightningChaserVerticalSpeed = 0.3f;

    @SerialEntry(comment = "MAGMAMUNCHER ATTRIBUTES")
    public float magmamuncherDamage = 4.0f;
    @SerialEntry
    public float magmamuncherKnockback = 0.2f;
    @SerialEntry
    public float magmamuncherHealth = 16.0f;
    @SerialEntry
    public float magmamuncherArmor = 2;
    @SerialEntry
    public float magmamuncherArmorToughness = 0;
    @SerialEntry
    public float magmamuncherGroundSpeed = MagmamuncherEntity.BASE_GROUND_SPEED;
    @SerialEntry
    public int magmamuncherBasePrimaryAttackCooldown = 30;
    @SerialEntry
    public float magmamuncherRotationSpeedGround = 20;

    public static URMobAttributesConfig getConfig() {
        return CONFIG.instance();
    }

    public static void init() {
        CONFIG.load();
    }
}
