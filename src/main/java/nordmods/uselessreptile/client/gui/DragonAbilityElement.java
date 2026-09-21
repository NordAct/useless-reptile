package nordmods.uselessreptile.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URKeyMappings;
import nordmods.uselessreptile.common.dragon_ability.type.NoopAbility;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.jspecify.annotations.NonNull;

public class DragonAbilityElement implements HudElement {
    private static final int ICON_SIZE = 22;

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().player == null || !(Minecraft.getInstance().player.getVehicle() instanceof URRideableDragonEntity dragon)) return;

        DragonAbilityHolder primary = dragon.getPrimaryRiderAbility();
        DragonAbilityHolder secondary = dragon.getSecondaryRiderAbility();

        renderIcon(
                graphics,
                primary,
                URClientConfig.getConfig().abilityIconOffsetsPreset.getPrimaryAbilityIconOffsetX(),
                URClientConfig.getConfig().abilityIconOffsetsPreset.getPrimaryAbilityIconOffsetY(),
                URKeyMappings.PRIMARY_ABILITY_KEY.getTranslatedKeyMessage()
        );
        renderIcon(
                graphics,
                secondary,
                URClientConfig.getConfig().abilityIconOffsetsPreset.getSecondaryAbilityIconOffsetX(),
                URClientConfig.getConfig().abilityIconOffsetsPreset.getSecondaryAbilityIconOffsetY(),
                URKeyMappings.SECONDARY_ABILITY_KEY.getTranslatedKeyMessage()
        );
    }

    private static void renderIcon(GuiGraphicsExtractor graphics, DragonAbilityHolder abilityHolder, int xOffset, int yOffset, Component hotkey) {
        if (!(abilityHolder.getAbility() instanceof NoopAbility)) abilityHolder.getAbility().getCommonAbilityData().icon().ifPresent(icon -> {
            float cooldown = abilityHolder.getCooldown() / 20f;
            int baseXOffset = graphics.guiWidth() / 2 + xOffset;
            int baseYOffset = graphics.guiHeight() + yOffset;
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    baseXOffset - ICON_SIZE,
                    baseYOffset - ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    !abilityHolder.getAbility().canUse(abilityHolder) || cooldown > 0 ? 0xFFAAAAAA : -1
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
                graphics.text(
                        Minecraft.getInstance().font,
                        hotkey,
                        baseXOffset- Minecraft.getInstance().font.width(hotkey),
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
