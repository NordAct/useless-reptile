package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import nordmods.uselessreptile.UselessReptile;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class URKeybinds {
    public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(UselessReptile.id("main"));
    public static final KeyBinding PRIMARY_ATTACK_KEY = new KeyBinding("key.uselessreptile.primaryAttackKey", GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyBinding SECONDARY_ATTACK_KEY = new KeyBinding("key.uselessreptile.secondaryAttackKey", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyBinding FLY_DOWN_KEY = new KeyBinding("key.uselessreptile.flyDownKey", GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);
    public static final KeyBinding FREE_LOOK_KEY = new KeyBinding("key.uselessreptile.freeLookKey", GLFW.GLFW_KEY_Z, CATEGORY);

    public static void init() {
        KeyBindingHelper.registerKeyBinding(FLY_DOWN_KEY);
        KeyBindingHelper.registerKeyBinding(PRIMARY_ATTACK_KEY);
        KeyBindingHelper.registerKeyBinding(SECONDARY_ATTACK_KEY);
        KeyBindingHelper.registerKeyBinding(FREE_LOOK_KEY);
    }
}
