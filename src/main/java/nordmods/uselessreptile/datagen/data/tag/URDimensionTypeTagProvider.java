package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class URDimensionTypeTagProvider extends FabricTagProvider<DimensionType> {
    public URDimensionTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DIMENSION_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(URTags.DEPLETED_MAGMA_REGENERATES)
                .add(DimensionTypes.THE_NETHER_ID);
    }
}
