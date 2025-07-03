package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import nordmods.uselessreptile.common.init.URDamageTypes;

import java.util.concurrent.CompletableFuture;

public class URDamageTypeTagProvider extends FabricTagProvider<DamageType> {
    public URDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(DamageTypeTags.BYPASSES_ARMOR).addOptional(URDamageTypes.ACID.getValue());
        getTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS).addOptional(URDamageTypes.ACID.getValue());
    }
}
