package nordmods.uselessreptile.client.renderer.fake_dragon;

import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.uselessreptile.client.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.common.util.SimpleAnimationController;

import java.util.Collection;
import java.util.List;

public class FakeDragon implements AssetCahceOwner, BRAnimatedObject {
    private final SimpleAnimationController idleController = new SimpleAnimationController(true) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final List<BRAnimationController> controllers = List.of(idleController);
    private final DragonAssetCache assetCache = new DragonAssetCache();


    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    @Override
    public DragonAssetCache getAssetCache() {
        return assetCache;
    }
}
