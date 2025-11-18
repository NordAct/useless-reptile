package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSoundEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonModelProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private final List<Tuple<ResourceLocation, DragonModel>> holder = new ArrayList<>();

    public URDragonModelProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/dragon_model");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addSpawnEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(entry -> {
                Path path = pathResolver.json(entry.getA());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DragonModel.CODEC, entry.getB(), path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addSpawnEntries() {
        addWyvern("jeb_");
        addWyvern("green");
        addWyvern("brown");

        addMoleclaw("black");
        addMoleclaw("brown");
        addMoleclaw("grey");
        addMoleclaw("albino");

        addLightningChaser("blue");
        addLightningChaser("grey");
        addLightningChaser("brown");
        addLightningChaser("purple");

        addRiverPikehorn("green");
        addRiverPikehorn("dark_green");
        addRiverPikehorn("blue");
        addRiverPikehorn("dark_blue");
        addRiverPikehorn("purple");
        addRiverPikehorn("dark_purple");
        addRiverPikehorn("teal");
        addRiverPikehorn("dark_teal");

        addMagmamuncher("netherrack");
        addMagmamuncher("magma");
    }

    protected ModelData getModelData(ResourceLocation id, String variant, boolean cull) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "entity/" + id.getPath() + "/" + variant);
        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "entity/" + id.getPath() + "/" + id.getPath());
        ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "entity/" + id.getPath() + "/" + id.getPath());
        return new ModelData(texture, model, Optional.of(animation), cull, false);
    }

    protected void addWyvern(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSoundEvent.WYVERN_STEP.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModel.Sound("shoot", SoundEvents.ENDER_DRAGON_SHOOT.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("bite", URSoundEvent.WYVERN_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSoundEvent.WYVERN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSoundEvent.WYVERN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSoundEvent.WYVERN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.WYVERN_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addMoleclaw(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSoundEvent.DRAGON_STEP.location(), Optional.empty(), Optional.of(0.7f), Optional.empty()));
        sounds.add(new DragonModel.Sound("attack_strong", URSoundEvent.MOLECLAW_STRONG_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("attack", URSoundEvent.MOLECLAW_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSoundEvent.MOLECLAW_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSoundEvent.MOLECLAW_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSoundEvent.MOLECLAW_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("panic", URSoundEvent.MOLECLAW_PANICKING.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MOLECLAW_ENTITY, variant, Optional.of(sounds), false);
    }

    protected void addRiverPikehorn(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", SoundEvents.CHICKEN_STEP.location(), Optional.of(0.5f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModel.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(0.7f), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.empty(), Optional.of(1.2f), Optional.empty()));
        sounds.add(new DragonModel.Sound("attack", URSoundEvent.PIKEHORN_ATTACK.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSoundEvent.PIKEHORN_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSoundEvent.PIKEHORN_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSoundEvent.PIKEHORN_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.RIVER_PIKEHORN_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addLightningChaser(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSoundEvent.DRAGON_STEP.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("woosh", URSoundEvent.DRAGON_WOOSH.location(), Optional.of(2f), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.6f), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap_heavy", SoundEvents.ENDER_DRAGON_FLAP.location(), Optional.of(3f), Optional.of(0.5f), Optional.empty()));
        sounds.add(new DragonModel.Sound("bite", URSoundEvent.LIGHTNING_CHASER_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSoundEvent.LIGHTNING_CHASER_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("roar", URSoundEvent.LIGHTNING_CHASER_DISTANT_ROAR.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("accept_challenge", URSoundEvent.LIGHTNING_CHASER_ACCEPT_CHALLENGE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSoundEvent.LIGHTNING_CHASER_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSoundEvent.LIGHTNING_CHASER_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.LIGHTNING_CHASER_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addMagmamuncher(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", SoundEvents.NETHERRACK_STEP.location(), Optional.of(0.25f), Optional.of(0.8f), Optional.empty()));
        sounds.add(new DragonModel.Sound("bite", URSoundEvent.MAGMAMUNCHER_BITE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSoundEvent.MAGMAMUNCHER_AMBIENT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSoundEvent.MAGMAMUNCHER_HURT.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSoundEvent.MAGMAMUNCHER_DEATH.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("apply_fire_resistance", SoundEvents.FIRECHARGE_USE.location(), Optional.empty(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MAGMAMUNCHER_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addEntry(ResourceLocation id, DragonModel variant) {
        holder.add(new Tuple<>(id, variant));
    }

    protected void addEntry(ResourceLocation dragonId, String variant, Optional<List<DragonModel.Sound>> sounds, boolean cull) {
        addEntry(id(dragonId, variant), new DragonModel(getModelData(dragonId, variant, cull), sounds));
    }

    protected void addEntry(EntityType<? extends Entity> entityType, String variant, Optional<List<DragonModel.Sound>> sounds, boolean cull) {
        addEntry(EntityType.getKey(entityType), variant, sounds, cull);
    }

    protected ResourceLocation id(ResourceLocation dragonId, String variant) {
        return ResourceLocation.fromNamespaceAndPath(dragonId.getNamespace(), dragonId.getPath() + "/" + variant);
    }

    @Override
    public @NotNull String getName() {
        return "Dragon Model";
    }
}
