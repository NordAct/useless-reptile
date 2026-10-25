package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.*;
import nordmods.uselessreptile.client.util.CurrentFluteModeProperty;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.init.URAttachments;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SelectItemModelProperties.ID_MAPPER.put(UselessReptile.id("current_flute_mode"), CurrentFluteModeProperty.TYPE);
        URAtlases.init();
        URClientConfig.init();
        URRenderers.init();
        URRenderPipelines.init();
        URKeyMappings.init();
        URClientPayloadHandlers.init();
        URHudElements.init();

        ClientEntityEvents.ENTITY_LOAD.register((entity, _) -> {
            if (entity instanceof URDragonEntity dragon && dragon instanceof BREntityWithAnimationProcessor<?> animationProcessorOwner) {
                entity.onAttachedSet(URAttachments.EQUIPMENT_CONTROLLER_STATES).register((_, newValue) -> {
                    if (newValue != null) {
                        newValue.forEach((equipmentSlot, controllerStates) -> {
                            DragonEquipment equipment = dragon.getAssetCache().getEquipment(equipmentSlot);
                            if (equipment != null) {
                                ControllerState.applyControllerStates(controllerStates, equipment.getAnimationControllers());
                                equipment.cloneAnimationController.copyFrom(dragon);
                                equipment.markAnimationUpdated();
                                if (equipment instanceof BREntityWithAnimationProcessor<?> processorOwner) processorOwner.onAnimationControllersUpdated();
                            }
                        });
                    }
                });
            }
        });
    }
}