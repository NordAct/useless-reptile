package nordmods.uselessreptile.client.util;

import net.minecraft.util.Identifier;

public class AssetCache {
    private Identifier modelLocationCache;
    private Identifier textureLocationCache;
    private Identifier animationLocationCache;
    private Identifier saddleTextureLocationCache;
    private Identifier glowLayerLocationCache;

    public Identifier getModelLocationCache() {
        return modelLocationCache;
    }

    public Identifier getAnimationLocationCache() {
        return animationLocationCache;
    }

    public Identifier getTextureLocationCache() {
        return textureLocationCache;
    }

    public Identifier getSaddleTextureLocationCache() {
        return saddleTextureLocationCache;
    }

    public Identifier getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }

    public void setModelLocationCache(Identifier state) {
        modelLocationCache = state;
    }

    public void setAnimationLocationCache(Identifier state) {
        animationLocationCache = state;
    }

    public void setTextureLocationCache(Identifier state) {
        textureLocationCache = state;
    }

    public void setSaddleTextureLocationCache(Identifier state) {
        saddleTextureLocationCache = state;
    }

    public void setGlowLayerLocationCache(Identifier state) {
        glowLayerLocationCache = state;
    }

    public void cleanCache() {
        setModelLocationCache(null);
        setAnimationLocationCache(null);
        setModelLocationCache(null);
        setSaddleTextureLocationCache(null);
        setGlowLayerLocationCache(null);
    }
}
