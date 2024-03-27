package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.client.renderer.layers.BannerLayer;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.renderer.layers.URDragonEquipmentLayer;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class URDragonRenderer <T extends URDragonEntity> extends GeoEntityRenderer<T> {
    public URDragonRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model, boolean hasBanner, boolean hasArmor) {
        super(renderManager, model);
        addRenderLayer(new URGlowingLayer<>(this));
        if (hasBanner) addRenderLayer(new BannerLayer<>(this));
        if (hasArmor) {
            addRenderLayer(new URDragonEquipmentLayer<>(this, EquipmentSlot.HEAD));
            addRenderLayer(new URDragonEquipmentLayer<>(this, EquipmentSlot.CHEST));
            addRenderLayer(new URDragonEquipmentLayer<>(this, EquipmentSlot.LEGS));
        }
    }
}
