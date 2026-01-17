package nordmods.uselessreptile.client.asset_cache;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;

public abstract class AssetCache {
    private Identifier modelLocationCache;
    private Identifier textureLocationCache;
    private Identifier animationLocationCache;
    private Identifier glowLayerLocationCache;
    private BRModelSubmitStorage.RenderTypeProvider renderTypeProviderCache;
    private boolean hasGlowing = true;

    public Identifier getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }

    public void setGlowLayerLocationCache(Identifier state) {
        glowLayerLocationCache = state;
    }

    public Identifier getModelLocationCache() {
        return modelLocationCache;
    }

    public void setModelLocationCache(Identifier state) {
        modelLocationCache = state;
    }

    public Identifier getAnimationLocationCache() {
        return animationLocationCache;
    }

    public void setAnimationLocationCache(Identifier state) {
        animationLocationCache = state;
    }

    public Identifier getTextureLocationCache() {
        return textureLocationCache;
    }

    public void setTextureLocationCache(Identifier state) {
        textureLocationCache = state;
    }

    public BRModelSubmitStorage.RenderTypeProvider getRenderTypeProviderCache() {
        return renderTypeProviderCache;
    }

    public void setRenderTypeProviderCache(BRModelSubmitStorage.RenderTypeProvider state) {
        renderTypeProviderCache = state;
    }

    public boolean hasGlowing() {
        return hasGlowing;
    }
    public void setHasGlowing(boolean state) {
        hasGlowing = state;
    }

    public void cleanCache() {
        modelLocationCache = null;
        textureLocationCache = null;
        animationLocationCache = null;
        glowLayerLocationCache = null;
        renderTypeProviderCache = null;
        hasGlowing = true;
    }
}
