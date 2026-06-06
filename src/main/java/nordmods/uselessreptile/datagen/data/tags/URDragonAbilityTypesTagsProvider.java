package nordmods.uselessreptile.datagen.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URDragonAbilityTypesTagsProvider extends FabricTagsProvider<DragonAbilityType<?>> {
    public URDragonAbilityTypesTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, URResourceKeys.DRAGON_ABILITY_TYPE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        getOrCreateRawBuilder(URTags.ATTACK)
                .addTag(URTags.MELEE_ATTACK.location())
                .addTag(URTags.RANGED_ATTACK.location());

        getOrCreateRawBuilder(URTags.MELEE_ATTACK)
                .addElement(URDragonAbilityTypes.MELEE_ATTACK.getId())
                .addElement(URDragonAbilityTypes.BLOCK_BREAKING_MELEE_ATTACK_ABILITY.getId());

        getOrCreateRawBuilder(URTags.RANGED_ATTACK)
                .addElement(URDragonAbilityTypes.SHOT_ATTACK.getId())
                .addElement(URDragonAbilityTypes.LIGHTNING_BREATH_ATTACK.getId());

        getOrCreateRawBuilder(URTags.DEFENSIVE_ABILITY)
                .addElement(URDragonAbilityTypes.SHOCKWAVE_ATTACK.getId());
    }
}
