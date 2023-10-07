package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.MoleclawEntityModel;
import nordmods.uselessreptile.client.renderer.layers.armor.DragonChestplateLayer;
import nordmods.uselessreptile.client.renderer.layers.armor.DragonHelmetLayer;
import nordmods.uselessreptile.client.renderer.layers.armor.DragonTailArmorLayer;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class MoleclawEntityRenderer extends URRideableDragonRenderer<MoleclawEntity> {
    public MoleclawEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MoleclawEntityModel(), false, false,
                0 , 8.45f, 0.2f, 0, new String[]{"dragon"}, "rider");
        addLayer(new DragonHelmetLayer<>(this));
        addLayer(new DragonChestplateLayer<>(this));
        addLayer(new DragonTailArmorLayer<>(this));
        shadowRadius = 1.25f;
    }

    public void updateSaddle (MoleclawEntity entity) {
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelResource(entity));
        boolean isSaddled = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("saddle_front").ifPresent(c -> c.setHidden(!isSaddled));
        model.getBone("saddle_neck").ifPresent(c -> c.setHidden(!isSaddled));
    }
}
