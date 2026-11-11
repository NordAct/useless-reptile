package nordmods.uselessreptile.client.util.duck;

import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.minecraft.server.packs.repository.Pack;

import java.util.List;
import java.util.Map;

public interface PackSelectionScreenAccessor {
    List<Pack> getInvalidPacks();
    Map<Pack, VersionInterval> getPacksWithVersion();
}
