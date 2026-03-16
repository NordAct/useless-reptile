package nordmods.uselessreptile.datagen.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URDimensionTypeTagsProvider extends FabricTagsProvider<DimensionType> {
    public URDimensionTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DIMENSION_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(URTags.DEPLETED_MAGMA_REGENERATES)
                .addElement(BuiltinDimensionTypes.NETHER.identifier());
    }
}
