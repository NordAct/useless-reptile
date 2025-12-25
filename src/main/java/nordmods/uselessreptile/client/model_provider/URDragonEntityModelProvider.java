package nordmods.uselessreptile.client.model_provider;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;

public class URDragonEntityModelProvider implements BRModelProvider {
    @Override
    public Identifier getAnimationId(BRState renderState) {
        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(dragonId);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);

        DragonModelData data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null ) {
            id = data.modelData().animation();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
                return id;
            }
        } else {
            UselessReptile.LOGGER.warn("Failed to find animation for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultAnimation(dragonId);
        assetCache.setAnimationLocationCache(id);
        return id;
    }

    @Override
    public Identifier getModelId(BRState renderState) {
        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(dragonId);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);

        DragonModelData data = DragonVariantUtil.getDragonModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level
        );
        if (data != null ) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
                return id;
            }
        } else {
            UselessReptile.LOGGER.warn("Failed to find model for {} ({}) of variant {}. Default will be used instead",
                    name,
                    dragonId,
                    variant);
        }

        id = getDefaultModel(dragonId);
        assetCache.setModelLocationCache(id);
        return id;
    }

    protected final Identifier getDefaultAnimation(Identifier entity) {
        return UselessReptile.id("biscuit_roll/animations/entity/" + entity.getPath() + "/" + entity.getPath() + ".animation.json");
    }

    protected final Identifier getDefaultModel(Identifier entity) {
        return UselessReptile.id("biscuit_roll/models/entity/" + entity.getPath() + "/" + entity.getPath() + ".geo.json");
    }
}
