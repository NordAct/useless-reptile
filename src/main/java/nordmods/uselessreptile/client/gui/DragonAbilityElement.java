package nordmods.uselessreptile.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import nordmods.uselessreptile.client.init.URKeyMappings;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class DragonAbilityElement implements HudElement { //todo move positions to config
    private static final int ICON_SIZE = 22;
    private static final int ICON_DISTANCE = 2;
    private static final int CENTER_HUD_OFFSET = 91 + 30;

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().player == null || !(Minecraft.getInstance().player.getVehicle() instanceof URRideableDragonEntity dragon)) return;

        DragonAbilityHolder primary = dragon.getPrimaryRiderAbility();
        DragonAbilityHolder secondary = dragon.getSecondaryRiderAbility();

        primary.getAbility().getCommonAbilityData().icon().ifPresent(icon -> {
            float cooldown = primary.getCooldown() / 20f;
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - ICON_SIZE,
                    graphics.guiHeight() - ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    !primary.getAbility().canUse(primary) || cooldown > 0 ? 0xFFAAAAAA : -1
            );
            if (cooldown > 0) {
                Component cd = Component.literal(String.format("%.1f", cooldown));
                graphics.text(
                        Minecraft.getInstance().font,
                        cd,
                        graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - (ICON_SIZE + Minecraft.getInstance().font.width(cd)) / 2,
                        graphics.guiHeight() - (ICON_SIZE + Minecraft.getInstance().font.lineHeight) / 2,
                        -1,
                        true
                );
            } else {
                Component key = URKeyMappings.PRIMARY_ATTACK_KEY.getTranslatedKeyMessage();
                graphics.text(
                        Minecraft.getInstance().font,
                        key,
                        graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - Minecraft.getInstance().font.width(key),
                        graphics.guiHeight() - Minecraft.getInstance().font.lineHeight,
                        -1,
                        true
                );
            }
        });
        secondary.getAbility().getCommonAbilityData().icon().ifPresent(icon -> {
            float cooldown = secondary.getCooldown() / 20f;
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - ICON_SIZE * 2 - ICON_DISTANCE,
                    graphics.guiHeight() - ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    !secondary.getAbility().canUse(secondary) || cooldown > 0 ? 0xFFAAAAAA : -1
            );
            if (cooldown > 0) {
                Component cd = Component.literal(String.format("%.1f", cooldown));
                graphics.text(
                        Minecraft.getInstance().font,
                        cd,
                        graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - ICON_SIZE - ICON_DISTANCE - (ICON_SIZE + Minecraft.getInstance().font.width(cd)) / 2,
                        graphics.guiHeight() - (ICON_SIZE + Minecraft.getInstance().font.lineHeight) / 2,
                        -1,
                        true
                );
            } else {
                Component key = URKeyMappings.SECONDARY_ATTACK_KEY.getTranslatedKeyMessage();
                graphics.text(
                        Minecraft.getInstance().font,
                        key,
                        graphics.guiWidth() / 2 - CENTER_HUD_OFFSET - ICON_SIZE - ICON_DISTANCE - Minecraft.getInstance().font.width(key),
                        graphics.guiHeight() - Minecraft.getInstance().font.lineHeight,
                        -1,
                        true
                );
            }
        });
    }
}
