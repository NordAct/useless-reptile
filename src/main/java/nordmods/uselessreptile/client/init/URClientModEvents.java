package nordmods.uselessreptile.client.init;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.init.URAttachments;
import nordmods.uselessreptile.common.init.URTags;

public class URClientModEvents {
    private static final AttackRange RANGE = new AttackRange(0, 100, 0, 100, 0.3f, 1f);
    public static void init() {
        //syncable controller states
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

        //spyglass info display
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if (player == null) return;
            if (!player.isUsingItem() || !player.getUseItem().is(URTags.DISPLAYS_DRAGON_INFO_ON_USE)) return;

            HitResult lookingAtResult = RANGE.getClosesetHit(player, 1, EntitySelector.CAN_BE_PICKED);
            if (lookingAtResult == null || lookingAtResult.getType() != HitResult.Type.ENTITY) return;
            if (((EntityHitResult)lookingAtResult).getEntity() instanceof URDragonEntity dragon) {
                MutableComponent message = Component.empty();
                message.append(dragon.getTypeName());
                message.append(Component.literal(" | "));
                message.append(Component.translatable(
                        "variant.uselessreptile",
                        dragon.isInvalidVariant() ?
                                Component.literal("UNREGISTERED VARIANT (" + dragon.getVariant() + ")").withStyle(ChatFormatting.DARK_RED) :
                                Component.translatable(dragon.getDragonVariant().common().variantNameKey()).withStyle(ChatFormatting.GOLD)
                ));
                message.append(Component.literal(" | "));
                if (dragon.isTame()) {
                    message.append(Component.translatable("variant.uselessreptile.tamed"));
                } else {
                    if (dragon.isTameable()) {
                        float progress = (float) (dragon.getBaseTamingProgress() - dragon.getTamingProgress()) * 100f / (float) Math.max(dragon.getBaseTamingProgress(), 1);
                        ChatFormatting color;
                        if (progress >= 100) color = ChatFormatting.AQUA;
                        else if (progress > 66.66667f) color = ChatFormatting.GREEN;
                        else if (progress > 33.333334f) color = ChatFormatting.YELLOW;
                        else color = ChatFormatting.RED;
                        message.append(Component.translatable("variant.uselessreptile.taming_progress",
                                Component.literal(String.format("%.1f", progress))
                                        .withStyle(color)
                                        .append("%")
                        ));
                    } else message.append(Component.translatable("variant.uselessreptile.untameable"));
                }
                player.sendOverlayMessage(message);
            }
        });
    }
}
