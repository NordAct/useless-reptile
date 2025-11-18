package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import nordmods.uselessreptile.UselessReptile;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class URKeyMappings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(UselessReptile.id("main"));
    public static final KeyMapping PRIMARY_ATTACK_KEY = new KeyMapping("key.uselessreptile.primaryAttackKey", GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping SECONDARY_ATTACK_KEY = new KeyMapping("key.uselessreptile.secondaryAttackKey", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping FLY_DOWN_KEY = new KeyMapping("key.uselessreptile.flyDownKey", GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);
    public static final KeyMapping FREE_LOOK_KEY = new KeyMapping("key.uselessreptile.freeLookKey", GLFW.GLFW_KEY_Z, CATEGORY);

    public static void init() {
        KeyBindingHelper.registerKeyBinding(FLY_DOWN_KEY);
        KeyBindingHelper.registerKeyBinding(PRIMARY_ATTACK_KEY);
        KeyBindingHelper.registerKeyBinding(SECONDARY_ATTACK_KEY);
        KeyBindingHelper.registerKeyBinding(FREE_LOOK_KEY);
    }
}
