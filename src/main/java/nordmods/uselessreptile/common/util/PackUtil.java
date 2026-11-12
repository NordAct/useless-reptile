package nordmods.uselessreptile.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PackUtil {
    private static final String META = UselessReptile.MODID + ".json";

    @Nullable
    public static VersionPredicate getModVersionPredicate(Pack pack) {
        try (PackResources resources = pack.open()){
            try {
                IoSupplier<InputStream> supplier = resources.getRootResource(META);
                if (supplier == null) return null;

                try (InputStream stream = supplier.get()) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                    String requiredRange = json.get("mod_version").getAsString();
                    String packId = resources.packId();
                    try {
                        return VersionPredicate.parse(requiredRange);
                    } catch (VersionParsingException e) {
                        UselessReptile.LOGGER.warn("Invalid version range in pack {}: {}", packId, requiredRange);
                        return null;
                    }
                }
            } catch (Exception _) {
                return null;
            }
        }
    }

    public static boolean isPackValid(VersionPredicate modVersionPredicate) {
        return modVersionPredicate.test(UselessReptile.MOD_VERSION);
    }
}
