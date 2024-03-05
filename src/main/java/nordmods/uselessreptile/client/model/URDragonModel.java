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
    public final String dragonID;
    public final String defaultVariant;

    protected URDragonModel(String dragon, String defaultVariant) {
        super(new Identifier(UselessReptile.MODID, dragon + "/" + dragon));
        dragonID = dragon;
        this.defaultVariant = defaultVariant;
    }

    @Override
    public Identifier getAnimationResource(T dragon) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation();

        if (!URClientConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
            Identifier id = ModelRedirectUtil.getCustomAnimationPath(dragon, dragonID);
            if (ResourceUtil.doesExist(id)) return id;
        }

        Identifier id = ModelRedirectUtil.getVariantAnimationPath(dragon, dragonID);
        if (ResourceUtil.doesExist(id)) return id;

        return getDefaultAnimation();
    }

    @Override
    public Identifier getModelResource(T dragon) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultModel();

        if (!URClientConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
            Identifier id = ModelRedirectUtil.getCustomModelPath(dragon, dragonID);
            if (ResourceUtil.doesExist(id)) return id;
        }

        Identifier id = ModelRedirectUtil.getVariantModelPath(dragon, dragonID);
        if (ResourceUtil.doesExist(id)) return id;

        return getDefaultModel();
    }

    @Override
    public Identifier getTextureResource(T dragon){
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultTexture();

        if (!URClientConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
            Identifier id = ModelRedirectUtil.getCustomTexturePath(dragon, dragonID);
            if (ResourceUtil.doesExist(id)) return id;
        }

        Identifier id = ModelRedirectUtil.getVariantTexturePath(dragon.getVariant(), dragonID);
        if (ResourceUtil.doesExist(id)) return id;

        return getDefaultTexture();
    }

    protected final Identifier getDefaultTexture() {
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ dragonID + "/" + defaultVariant + ".png");
    }

    protected final Identifier getDefaultAnimation() {
        return new Identifier(UselessReptile.MODID, "animations/entity/" + dragonID + "/" + dragonID + ".animation.json");
    }

    protected final Identifier getDefaultModel() {
        return new Identifier(UselessReptile.MODID, "geo/entity/" + dragonID + "/" + dragonID + ".geo.json");
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture) {
        return RenderLayer.getEntityCutout(texture);
    }

}
