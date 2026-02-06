package nordmods.uselessreptile.client.model_provider;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.asset_cache.AssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;
import org.jspecify.annotations.Nullable;

public class DragonEquipmentModelProvider implements BRModelProvider {
    @Override
    public Identifier getModelId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(renderState.getStateData(URStateDataTypes.DRAGON_ID));
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getModelLocationCache();
    }

    @Override
    public Identifier getAnimationId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(renderState.getStateData(URStateDataTypes.DRAGON_ID));
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getAnimationLocationCache();
    }

    public final Identifier getDefaultAnimation(Identifier entity) {
        return UselessReptile.id("biscuit_roll/animations/entity/" + entity.getPath() + "/empty.animation.json");
    }

    public final Identifier getDefaultModel(Identifier entity) {
        return UselessReptile.id("biscuit_roll/models/entity/" + entity.getPath() + "/" + entity.getPath() + ".geo.json");
    }
}
