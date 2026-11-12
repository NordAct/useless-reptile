package nordmods.uselessreptile.mixin.common.pack;

import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.util.PackUtil;
import nordmods.uselessreptile.common.util.duck.PackModVersionPredicateStorage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Checks pack compat with mod
@Mixin(Pack.class)
public abstract class PackMixin implements PackModVersionPredicateStorage {
    @Shadow
    public abstract String getId();

    @Unique
    private VersionPredicate modVersionPredicate;
    @Unique
    private boolean modVersionMismatch;

    @Inject(method = "readMetaAndCreate", at = @At("RETURN"))
    private static void verifyModVersion(PackLocationInfo location, Pack.ResourcesSupplier resources, PackType packType, PackSelectionConfig selectionConfig, CallbackInfoReturnable<Pack> cir) {
        Pack pack = cir.getReturnValue();
        if (pack == null) return;

        VersionPredicate predicate = PackUtil.getModVersionPredicate(pack);
        if (predicate == null) return;
        ((PackModVersionPredicateStorage)pack).useless_reptile$setModVersionPredicate(predicate);
        if (!PackUtil.isPackValid(predicate)) {
            ((PackModVersionPredicateStorage) pack).useless_reptile$setModVersionMismatch(true);
            UselessReptile.LOGGER.warn("Pack {} requires Useless Reptile version {}, but current is {}. This pack may fail to load", pack.getId(), predicate, UselessReptile.MOD_VERSION);
        }
    }

    @Override
    public @Nullable VersionPredicate useless_reptile$getModVersionPredicate() {
        return modVersionPredicate;
    }

    @Override
    public void useless_reptile$setModVersionPredicate(VersionPredicate versionPredicate) {
        modVersionPredicate = versionPredicate;
    }

    @Override
    public boolean useless_reptile$modVersionMismatch() {
        return modVersionMismatch;
    }

    @Override
    public void useless_reptile$setModVersionMismatch(boolean mismatch) {
        modVersionMismatch = mismatch;
    }
}
