package nordmods.uselessreptile.common.util.duck;

import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jspecify.annotations.Nullable;

public interface PackModVersionPredicateStorage {
    @Nullable VersionPredicate useless_reptile$getModVersionPredicate();
    void useless_reptile$setModVersionPredicate(VersionPredicate versionPredicate);
    boolean useless_reptile$modVersionMismatch();
    void useless_reptile$setModVersionMismatch(boolean mismatch);
}
