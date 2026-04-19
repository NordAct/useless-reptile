package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.datagen.data.URAbstractDataProvider;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonModelProvider extends URAbstractDataProvider<DragonModelData> {

    public URDragonModelProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, DragonModelData.CODEC, "uselessreptile/dragon_model");
    }

    @Override
    public void addEntries(HolderLookup.Provider provider) {
        addWyvern("jeb_", true);
        addWyvern("green", false);
        addWyvern("brown", false);

        addMoleclaw("black", false);
        addMoleclaw("brown", false);
        addMoleclaw("grey", false);
        addMoleclaw("albino", false);

        addLightningChaser("blue", false);
        addLightningChaser("grey", false);
        addLightningChaser("brown", false);
        addLightningChaser("purple", false);

        addRiverPikehorn("green", false);
        addRiverPikehorn("dark_green", false);
        addRiverPikehorn("blue", false);
        addRiverPikehorn("dark_blue", false);
        addRiverPikehorn("purple", false);
        addRiverPikehorn("dark_purple", false);
        addRiverPikehorn("teal", false);
        addRiverPikehorn("dark_teal", false);

        addMagmamuncher("netherrack", false);
        addMagmamuncher("magma", false);
    }

    protected ModelData getModelData(Identifier id, String variant, boolean cull, boolean animatedTexture) {
        Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/" + (animatedTexture ? "animated_textures/" : "") + "entity/" + id.getPath() + "/" + variant + ".png");
        Identifier model = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/models/entity/" + id.getPath() + "/" + id.getPath() + ".geo.json");
        Identifier animation = Identifier.fromNamespaceAndPath(id.getNamespace(), "biscuit_roll/animations/entity/" + id.getPath() + "/" + id.getPath() + ".animation.json");
        return new ModelData(texture, model, animation, cull, false);
    }

    protected void addWyvern(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", URSoundEvent.WYVERN_STEP.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("shoot", SoundEvents.ENDER_DRAGON_SHOOT.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("bite", URSoundEvent.WYVERN_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.WYVERN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.WYVERN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.WYVERN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.WYVERN, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected void addMoleclaw(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", URSoundEvent.DRAGON_STEP.location(), Optional.empty(), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack_strong", URSoundEvent.MOLECLAW_STRONG_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack", URSoundEvent.MOLECLAW_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.MOLECLAW_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.MOLECLAW_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.MOLECLAW_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("panic", URSoundEvent.MOLECLAW_PANICKING.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MOLECLAW, variant, Optional.of(sounds), false, animatedTexture);
    }

    protected void addRiverPikehorn(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", SoundEvents.CHICKEN_STEP.value().location(), Optional.of(0.5f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(0.7f), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.empty(), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("attack", URSoundEvent.PIKEHORN_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.PIKEHORN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.PIKEHORN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.PIKEHORN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.RIVER_PIKEHORN, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected void addLightningChaser(String variant, boolean animatedTexture) {
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
        addEntry(UREntities.LIGHTNING_CHASER, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected void addMagmamuncher(String variant, boolean animatedTexture) {
        List<DragonModelData.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModelData.Sound("step", SoundEvents.NETHERRACK_STEP.location(), Optional.of(0.25f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModelData.Sound("bite", URSoundEvent.MAGMAMUNCHER_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("idle", URSoundEvent.MAGMAMUNCHER_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("hurt", URSoundEvent.MAGMAMUNCHER_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("death", URSoundEvent.MAGMAMUNCHER_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModelData.Sound("apply_fire_resistance", SoundEvents.FIRECHARGE_USE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MAGMAMUNCHER, variant, Optional.of(sounds), true, animatedTexture);
    }

    protected void addEntry(Identifier dragonId, String variant, Optional<List<DragonModelData.Sound>> sounds, boolean cull, boolean animatedTexture) {
        addEntry(id(dragonId, variant), new DragonModelData(getModelData(dragonId, variant, cull, animatedTexture), sounds));
    }

    protected void addEntry(EntityType<? extends Entity> entityType, String variant, Optional<List<DragonModelData.Sound>> sounds, boolean cull, boolean animatedTexture) {
        addEntry(EntityType.getKey(entityType), variant, sounds, cull, animatedTexture);
    }

    protected Identifier id(Identifier dragonId, String variant) {
        return Identifier.fromNamespaceAndPath(dragonId.getNamespace(), dragonId.getPath() + "/" + variant);
    }

    @Override
    public @NonNull String getName() {
        return "Dragon Model";
    }
}
