package nordmods.sap.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import nordmods.sap.SAP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.typeadapter.BakedAnimationsAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Mixin(value = GeckoLibResources.class, remap = false)
public abstract class GeckoLibResourcesMixin {
    @Shadow
    private static CompletableFuture<Map<Identifier, BakedAnimations>> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager) {
        return null;
    }

    @Shadow
    private static CompletableFuture<Map<Identifier, BakedGeoModel>> loadModels(Executor backgroundExecutor, ResourceManager resourceManager) {
        return null;
    }

    private static Map<Identifier, BakedAnimations> ANIMATIONS_SERVER = Collections.emptyMap();
    private static Map<Identifier, BakedGeoModel> MODELS_SERVER = Collections.emptyMap();


    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    private static void correctSideReload(ResourceReloader.Synchronizer stage, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (SAP.isServerSide()) {
            CompletableFuture<Map<Identifier, BakedAnimations>> animations = loadAnimations(backgroundExecutor, resourceManager);
            CompletableFuture<Map<Identifier, BakedGeoModel>> models = loadModels(backgroundExecutor, resourceManager);

            cir.setReturnValue(CompletableFuture.runAsync(() -> BakedAnimationsAdapter.COMPRESSION_CACHE = new ConcurrentHashMap<>(), backgroundExecutor)
                    .thenCompose(ignored -> CompletableFuture.allOf(animations, models).thenCompose(stage::whenPrepared).thenRunAsync(() -> {
                        ANIMATIONS_SERVER = animations.join();
                        MODELS_SERVER = models.join();
                        BakedAnimationsAdapter.COMPRESSION_CACHE = null;
                    }, gameExecutor)));
        }
    }

    @Inject(method = "getBakedAnimations", at = @At("HEAD"), cancellable = true)
    private static void correctSideAnimation(CallbackInfoReturnable<Map<Identifier, BakedAnimations>> cir) {
        if (SAP.isServerSide()) cir.setReturnValue(ANIMATIONS_SERVER);
    }

    @Inject(method = "getBakedModels", at = @At("HEAD"), cancellable = true)
    private static void correctSideModel(CallbackInfoReturnable<Map<Identifier, BakedGeoModel>> cir) {
        if (SAP.isServerSide()) cir.setReturnValue(MODELS_SERVER);
    }
}
