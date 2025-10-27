package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class UREntityTypeTagProvider extends FabricTagProvider<EntityType<?>>{
    public UREntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(URTags.DRAGON_IMMUNE)
                .add(EntityType.getId(EntityType.ITEM));
    }
}
