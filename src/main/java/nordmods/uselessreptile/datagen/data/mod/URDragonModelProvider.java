package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.common.init.URSoundEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonModelProvider extends FabricDynamicRegistryProvider {
    public static final ResourceKey<DragonModelData> WYVERN_JEB = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("wyvern/jeb_"));
    public static final ResourceKey<DragonModelData> WYVERN_GREEN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("wyvern/green"));
    public static final ResourceKey<DragonModelData> WYVERN_BROWN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("wyvern/brown"));

    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_GREEN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/green"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_BLUE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/blue"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/purple"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_TEAL = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/teal"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_DARK_GREEN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/dark_green"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_DARK_BLUE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/dark_blue"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_DARK_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/dark_purple"));
    public static final ResourceKey<DragonModelData> RIVER_PIKEHORN_DARK_TEAL = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("river_pikehorn/dark_teal"));

    public static final ResourceKey<DragonModelData> LIGHTNING_CHASER_GREY = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/grey"));
    public static final ResourceKey<DragonModelData> LIGHTNING_CHASER_BLUE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/blue"));
    public static final ResourceKey<DragonModelData> LIGHTNING_CHASER_BROWN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/brown"));
    public static final ResourceKey<DragonModelData> LIGHTNING_CHASER_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("lightning_chaser/purple"));

    public static final ResourceKey<DragonModelData> MOLECLAW_BLACK = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/black"));
    public static final ResourceKey<DragonModelData> MOLECLAW_GREY = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/grey"));
    public static final ResourceKey<DragonModelData> MOLECLAW_BROWN = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/brown"));
    public static final ResourceKey<DragonModelData> MOLECLAW_ALBINO = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("moleclaw/albino"));

    public static final ResourceKey<DragonModelData> MAGMAMUNCHER_NETHERRACK = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("magmamuncher/netherrack"));
    public static final ResourceKey<DragonModelData> MAGMAMUNCHER_MAGMA = ResourceKey.create(URResourceKeys.DRAGON_MODEL, UselessReptile.id("magmamuncher/magma"));


    public URDragonModelProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_MODEL));
    }

    public static void register(BootstrapContext<DragonModelData> bootstrapContext) {
        bootstrapContext.register(WYVERN_JEB, makeWyvern("jeb_", true));
        bootstrapContext.register(WYVERN_GREEN, makeWyvern("green", false));
        bootstrapContext.register(WYVERN_BROWN, makeWyvern("brown", false));
        bootstrapContext.register(MOLECLAW_BLACK, makeMoleclaw("black", false));
        bootstrapContext.register(MOLECLAW_BROWN, makeMoleclaw("brown", false));
        bootstrapContext.register(MOLECLAW_GREY, makeMoleclaw("grey", false));
        bootstrapContext.register(MOLECLAW_ALBINO, makeMoleclaw("albino", false));
        bootstrapContext.register(LIGHTNING_CHASER_BLUE, makeLightningChaser("blue", false));
        bootstrapContext.register(LIGHTNING_CHASER_GREY, makeLightningChaser("grey", false));
        bootstrapContext.register(LIGHTNING_CHASER_BROWN, makeLightningChaser("brown", false));
        bootstrapContext.register(LIGHTNING_CHASER_PURPLE, makeLightningChaser("purple", false));
        bootstrapContext.register(RIVER_PIKEHORN_GREEN, makeRiverPikehorn("green", false));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_GREEN, makeRiverPikehorn("dark_green", false));
        bootstrapContext.register(RIVER_PIKEHORN_BLUE, makeRiverPikehorn("blue", false));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_BLUE, makeRiverPikehorn("dark_blue", false));
        bootstrapContext.register(RIVER_PIKEHORN_PURPLE, makeRiverPikehorn("purple", false));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_PURPLE, makeRiverPikehorn("dark_purple", false));
        bootstrapContext.register(RIVER_PIKEHORN_TEAL, makeRiverPikehorn("teal", false));
        bootstrapContext.register(RIVER_PIKEHORN_DARK_TEAL, makeRiverPikehorn("dark_teal", false));
        bootstrapContext.register(MAGMAMUNCHER_NETHERRACK, makeMagmamuncher("netherrack", false));
        bootstrapContext.register(MAGMAMUNCHER_MAGMA, makeMagmamuncher("magma", false));
    }

    protected static ModelData getModelData(Identifier id, String variant, boolean cull, boolean animatedTexture) {
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/" + (animatedTexture ? "entity/animated_dragon_textures/" : "entity/") + id.getPath() + "/" + variant + ".png");
        Identifier model = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/" + id.getPath() + ".geo.json");
        Identifier animation = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/animations/entity/" + id.getPath() + "/" + id.getPath() + ".animation.json");
        return new ModelData(texture, model, animation, cull, false);
    }

    protected static DragonModelData makeWyvern(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", URSoundEvent.WYVERN_STEP.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("shoot", SoundEvents.ENDER_DRAGON_SHOOT.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("bite", URSoundEvent.WYVERN_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.WYVERN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.WYVERN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.WYVERN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        return makeModel(UREntities.WYVERN, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected static DragonModelData makeMoleclaw(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", URSoundEvent.DRAGON_STEP.location(), Optional.empty(), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack_strong", URSoundEvent.MOLECLAW_STRONG_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack", URSoundEvent.MOLECLAW_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.MOLECLAW_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.MOLECLAW_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.MOLECLAW_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("panic", URSoundEvent.MOLECLAW_PANICKING.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        return makeModel(UREntities.MOLECLAW, variant, Optional.of(sounds), false, animatedTexture);
    }

    protected static DragonModelData makeRiverPikehorn(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", SoundEvents.CHICKEN_STEP.value().location(), Optional.of(0.5f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(0.7f), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.empty(), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack", URSoundEvent.PIKEHORN_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.PIKEHORN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.PIKEHORN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.PIKEHORN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        return makeModel(UREntities.RIVER_PIKEHORN, variant, Optional.of(sounds), false, animatedTexture);
    }

    protected static DragonModelData makeLightningChaser(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", URSoundEvent.DRAGON_STEP.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.6f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap_heavy", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.5f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("bite", URSoundEvent.LIGHTNING_CHASER_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.LIGHTNING_CHASER_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("roar", URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("accept_challenge", URSoundEvent.LIGHTNING_CHASER_ACCEPT_CHALLENGE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.LIGHTNING_CHASER_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.LIGHTNING_CHASER_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        return makeModel(UREntities.LIGHTNING_CHASER, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected static DragonModelData makeMagmamuncher(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", SoundEvents.NETHERRACK_STEP.location(), Optional.of(0.25f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("bite", URSoundEvent.MAGMAMUNCHER_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.MAGMAMUNCHER_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.MAGMAMUNCHER_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.MAGMAMUNCHER_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("apply_fire_resistance", SoundEvents.FIRECHARGE_USE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        return makeModel(UREntities.MAGMAMUNCHER, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected static DragonModelData makeModel(Identifier dragonId, String variant, Optional<List<DragonModelData.Sound>> sounds, boolean cull, boolean animatedTexture) {
        return new DragonModelData(getModelData(dragonId, variant, cull, animatedTexture), sounds);
    }

    protected static DragonModelData makeModel(EntityType<? extends Entity> entityType, String variant, Optional<List<DragonModelData.Sound>> sounds, boolean cull, boolean animatedTexture) {
        return makeModel(EntityType.getKey(entityType), variant, sounds, cull, animatedTexture);
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Model";
    }
}
