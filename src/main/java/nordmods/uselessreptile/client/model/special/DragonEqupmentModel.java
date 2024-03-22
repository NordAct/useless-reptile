package nordmods.uselessreptile.client.model.special;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_data.ModelDataUtil;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class DragonEqupmentModel<T extends URDragonEntity> extends GeoModel<T> {
    private final T owner;
    private final Item item;

    public DragonEqupmentModel(T owner, Item item) {
        this.owner = owner;
        this.item = item;
    }

    @Override
    @Nullable
    public Identifier getModelResource(T entity) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(owner, item);
        if (data != null && ResourceUtil.doesExist(data.modelData().model())) return data.modelData().model();

        return null;
    }

    @Override
    @Nullable
    public Identifier getTextureResource(T entity){
        if (!ResourceUtil.isResourceReloadFinished) return null;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(owner, item);
        if (data != null && ResourceUtil.doesExist(data.modelData().texture())) return data.modelData().texture();

        return null;
    }

    //this actually is not used anywhere atm
    @Override
    @Nullable
    public Identifier getAnimationResource(T animatable) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(owner, item);
        if (data != null && ResourceUtil.doesExist(data.modelData().animation())) return data.modelData().animation();

        return null;
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture) {
        EquipmentModelData data = ModelDataUtil.getEquipmentModelData(owner, item);
        if (data != null) return data.modelData().renderType();
        return RenderLayer.getEntityCutoutNoCull(texture);
    }
}
