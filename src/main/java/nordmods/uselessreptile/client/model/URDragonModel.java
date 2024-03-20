package nordmods.uselessreptile.client.model;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_redirect.ModelRedirectUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public abstract class URDragonModel<T extends URDragonEntity> extends DefaultedEntityGeoModel<T> {
    private final String dragonID;
    private final String defaultVariant;

    protected URDragonModel(String dragon, String defaultVariant) {
        super(new Identifier(UselessReptile.MODID, dragon + "/" + dragon));
        dragonID = dragon;
        this.defaultVariant = defaultVariant;
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) {
            setAnimationLocationCache(entity,null);
            return getDefaultAnimation();
        }

        if (getAnimationLocationCache(entity) != null) return getAnimationLocationCache(entity);

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomAnimationPath(entity, getDragonID());
            if (ResourceUtil.doesExist(id)) {
                setAnimationLocationCache(entity, id);
                return id;
            }
        }

        id = ModelRedirectUtil.getVariantAnimationPath(entity, getDragonID());
        if (ResourceUtil.doesExist(id)) {
            setAnimationLocationCache(entity, id);
            return id;
        }

        setAnimationLocationCache(entity, getDefaultAnimation());
        return getDefaultAnimation();
    }

    @Override
    public Identifier getModelResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) {
            setModelLocationCache(entity,null);
            return getDefaultModel();
        }

        if (getModelLocationCache(entity) != null) return getModelLocationCache(entity);

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomModelPath(entity, getDragonID());
            if (ResourceUtil.doesExist(id)) {
                setModelLocationCache(entity, id);
                return id;
            }
        }

        id = ModelRedirectUtil.getVariantModelPath(entity, getDragonID());
        if (ResourceUtil.doesExist(id)) {
            setModelLocationCache(entity, id);
            return id;
        }

        setModelLocationCache(entity,getDefaultModel());
        return getDefaultModel();
    }

    @Override
    public Identifier getTextureResource(T entity){
        if (!ResourceUtil.isResourceReloadFinished) {
            setTextureLocationCache(entity, null);
            return getDefaultTexture();
        }

        if (getTextureLocationCache(entity) != null) return getTextureLocationCache(entity);

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomTexturePath(entity, getDragonID());
            if (ResourceUtil.doesExist(id)) {
                setTextureLocationCache(entity, id);
                return id;
            }
        }

        id = ModelRedirectUtil.getVariantTexturePath(entity.getVariant(), getDragonID());
        if (ResourceUtil.doesExist(id)) {
            setTextureLocationCache(entity, id);
            return id;
        }

        setTextureLocationCache(entity, getDefaultTexture());
        return getDefaultTexture();
    }

    protected final Identifier getDefaultTexture() {
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ getDragonID() + "/" + defaultVariant + ".png");
    }

    protected final Identifier getDefaultAnimation() {
        return new Identifier(UselessReptile.MODID, "animations/entity/" + getDragonID() + "/" + getDragonID() + ".animation.json");
    }

    protected final Identifier getDefaultModel() {
        return new Identifier(UselessReptile.MODID, "geo/entity/" + getDragonID() + "/" + getDragonID() + ".geo.json");
    }

    public String getDragonID() {
        return dragonID;
    }

    public Identifier getModelLocationCache(T entity) {
        return entity.getAssetCache().getModelLocationCache();
    }

    public Identifier getAnimationLocationCache(T entity) {
        return entity.getAssetCache().getAnimationLocationCache();
    }

    public Identifier getTextureLocationCache(T entity) {
        return entity.getAssetCache().getTextureLocationCache();
    }

    public void setModelLocationCache(T entity, Identifier state) {
        entity.getAssetCache().setModelLocationCache(state);
    }

    public void setAnimationLocationCache(T entity, Identifier state) {
        entity.getAssetCache().setAnimationLocationCache(state);
    }

    public void setTextureLocationCache(T entity, Identifier state) {
        entity.getAssetCache().setTextureLocationCache(state);
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture) {
        return RenderLayer.getEntityCutout(texture);
    }

}
