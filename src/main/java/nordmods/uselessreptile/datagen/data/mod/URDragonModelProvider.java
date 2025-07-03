package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.dragon_variant.model.ModelData;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonModelProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final List<Pair<Identifier, DragonModel>> holder = new ArrayList<>();

    public URDragonModelProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "uselessreptile/dragon_model");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addSpawnEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(entry -> {
                Path path = pathResolver.resolveJson(entry.getLeft());
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonModel.CODEC, entry.getRight(), path));
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
    }

    protected ModelData getModelData(Identifier id, String variant, boolean cull) {
        Identifier texture = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/" + variant);
        Identifier model = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/" + id.getPath());
        Identifier animation = Identifier.of(id.getNamespace(), "entity/" + id.getPath() + "/" + id.getPath());
        return new ModelData(texture, model, Optional.of(animation), cull, false);
    }

    protected void addWyvern(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSounds.WYVERN_STEP.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("woosh", URSounds.DRAGON_WOOSH.id(), Optional.of(2f), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENTITY_ENDER_DRAGON_FLAP.id(), Optional.of(3f), Optional.of(0.7f)));
        sounds.add(new DragonModel.Sound("shoot", SoundEvents.ENTITY_ENDER_DRAGON_SHOOT.id(), Optional.of(2f), Optional.empty()));
        sounds.add(new DragonModel.Sound("bite", URSounds.WYVERN_BITE.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSounds.WYVERN_AMBIENT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSounds.WYVERN_HURT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSounds.WYVERN_DEATH.id(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.WYVERN_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addMoleclaw(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSounds.DRAGON_STEP.id(), Optional.empty(), Optional.of(0.7f)));
        sounds.add(new DragonModel.Sound("attack_strong", URSounds.MOLECLAW_STRONG_ATTACK.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("attack", URSounds.MOLECLAW_ATTACK.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSounds.MOLECLAW_AMBIENT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSounds.MOLECLAW_HURT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSounds.MOLECLAW_DEATH.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("panic", URSounds.MOLECLAW_PANICKING.id(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MOLECLAW_ENTITY, variant, Optional.of(sounds), false);
    }

    protected void addRiverPikehorn(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", SoundEvents.ENTITY_CHICKEN_STEP.id(), Optional.of(0.5f), Optional.of(0.8f)));
        sounds.add(new DragonModel.Sound("woosh", URSounds.DRAGON_WOOSH.id(), Optional.of(0.7f), Optional.of(1.2f)));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENTITY_ENDER_DRAGON_FLAP.id(), Optional.empty(), Optional.of(1.2f)));
        sounds.add(new DragonModel.Sound("attack", URSounds.PIKEHORN_ATTACK.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSounds.PIKEHORN_AMBIENT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSounds.PIKEHORN_HURT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSounds.PIKEHORN_DEATH.id(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.RIVER_PIKEHORN_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addLightningChaser(String variant) {
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", URSounds.DRAGON_STEP.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("woosh", URSounds.DRAGON_WOOSH.id(), Optional.of(2f), Optional.empty()));
        sounds.add(new DragonModel.Sound("flap", SoundEvents.ENTITY_ENDER_DRAGON_FLAP.id(), Optional.of(3f), Optional.of(0.6f)));
        sounds.add(new DragonModel.Sound("flap_heavy", SoundEvents.ENTITY_ENDER_DRAGON_FLAP.id(), Optional.of(3f), Optional.of(0.5f)));
        sounds.add(new DragonModel.Sound("bite", URSounds.LIGHTNING_CHASER_BITE.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSounds.LIGHTNING_CHASER_AMBIENT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("roar", URSounds.LIGHTNING_CHASER_DISTANT_ROAR.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("accept_challenge", URSounds.LIGHTNING_CHASER_ACCEPT_CHALLENGE.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSounds.LIGHTNING_CHASER_HURT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSounds.LIGHTNING_CHASER_DEATH.id(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.LIGHTNING_CHASER_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addMagmamuncher(String variant) { //TODO: magmamuncher sounds
        List<DragonModel.Sound> sounds = new ArrayList<>();
        sounds.add(new DragonModel.Sound("step", SoundEvents.ENTITY_CHICKEN_STEP.id(), Optional.of(0.5f), Optional.of(0.8f)));
        sounds.add(new DragonModel.Sound("attack", URSounds.PIKEHORN_ATTACK.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("idle", URSounds.PIKEHORN_AMBIENT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("hurt", URSounds.PIKEHORN_HURT.id(), Optional.empty(), Optional.empty()));
        sounds.add(new DragonModel.Sound("death", URSounds.PIKEHORN_DEATH.id(), Optional.empty(), Optional.empty()));
        addEntry(UREntities.MAGMAMUNCHER_ENTITY, variant, Optional.of(sounds), true);
    }

    protected void addEntry(Identifier id, DragonModel variant) {
        holder.add(new Pair<>(id, variant));
    }

    protected void addEntry(Identifier dragonId, String variant, Optional<List<DragonModel.Sound>> sounds, boolean cull) {
        addEntry(id(dragonId, variant), new DragonModel(getModelData(dragonId, variant, cull), sounds));
    }

    protected void addEntry(EntityType<? extends Entity> entityType, String variant, Optional<List<DragonModel.Sound>> sounds, boolean cull) {
        addEntry(EntityType.getId(entityType), variant, sounds, cull);
    }

    protected Identifier id(Identifier dragonId, String variant) {
        return Identifier.of(dragonId.getNamespace(), dragonId.getPath() + "/" + variant);
    }

    @Override
    public String getName() {
        return "Dragon Model";
    }
}
