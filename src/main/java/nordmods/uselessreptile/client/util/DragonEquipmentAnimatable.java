package nordmods.uselessreptile.client.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.Item;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Map;

public class DragonEquipmentAnimatable implements GeoAnimatable, AssetCahceOwner {
    public GeoRenderState ownerRenderState;
    public final Item item;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final AssetCache assetCache = new AssetCache();
    public final Map<String, GeoBone> equipmentBones = new Object2ObjectOpenHashMap<>();

    public AssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipmentAnimatable(GeoRenderState ownerRenderState, Item item) {
        this.ownerRenderState = ownerRenderState;
        this.item = item;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<DragonEquipmentAnimatable> idle = new AnimationController<>("idle", URDragonEntity.TRANSITION_TICKS, event -> {
            event.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        });
        controllers.add(idle);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return RenderUtil.getCurrentTick();
    }
}
