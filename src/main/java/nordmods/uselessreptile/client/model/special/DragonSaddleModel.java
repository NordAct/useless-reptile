package nordmods.uselessreptile.client.model.special;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_redirect.ModelRedirectUtil;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.model.GeoModel;

public class DragonSaddleModel <T extends URRideableDragonEntity> extends GeoModel<T> {

    private final T owner;

    public DragonSaddleModel(T owner) {
        this.owner = owner;
    }

    @Override
    public Identifier getModelResource(T entity) {
        return new Identifier(UselessReptile.MODID, "geo/entity/" + owner.getDragonID() + "/saddle.geo.json");
    }

    @Override
    public Identifier getTextureResource(T entity){
        if (!ResourceUtil.isResourceReloadFinished) {
            owner.getAssetCache().setSaddleTextureLocationCache(null);
            return getDefaultTexture();
        }

        if (owner.getAssetCache().getSaddleTextureLocationCache() != null) return owner.getAssetCache().getSaddleTextureLocationCache();

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && owner.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomSaddleTexturePath(owner, owner.getDragonID());
            if (ResourceUtil.doesExist(id)) {
                owner.getAssetCache().setSaddleTextureLocationCache(id);
                return id;
            }
        }

        id = ModelRedirectUtil.getVariantSaddleTexturePath(owner, owner.getDragonID());
        if (ResourceUtil.doesExist(id)) {
            owner.getAssetCache().setSaddleTextureLocationCache(id);
            return id;
        }

        owner.getAssetCache().setSaddleTextureLocationCache(getDefaultTexture());
        return getDefaultTexture();
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return null;
    }

    private Identifier getDefaultTexture() {
        return new Identifier(UselessReptile.MODID, "textures/entity/" + owner.getDragonID() + "/saddle.png");
    }
}
