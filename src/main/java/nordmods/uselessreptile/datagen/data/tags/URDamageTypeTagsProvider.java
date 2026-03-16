package nordmods.uselessreptile.datagen.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import nordmods.uselessreptile.common.init.URDamageTypes;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URDamageTypeTagsProvider extends FabricTagsProvider<DamageType> {
    public URDamageTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptionalElement(URDamageTypes.ACID.identifier());
        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS).addOptionalElement(URDamageTypes.ACID.identifier());
    }
}
