package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URDamageTypeProvider extends URAbstractDataProvider<DamageType> {

    public URDamageTypeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, DamageType.DIRECT_CODEC, Registries.DAMAGE_TYPE.identifier().getPath());
    }

    @Override
    public void addEntries(HolderLookup.Provider provider) {
        addEntry(UselessReptile.id("acid"), new DamageType("acid", DamageScaling.NEVER, 0));
    }

    @Override
    public @NonNull String getName() {
        return "Damage Type";
    }
}
