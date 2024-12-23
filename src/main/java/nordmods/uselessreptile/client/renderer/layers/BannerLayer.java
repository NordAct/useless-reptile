package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class BannerLayer<T extends DragonEquipmentAnimatable> extends BlockAndItemGeoLayer<T> {
    public BannerLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Nullable
    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {
        return bone.getName().equals("banner") ? animatable.owner.getEquippedStack(EquipmentSlot.OFFHAND) : null;
    }
}
