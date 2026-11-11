package nordmods.uselessreptile.mixin.client.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.duck.PackSelectionScreenAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin implements PackSelectionScreenAccessor {
    @Unique
    private final List<Pack> invalidPacks = new ArrayList<>();
    @Unique
    private static final String META = UselessReptile.MODID + ".json";
    @Shadow
    @Final
    private PackSelectionModel model;
    @Unique
    private final Map<Pack, VersionInterval> packsWithVersion = new HashMap<>();

    @Inject(method = "populateLists", at = @At("TAIL"))
    private void checkForPacks(PackSelectionModel.EntryBase transferredEntry, CallbackInfo ci) {
        invalidPacks.clear();
        packsWithVersion.clear();
        model.getSelected().forEach(entry -> {
            if (entry instanceof PackSelectionModel.EntryBase entryBase) tryFindMetaFile(entryBase.pack);
        });
        model.getUnselected().forEach(entry -> {
            if (entry instanceof PackSelectionModel.EntryBase entryBase) tryFindMetaFile(entryBase.pack);
        });

    }

    private void tryFindMetaFile(Pack entry) { //todo move checker method somewhere else and use it for already existing packs in world during load too
        try (PackResources pack = entry.open()){
            try {
                IoSupplier<InputStream> supplier = pack.getRootResource(META);
                if (supplier == null) return;

                try (InputStream stream = supplier.get()) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                    String requiredRange = json.get("mod_version").getAsString();
                    String packId = pack.packId();
                    packsWithVersion.put(entry, VersionPredicate.parse(requiredRange).getInterval());
                    try {
                        if (!VersionPredicate.parse(requiredRange).test(UselessReptile.MOD_VERSION)) {
                            invalidPacks.add(entry);
                            UselessReptile.LOGGER.warn("Pack {} requires Useless Reptile version {}, but current is {}", packId, requiredRange, UselessReptile.MOD_VERSION);
                        }
                    } catch (VersionParsingException e) {
                        UselessReptile.LOGGER.warn("Invalid version range in pack {}: {}", packId, requiredRange);
                    }
                }
            } catch (Exception _) {}
        }
    }

    @Override
    public List<Pack> getInvalidPacks() {
        return invalidPacks;
    }

    @Override
    public Map<Pack, VersionInterval> getPacksWithVersion() {
        return packsWithVersion;
    }
}
