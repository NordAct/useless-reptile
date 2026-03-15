package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class URDamageTypeProvider implements DataProvider {
    protected final FabricPackOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private static final Set<DamageType> damageTypes = new HashSet<>();

    public URDamageTypeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "damage_type");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            damageTypes.forEach(entry -> {
                Path path = this.pathResolver.json(UselessReptile.id(entry.msgId()));
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DamageType.DIRECT_CODEC, entry, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    private static void addEntries() {
        damageTypes.add(new DamageType("acid", DamageScaling.NEVER, 0));
    }

    @Override
    public @NonNull String getName() {
        return "Damage Type";
    }
}
