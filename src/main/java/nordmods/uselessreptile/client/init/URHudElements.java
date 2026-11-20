package nordmods.uselessreptile.client.init;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.gui.DragonAbilityElement;

public class URHudElements {
    public static final HudElement DRAGON_ABILITIES = new DragonAbilityElement();
    public static void init() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.MOB_EFFECTS, UselessReptile.id("dragon_abilities"), DRAGON_ABILITIES);
    }
}
