package nordmods.uselessreptile.datagen.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class URAbstractDataProvider<T> implements DataProvider {
    protected final FabricPackOutput output;
    protected final PackOutput.PathProvider pathResolver;
    protected final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    protected final Map<Identifier, T> holder = new HashMap<>();
    protected final Codec<T> codec;

    public URAbstractDataProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture, Codec<T> codec, String path) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, path);
        this.registryLookupFuture = registryLookupFuture;
        this.codec = codec;
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
        return registryLookupFuture.thenCompose((provider) -> {
            addEntries(provider);
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach((key, value) -> {
                Path path = pathResolver.json(key);
                list.add(DataProvider.saveStable(cache, provider, codec, value, path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected void addEntry(Identifier id, T entry) {
        holder.put(id, entry);
    }

    public abstract void addEntries(HolderLookup.Provider provider);
}
