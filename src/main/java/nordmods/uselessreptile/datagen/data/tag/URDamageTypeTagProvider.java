package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import nordmods.uselessreptile.common.init.URDamageTypes;

import java.util.concurrent.CompletableFuture;

public class URDamageTypeTagProvider extends FabricTagProvider<DamageType> {
    public URDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptionalElement(URDamageTypes.ACID.identifier());
        getOrCreateRawBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS).addOptionalElement(URDamageTypes.ACID.identifier());
    }
}
