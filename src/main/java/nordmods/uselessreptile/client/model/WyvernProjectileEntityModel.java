package nordmods.uselessreptile.client.model;

import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.WyvernProjectileEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class WyvernProjectileEntityModel extends AnimatedGeoModel<WyvernProjectileEntity> {
    @Override
    public Identifier getModelResource(WyvernProjectileEntity object) {
        return new Identifier(UselessReptile.MODID, "geo/entity/wyvern_proj/wyvern_proj.geo.json");
    }

    @Override
    public Identifier getTextureResource(WyvernProjectileEntity object) {
        return new Identifier(UselessReptile.MODID, "textures/entity/wyvern_proj/wyvern_proj.png");
    }

    @Override
    public Identifier getAnimationResource(WyvernProjectileEntity animatable) {
        return new Identifier(UselessReptile.MODID, "animations/entity/wyvern_proj/wyvern_proj.animation.json");
    }
}
