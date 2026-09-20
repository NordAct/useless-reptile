package nordmods.uselessreptile.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URKeyMappings;
import nordmods.uselessreptile.common.dragon_ability.NoopAbility;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class DragonAbilityElement implements HudElement {
    private static final int ICON_SIZE = 22;

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().player == null || !(Minecraft.getInstance().player.getVehicle() instanceof URRideableDragonEntity dragon)) return;

        DragonAbilityHolder primary = dragon.getPrimaryRiderAbility();
        DragonAbilityHolder secondary = dragon.getSecondaryRiderAbility();

        if (!(primary.getAbility() instanceof NoopAbility)) primary.getAbility().getCommonAbilityData().icon().ifPresent(icon -> {
            float cooldown = primary.getCooldown() / 20f;
            int baseXOffset = graphics.guiWidth() / 2 + URClientConfig.getConfig().abilityIconOffsetsPreset.getPrimaryAbilityIconOffsetX();
            int baseYOffset = graphics.guiHeight() + URClientConfig.getConfig().abilityIconOffsetsPreset.getPrimaryAbilityIconOffsetY();
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    baseXOffset - ICON_SIZE,
                    baseYOffset - ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    !primary.getAbility().canUse(primary) || cooldown > 0 ? 0xFFAAAAAA : -1
            );
            if (cooldown > 0) {
                Component cd = Component.literal(String.format("%.1f", cooldown));
                graphics.text(
                        Minecraft.getInstance().font,
                        cd,
                        baseXOffset - (ICON_SIZE + Minecraft.getInstance().font.width(cd)) / 2,
                        baseYOffset - (ICON_SIZE + Minecraft.getInstance().font.lineHeight) / 2,
                        -1,
                        true
                );
            } else {
                Component key = URKeyMappings.PRIMARY_ABILITY_KEY.getTranslatedKeyMessage();
                graphics.text(
                        Minecraft.getInstance().font,
                        key,
                        baseXOffset- Minecraft.getInstance().font.width(key),
                        baseYOffset - Minecraft.getInstance().font.lineHeight,
                        -1,
                        true
                );
            }
        });

        if (!(secondary.getAbility() instanceof NoopAbility)) secondary.getAbility().getCommonAbilityData().icon().ifPresent(icon -> {
            float cooldown = secondary.getCooldown() / 20f;
            int baseXOffset = graphics.guiWidth() / 2 + URClientConfig.getConfig().abilityIconOffsetsPreset.getSecondaryAbilityIconOffsetX();
            int baseYOffset = graphics.guiHeight() + URClientConfig.getConfig().abilityIconOffsetsPreset.getSecondaryAbilityIconOffsetY();
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    baseXOffset - ICON_SIZE,
                    baseYOffset - ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    !secondary.getAbility().canUse(secondary) || cooldown > 0 ? 0xFFAAAAAA : -1
            );
            if (cooldown > 0) {
                Component cd = Component.literal(String.format("%.1f", cooldown));
                graphics.text(
                        Minecraft.getInstance().font,
                        cd,
                        baseXOffset - (ICON_SIZE + Minecraft.getInstance().font.width(cd)) / 2,
                        baseYOffset - (ICON_SIZE + Minecraft.getInstance().font.lineHeight) / 2,
                        -1,
                        true
                );
            } else {
                Component key = URKeyMappings.SECONDARY_ABILITY_KEY.getTranslatedKeyMessage();
                graphics.text(
                        Minecraft.getInstance().font,
                        key,
                        baseXOffset - Minecraft.getInstance().font.width(key),
                        baseYOffset - Minecraft.getInstance().font.lineHeight,
                        -1,
                        true
                );
            }
        });
    }

    public enum AbilityIconOffsetPreset {
        RIGHT(121, 0, 145, 0),
        RIGHT_WITH_OFFHAND_OFFSET(121 + 29, 0, 145 + 29, 0),
        LEFT(-121 + 29, 0, -145 + 29, 0),
        LEFT_WITH_OFFHAND_OFFSET(-121, 0, -145, 0),
        CUSTOM(0, 0, 0, 0)
        ;
        private final int primaryAbilityIconOffsetX;
        private final int primaryAbilityIconOffsetY;
        private final int secondaryAbilityIconOffsetX;
        private final int secondaryAbilityIconOffsetY;

        AbilityIconOffsetPreset(int primaryAbilityIconOffsetX, int primaryAbilityIconOffsetY, int secondaryAbilityIconOffsetX, int secondaryAbilityIconOffsetY) {
            this.primaryAbilityIconOffsetX = primaryAbilityIconOffsetX;
            this.primaryAbilityIconOffsetY = primaryAbilityIconOffsetY;
            this.secondaryAbilityIconOffsetX = secondaryAbilityIconOffsetX;
            this.secondaryAbilityIconOffsetY = secondaryAbilityIconOffsetY;
        }

        public int getPrimaryAbilityIconOffsetX() {
            return this == CUSTOM ? URClientConfig.getConfig().primaryAbilityIconOffsetX : primaryAbilityIconOffsetX;
        }

        public int getPrimaryAbilityIconOffsetY() {
            return this == CUSTOM ? URClientConfig.getConfig().primaryAbilityIconOffsetY : primaryAbilityIconOffsetY;
        }

        public int getSecondaryAbilityIconOffsetX() {
            return this == CUSTOM ? URClientConfig.getConfig().secondaryAbilityIconOffsetX : secondaryAbilityIconOffsetX;
        }

        public int getSecondaryAbilityIconOffsetY() {
            return this == CUSTOM ? URClientConfig.getConfig().secondaryAbilityIconOffsetY : secondaryAbilityIconOffsetY;
        }
    }
}
