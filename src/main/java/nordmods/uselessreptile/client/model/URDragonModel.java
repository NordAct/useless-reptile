package nordmods.uselessreptile.client.model;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.modelRedirect.ModelRedirectUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class URDragonModel<T extends URDragonEntity> extends AnimatedGeoModel<T> {
    public final String dragonID;
    public final String defaultVariant;

    protected URDragonModel(String dragon, String defaultVariant) {
        dragonID = dragon;
        this.defaultVariant = defaultVariant;
    }

    @Override
    public Identifier getAnimationResource(T dragon) {
        if (!ResourceUtil.isResourceReloadFinished) return getDefaultAnimation();

        if (!URConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
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

        if (!URConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
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

        if (!URConfig.getConfig().disableNamedEntityModels && dragon.getCustomName() != null) {
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

}
