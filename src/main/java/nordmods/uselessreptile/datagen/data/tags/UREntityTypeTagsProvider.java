package nordmods.uselessreptile.datagen.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class UREntityTypeTagsProvider extends FabricTagsProvider<EntityType<?>>{
    public UREntityTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(URTags.DRAGON_IMMUNE)
                .addElement(EntityType.getKey(EntityTypes.ITEM));

        getOrCreateRawBuilder(URTags.DRAGON)
                .addElement(EntityType.getKey(UREntities.MAGMAMUNCHER))
                .addElement(EntityType.getKey(UREntities.WYVERN))
                .addElement(EntityType.getKey(UREntities.MOLECLAW))
                .addElement(EntityType.getKey(UREntities.LIGHTNING_CHASER))
                .addElement(EntityType.getKey(UREntities.RIVER_PIKEHORN));
    }
}
