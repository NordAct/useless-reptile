package nordmods.uselessreptile.datagen.assets;

import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URSoundEvent;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URSoundsProvider extends FabricSoundsProvider {
    public URSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider registryLookup, SoundExporter exporter) {
        exporter.add(URSoundEvent.DRAGON_WOOSH,
                SoundTypeBuilder.of()
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh4")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh5")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/woosh6")))
        );
        exporter.add(URSoundEvent.DRAGON_STEP,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.block.generic.footsteps")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/step1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/step2")))
        );

        exporter.add(URSoundEvent.WYVERN_STEP,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.block.generic.footsteps")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/step1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/step2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/step3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/step4")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/step5")))
        );
        exporter.add(URSoundEvent.WYVERN_BITE,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.dragon.bite")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/bite1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/bite2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/bite3")))
        );
        exporter.add(URSoundEvent.WYVERN_HURT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.wyvern.hurt")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/hit1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/hit2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/hit3")))
        );
        exporter.add(URSoundEvent.WYVERN_AMBIENT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.wyvern.ambient")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/idle1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/idle2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/idle3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/idle4")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/idle5")))
        );
        exporter.add(URSoundEvent.WYVERN_DEATH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.wyvern.death")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("wyvern/death1")))
        );

        exporter.add(URSoundEvent.MOLECLAW_HURT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.hurt")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/hit1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/hit2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/hit3")))
        );
        exporter.add(URSoundEvent.MOLECLAW_AMBIENT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.ambient")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/idle1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/idle2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/idle3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/idle4")))
        );
        exporter.add(URSoundEvent.MOLECLAW_DEATH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.death")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/death")))
        );
        exporter.add(URSoundEvent.MOLECLAW_PANICKING,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.panicking")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/panic1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/panic2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/panic3")))
        );
        exporter.add(URSoundEvent.MOLECLAW_ATTACK,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.attack")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/attack")))
        );
        exporter.add(URSoundEvent.MOLECLAW_STRONG_ATTACK,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.moleclaw.strong_attack")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("moleclaw/strong_attack")))
        );

        exporter.add(URSoundEvent.ACID_SPLASH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.acid.splash")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_splash")))
        );
        exporter.add(URSoundEvent.ACID_BURN,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.acid.burn")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_burn1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_burn2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_burn3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_burn4")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/acid_burn5")))
        );

        exporter.add(URSoundEvent.FLUTE_CALL,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.flute")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("flute/call")))
        );
        exporter.add(URSoundEvent.FLUTE_TARGET,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.flute")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("flute/target")))
        );
        exporter.add(URSoundEvent.FLUTE_GATHER,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.flute")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("flute/gather")))
        );
        exporter.add(URSoundEvent.FLUTE_SIT_DOWN,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.flute")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("flute/sit_down")))
        );
        exporter.add(URSoundEvent.FLUTE_STAND_UP,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.flute")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("flute/stand_up")))
        );

        exporter.add(URSoundEvent.PIKEHORN_HURT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.river_pikehorn.hurt")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/hit1")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/hit2")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/hit3")).pitch(0.75f))
        );
        exporter.add(URSoundEvent.PIKEHORN_AMBIENT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.river_pikehorn.ambient")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/idle1")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/idle2")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/idle3")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/idle4")).pitch(0.75f))
        );
        exporter.add(URSoundEvent.PIKEHORN_DEATH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.river_pikehorn.death")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/death")).pitch(0.75f))
        );
        exporter.add(URSoundEvent.PIKEHORN_ATTACK,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.river_pikehorn.attack")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/attack1")).pitch(0.75f))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("river_pikehorn/attack2")).pitch(0.75f))
        );

        exporter.add(URSoundEvent.SHOCKWAVE_HIT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.shockwave.hit")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave_hit1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave_hit2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave_hit3")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave_hit4")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave_hit5")))
        );
        exporter.add(URSoundEvent.SHOCKWAVE,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.shockwave")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave1")).attenuationDistance(URSoundEvent.SHOCKWAVE.fixedRange().orElseThrow().intValue()))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave2")).attenuationDistance(URSoundEvent.SHOCKWAVE.fixedRange().orElseThrow().intValue()))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("common/shockwave3")).attenuationDistance(URSoundEvent.SHOCKWAVE.fixedRange().orElseThrow().intValue()))
        );

        exporter.add(URSoundEvent.LIGHTNING_CHASER_HURT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.hurt")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/hit1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/hit2")))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_AMBIENT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.ambient")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/idle1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/idle2")))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_DEATH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.death")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/death")))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_BITE,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.dragon.bite")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/bite1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/bite2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/bite3")))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.roar")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/roar1")).attenuationDistance(URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR.fixedRange().orElseThrow().intValue()))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/roar2")).attenuationDistance(URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR.fixedRange().orElseThrow().intValue()))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/roar3")).attenuationDistance(URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR.fixedRange().orElseThrow().intValue()))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_ACCEPT_CHALLENGE,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.roar")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/accept_challenge")).attenuationDistance(URSoundEvent.LIGHTNING_CHASER_ACCEPT_CHALLENGE.fixedRange().orElseThrow().intValue()))
        );
        exporter.add(URSoundEvent.LIGHTNING_CHASER_SURRENDER,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.lightning_chaser.roar")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("lightning_chaser/surrender")).attenuationDistance(URSoundEvent.LIGHTNING_CHASER_SURRENDER.fixedRange().orElseThrow().intValue()))
        );

        exporter.add(URSoundEvent.VORTEX_HORN_SUCK_IN,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.vortex_horn.suck_in")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("vortex_horn/suck_in")))
        );
        exporter.add(URSoundEvent.VORTEX_HORN_SPIT_OUT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.vortex_horn.spit_out")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("vortex_horn/spit_out")))
        );

        exporter.add(URSoundEvent.MAGMAMUNCHER_HURT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.magmamuncher.hurt")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/hit")))
        );
        exporter.add(URSoundEvent.MAGMAMUNCHER_AMBIENT,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.magmamuncher.ambient")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/idle1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/idle2")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/idle3")))
        );
        exporter.add(URSoundEvent.MAGMAMUNCHER_DEATH,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.river_pikehorn.death")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/death")))
        );
        exporter.add(URSoundEvent.MAGMAMUNCHER_BITE,
                SoundTypeBuilder.of()
                        .subtitle("subtitles.uselessreptile.dragon.bite")
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/bite1")))
                        .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(UselessReptile.id("magmamuncher/bite2")))
        );
    }

    @Override
    public @NonNull String getName() {
        return "UR Sounds";
    }
}
