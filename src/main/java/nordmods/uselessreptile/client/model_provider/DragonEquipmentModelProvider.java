package nordmods.uselessreptile.client.model_provider;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.EquipmentAssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import org.jetbrains.annotations.Nullable;

public class DragonEquipmentModelProvider implements BRModelProvider {
    @Override
    @Nullable
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
