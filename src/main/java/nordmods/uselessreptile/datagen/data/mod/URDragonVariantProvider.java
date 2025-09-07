package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.dynamic.Codecs;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.UREntities;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class URDragonVariantProvider implements DataProvider {
    private static final List<Pair<Identifier, DragonVariant>> holder = new ArrayList<>();
    private static final List<Pair<Identifier, DragonVariant>> holderCustomName = new ArrayList<>();
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final DataOutput.PathResolver pathResolverCustomName;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public URDragonVariantProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "uselessreptile/variant");
        this.pathResolverCustomName = output.getResolver(DataOutput.OutputType.DATA_PACK, "uselessreptile/custom_name");
        this.registryLookupFuture = registryLookupFuture;
    }

    public void addEntry(Identifier id, DragonVariant variant) {
        holder.add(new Pair<>(id, variant));
    }

    public void addCustomNameEntry(Identifier id, DragonVariant variant) {
        holderCustomName.add(new Pair<>(id, variant));
    }

    public void addCustomNameEntry(Identifier id, String name) {
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("wyvern/" + name),
                UselessReptile.id("wyvern"),
                Optional.empty(),
                Optional.empty(),
                -1,
                Optional.empty()
        );
        addCustomNameEntry(getId(id, name), variant);
    }


    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(variant -> {
                Path path = pathResolver.resolveJson(variant.getLeft());
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonVariant.CODEC, variant.getRight(), path));
            });
            holderCustomName.forEach(variant -> {
                Path path = pathResolverCustomName.resolveJson(variant.getLeft());
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonVariant.CODEC, variant.getRight(), path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntries() {
        addWyvern("green");
        addWyvern("brown");
        addCustomNameEntry(EntityType.getId(UREntities.WYVERN_ENTITY), "jeb_");

        addMoleclaw("black", false);
        addMoleclaw("brown", false);
        addMoleclaw("grey", false);
        addMoleclaw("albino", true);

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

    protected void addWyvern(String name) {
        Identifier id = EntityType.getId(UREntities.WYVERN_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("wyvern/" + name),
                UselessReptile.id("wyvern"),
                Optional.of(UselessReptile.id("wyvern/default")),
                Optional.empty(),
                128,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new Codecs.TagEntryId(Items.CHICKEN.getRegistryEntry().registryKey().getValue(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2)
                                ),
                                new DragonVariant.TamingItem(
                                        new Codecs.TagEntryId(Items.COOKED_CHICKEN.getRegistryEntry().registryKey().getValue(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 1)
                                )
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addMoleclaw(String name, boolean rare) {
        Identifier id = EntityType.getId(UREntities.MOLECLAW_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("moleclaw/" + name),
                UselessReptile.id("moleclaw"),
                Optional.of(UselessReptile.id("moleclaw/" + (rare ? "rare" : "default"))),
                Optional.empty(),
                64,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new Codecs.TagEntryId(Items.BEETROOT.getRegistryEntry().registryKey().getValue(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 2)
                                )
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addRiverPikehorn(String name) {
        Identifier id = EntityType.getId(UREntities.RIVER_PIKEHORN_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("river_pikehorn/" + name),
                UselessReptile.id("empty"),
                Optional.of(UselessReptile.id("river_pikehorn/default")),
                Optional.empty(),
                1,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new Codecs.TagEntryId(Items.TROPICAL_FISH_BUCKET.getRegistryEntry().registryKey().getValue(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 1)
                                )
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected void addLightningChaser(String name) {
        Identifier id = EntityType.getId(UREntities.LIGHTNING_CHASER_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("lightning_chaser/" + name),
                UselessReptile.id("lightning_chaser"),
                Optional.of(UselessReptile.id("lightning_chaser/" + name)),
                Optional.empty(),
                3,
                Optional.empty()
        );
        addEntry(getId(id, name), variant);
    }

    protected void addMagmamuncher(String name) {
        Identifier id = EntityType.getId(UREntities.MAGMAMUNCHER_ENTITY);
        DragonVariant variant = new DragonVariant(
                id,
                name,
                Optional.empty(),
                UselessReptile.id("magmamuncher/" + name),
                UselessReptile.id("empty"),
                Optional.of(UselessReptile.id("magmamuncher/default")),
                Optional.empty(),
                12,
                Optional.of(
                        List.of(
                                new DragonVariant.TamingItem(
                                        new Codecs.TagEntryId(Items.BLAZE_ROD.getRegistryEntry().registryKey().getValue(), false),
                                        new com.mojang.datafixers.util.Pair<>(1, 3)
                                )
                        )
                )
        );
        addEntry(getId(id, name), variant);
    }

    protected Identifier getId(Identifier dragonId, String name) {
        return Identifier.of(dragonId.getNamespace(), dragonId.getPath() + "/" + name);
    }

    @Override
    public String getName() {
        return "Dragon Variant";
    }
}
