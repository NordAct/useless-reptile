package nordmods.uselessreptile.client.model.special;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_redirect.ModelRedirectUtil;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.special.RenderOnlyEntity;
import software.bernie.geckolib.model.GeoModel;

public class DragonSaddleModel <E extends URRideableDragonEntity, T extends RenderOnlyEntity<E>> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(T entity) {
        return new Identifier(UselessReptile.MODID, "geo/entity/" + entity.owner.getDragonID() + "/saddle.geo.json");
    }

    @Override
    public Identifier getTextureResource(T entity){
        if (!ResourceUtil.isResourceReloadFinished) {
            entity.owner.getAssetCache().setSaddleTextureLocationCache(null);
            return getDefaultTexture(entity);
        }

        if (entity.owner.getAssetCache().getSaddleTextureLocationCache() != null) return entity.owner.getAssetCache().getSaddleTextureLocationCache();

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.owner.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomSaddleTexturePath(entity.owner, entity.owner.getDragonID());
            if (ResourceUtil.doesExist(id)) {
                entity.owner.getAssetCache().setSaddleTextureLocationCache(id);
                return id;
            }
        }

        id = ModelRedirectUtil.getVariantSaddleTexturePath(entity.owner, entity.owner.getDragonID());
        if (ResourceUtil.doesExist(id)) {
            entity.owner.getAssetCache().setSaddleTextureLocationCache(id);
            return id;
        }

        entity.owner.getAssetCache().setSaddleTextureLocationCache(getDefaultTexture(entity));
        return getDefaultTexture(entity);
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return null;
    }

    private Identifier getDefaultTexture(T entity) {
        return new Identifier(UselessReptile.MODID, "textures/entity/" + entity.owner.getDragonID() + "/saddle.png");
    }
}
