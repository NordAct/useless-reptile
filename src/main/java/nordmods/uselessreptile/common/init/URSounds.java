package nordmods.uselessreptile.common.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class URSounds {
    public static final SoundEvent DRAGON_WOOSH = register("dragon.woosh");
    public static final SoundEvent WYVERN_STEP = register("wyvern.step");
    public static final SoundEvent WYVERN_BITE = register("wyvern.bite");
    public static final SoundEvent WYVERN_HURT = register("wyvern.hurt");
    public static final SoundEvent WYVERN_AMBIENT = register("wyvern.ambient");
    public static final SoundEvent WYVERN_DEATH = register("wyvern.death");
    public static final SoundEvent MOLECLAW_ATTACK = register("moleclaw.attack");
    public static final SoundEvent MOLECLAW_STRONG_ATTACK = register("moleclaw.strong_attack");
    public static final SoundEvent MOLECLAW_HURT = register("moleclaw.hurt");
    public static final SoundEvent MOLECLAW_AMBIENT = register("moleclaw.ambient");
    public static final SoundEvent MOLECLAW_DEATH = register("moleclaw.death");
    public static final SoundEvent MOLECLAW_PANICKING = register("moleclaw.panicking");
    public static final SoundEvent ACID_BURN = register("acid.burn");
    public static final SoundEvent ACID_SPLASH = register("acid.splash");
    public static final SoundEvent FLUTE_CALL = register("flute.call");
    public static final SoundEvent FLUTE_TARGET = register("flute.target");
    public static final SoundEvent FLUTE_GATHER = register("flute.gather");
    public static final SoundEvent PIKEHORN_ATTACK = register("river_pikehorn.attack");
    public static final SoundEvent PIKEHORN_AMBIENT = register("river_pikehorn.ambient");
    public static final SoundEvent PIKEHORN_HURT = register("river_pikehorn.hurt");
    public static final SoundEvent PIKEHORN_DEATH = register("river_pikehorn.death");

    private static SoundEvent register(String id) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(new Identifier(UselessReptile.MODID, id)));
    }

    @SuppressWarnings("EmptyMethod")
    public static void init () {}
}

