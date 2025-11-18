package nordmods.uselessreptile.client.util;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class AssetCache {
    private ResourceLocation modelLocationCache;
    private ResourceLocation textureLocationCache;
    private ResourceLocation animationLocationCache;
    private ResourceLocation glowLayerLocationCache;
    private RenderType renderTypeCache;
    private boolean hasGlowing = true;

    public ResourceLocation getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }

    public void setGlowLayerLocationCache(ResourceLocation state) {
        glowLayerLocationCache = state;
    }

    public ResourceLocation getModelLocationCache() {
        return modelLocationCache;
    }

    public void setModelLocationCache(ResourceLocation state) {
        modelLocationCache = state;
    }

    public ResourceLocation getAnimationLocationCache() {
        return animationLocationCache;
    }

    public void setAnimationLocationCache(ResourceLocation state) {
        animationLocationCache = state;
    }

    public ResourceLocation getTextureLocationCache() {
        return textureLocationCache;
    }

    public void setTextureLocationCache(ResourceLocation state) {
        textureLocationCache = state;
    }

    public RenderType getRenderTypeCache() {
        return renderTypeCache;
    }

    public void setRenderTypeCache(RenderType state) {
        renderTypeCache = state;
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
        renderTypeCache = null;
        hasGlowing = true;
    }
}
