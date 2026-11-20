package nordmods.uselessreptile.common.entity.model_provider;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URStateDataTypes;
import nordmods.uselessreptile.common.asset_cache.AssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;

import java.util.HashMap;
import java.util.Map;

public class URDragonEntityModelProvider implements BRModelProvider {
    private static final Map<Identifier, Identifier> DEFAULT_MODELS = new HashMap<>();
    private static final Map<Identifier, Identifier> DEFAULT_ANIMATIONS = new HashMap<>();
    @Override
    public Identifier getAnimationId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(renderState.getStateData(URStateDataTypes.DRAGON_ID));
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getAnimationLocationCache();
    }

    @Override
    public Identifier getModelId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(renderState.getStateData(URStateDataTypes.DRAGON_ID));
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getModelLocationCache();
    }

    public final Identifier getDefaultAnimation(Identifier entity) {
        return DEFAULT_ANIMATIONS.computeIfAbsent(entity, id -> UselessReptile.id("biscuit_roll/animations/entity/" + id.getPath() + "/" + id.getPath() + ".animation.json"));
    }

    public final Identifier getDefaultModel(Identifier entity) {
        return DEFAULT_MODELS.computeIfAbsent(entity, id -> UselessReptile.id("biscuit_roll/models/entity/" + id.getPath() + "/" + id.getPath() + ".geo.json"));
    }
}
