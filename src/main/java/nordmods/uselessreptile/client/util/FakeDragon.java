package nordmods.uselessreptile.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.client.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.URDragonEntityModelProvider;
import nordmods.uselessreptile.client.renderer.base.URDragonEntityRenderer;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.util.SimpleAnimationController;

import java.util.Collection;
import java.util.List;

public class FakeDragon implements AssetCahceOwner, BRAnimatedObject {
    private AABB modelAABB;
    private final SimpleAnimationController controller = new SimpleAnimationController(false) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final List<BRAnimationController> controllers = List.of(controller);
    private final DragonAssetCache assetCache = new DragonAssetCache();
    private DragonVariantType<?> variantType;
    private DragonVariant[] dragonVariants;
    private int currentVariantOrdinal;
    private static final URDragonEntityModelProvider MODEL_PROVIDER = new URDragonEntityModelProvider();
    private boolean needsAnimationUpdate;
    private static final DragonVariantType<?>[] DRAGON_VARIANT_TYPES = URRegistries.VARIANT_TYPE.stream().toArray(DragonVariantType[]::new);

    public FakeDragon(DragonVariantType<?> variantType, String variantName) {
        setVariantType(variantType);
        for (currentVariantOrdinal = 0; currentVariantOrdinal < dragonVariants.length; currentVariantOrdinal++) {
            if (dragonVariants[currentVariantOrdinal].common().name().equals(variantName)) break;
        }
        //it's there because otherwise first AABB calculation happens when model is t-posing
        createDragonRenderState();
        clearModel();
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    @Override
    public DragonAssetCache getAssetCache() {
        return assetCache;
    }

    public DragonVariantType<?> getVariantType() {
        return variantType;
    }

    public void setVariantType(DragonVariantType<?> variantType) {
        currentVariantOrdinal = 0;
        this.variantType = variantType;
        updateVariants();
        clearModel();
        //it's there because otherwise first AABB calculation happens when model is t-posing
        createDragonRenderState();
        clearModel();
    }

    protected void updateVariants() {
        dragonVariants = DragonVariant.getSameType(variantType, Minecraft.getInstance().level).toArray(new DragonVariant[0]);
    }

    protected float getAnimationTime(float tickDelta) {
        return (Minecraft.getInstance().player.tickCount + tickDelta) / 20f;
    }

    public void nextVariant() {
        currentVariantOrdinal += 1;
        if (currentVariantOrdinal >= dragonVariants.length) currentVariantOrdinal = 0;
        clearModel();
    }

    public void previousVariant() {
        currentVariantOrdinal -= 1;
        if (currentVariantOrdinal < 0) currentVariantOrdinal = dragonVariants.length - 1;
        clearModel();
    }

    public void nextType() {
        int current;
        for (current = 0; current < DRAGON_VARIANT_TYPES.length; current++) {
            if (DRAGON_VARIANT_TYPES[current] == variantType) break;
        }
        current++;
        if (current >= DRAGON_VARIANT_TYPES.length) setVariantType(DRAGON_VARIANT_TYPES[0]);
        else setVariantType(DRAGON_VARIANT_TYPES[current]);
    }

    public void previousType() {
        int current;
        for (current = 0; current < DRAGON_VARIANT_TYPES.length; current++) {
            if (DRAGON_VARIANT_TYPES[current] == variantType) break;
        }
        current--;
        if (current < 0) setVariantType(DRAGON_VARIANT_TYPES[DRAGON_VARIANT_TYPES.length - 1]);
        else setVariantType(DRAGON_VARIANT_TYPES[current]);
    }

    protected void clearModel() {
        assetCache.cleanCache();
        needsAnimationUpdate = true;
        modelAABB = null;
    }

    public DragonVariant getDragonVariant() {
        return dragonVariants[currentVariantOrdinal];
    }

    public LivingEntityRenderState createDragonRenderState() {
        LivingEntityRenderState renderState = new LivingEntityRenderState();
        float tickDelta = RenderUtil.getTickDelta(false);

        DragonAssetCache assetCache = getAssetCache();
        Identifier dragonId = getVariantType().getId();
        URDragonEntityRenderer.fillDragonCache(
                assetCache,
                getDragonVariant(),
                dragonId,
                Component.translatable(getDragonVariant().getType().getTranslationKey()).getString(),
                getDragonVariant().common().name(),
                URDragonEntityRenderer.getDefaultTexture(dragonId),
                MODEL_PROVIDER.getDefaultModel(dragonId),
                MODEL_PROVIDER.getDefaultAnimation(dragonId)
        );

        renderState.setStateData(URStateDataTypes.ASSET_CACHE, assetCache);
        renderState.setStateData(URStateDataTypes.DRAGON_ID, dragonId);
        renderState.setStateData(StateDataTypes.CONTROLLERS, getAnimationControllers());
        renderState.setStateData(StateDataTypes.MODEL_PROVIDER, MODEL_PROVIDER);

        if (modelAABB == null) {
            modelAABB = computeModelAABB(ClientModelManager.instance().getModel(MODEL_PROVIDER.getModelId(renderState)), renderState);
        }
        renderState.boundingBoxHeight = (float) modelAABB.getYsize();
        renderState.boundingBoxWidth = (float) Math.max(modelAABB.getXsize(), modelAABB.getZsize());
        renderState.setStateData(StateDataTypes.SCALE, Math.max(renderState.boundingBoxHeight, renderState.boundingBoxWidth));
        renderState.entityType = BuiltInRegistries.ENTITY_TYPE.getValue(dragonId);

        renderState.setStateData(StateDataTypes.ANIMATION_TIME, getAnimationTime(tickDelta));

        if (needsAnimationUpdate) {
            updateAnimation(renderState);
            needsAnimationUpdate = false;
        }

        return renderState;
    }

    private void updateAnimation(LivingEntityRenderState renderState) {
        controller.clearAnimations();
        controller.update(renderState);
        controller.playAnimation("idle");
        controller.playAnimation("blink");
    }

    private AABB computeModelAABB(BRModel model, LivingEntityRenderState state) {
        AABB.Builder builder = new AABB.Builder();
        state.setStateData(StateDataTypes.ANIMATION_ADJUSTMENT, ((_, _) -> {}));
        model.applyAnimations(state);
        nordmods.biscuit_roll.client.util.RenderUtil.getExtentsForGui(model, new PoseStack(), builder::include);
        return builder.isDefined() ? builder.build() : AABB.ofSize(Vec3.ZERO, 0, 0, 0);
    }
}
