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
        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel(dragonId);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        if (assetCache == null) return getDefaultModel(dragonId);

        Identifier id = assetCache.getModelLocationCache();
        if (id != null) return id;

        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);
        Identifier itemId = BuiltInRegistries.ITEM.getKey(renderState.getStateData(URStateDataTypes.EQUIPMENT_ITEM_STACK).getItem());
        EquipmentModelData.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null) {
            id = data.modelData().model();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setModelLocationCache(id);
                return id;
            } else {
                UselessReptile.LOGGER.warn("Failed to find model for equipment ({}) for {} ({}) of variant {}",
                        itemId,
                        name,
                        dragonId,
                        variant);
            }
        }

        ((EquipmentAssetCache)assetCache).setCanRender(false);
        assetCache.setModelLocationCache(getDefaultModel(dragonId));
        return getDefaultModel(dragonId);
    }

    @Override
    public Identifier getAnimationId(BRState renderState) {
        Identifier dragonId = renderState.getStateData(URStateDataTypes.DRAGON_ID);

        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation(dragonId);

        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        if (assetCache == null) return getDefaultAnimation(dragonId);

        Identifier id = assetCache.getAnimationLocationCache();
        if (id != null) return id;

        String name = renderState.getStateData(URStateDataTypes.DRAGON_NAME).getString();
        String variant = renderState.getStateData(URStateDataTypes.DRAGON_VARIANT);
        Identifier itemId = BuiltInRegistries.ITEM.getKey(renderState.getStateData(URStateDataTypes.EQUIPMENT_ITEM_STACK).getItem());
        EquipmentModelData.Equipment data = DragonVariantUtil.getEquipmentModelData(
                dragonId,
                name,
                variant,
                Minecraft.getInstance().level,
                itemId
        );
        if (data != null) {
            id = data.modelData().animation();
            if (ResourceUtil.doesExist(id)) {
                assetCache.setAnimationLocationCache(id);
                return id;
            } else {
                UselessReptile.LOGGER.warn("Failed to find animation for equipment ({}) for {} ({}) of variant {}",
                        itemId,
                        name,
                        dragonId,
                        variant);
            }
        }
        assetCache.setAnimationLocationCache(getDefaultAnimation(dragonId));
        return getDefaultAnimation(dragonId);
    }

    protected final Identifier getDefaultAnimation(Identifier entity) {
        return UselessReptile.id("biscuit_roll/animations/entity/" + entity.getPath() + "/empty.animation.json");
    }

    protected final Identifier getDefaultModel(Identifier entity) {
        return UselessReptile.id("biscuit_roll/models/entity/" + entity.getPath() + "/" + entity.getPath() + ".geo.json");
    }
}
