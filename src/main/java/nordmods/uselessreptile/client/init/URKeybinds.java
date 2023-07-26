package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class URKeybinds {
    public static KeyBinding primaryAttackKey;
    public static KeyBinding secondaryAttackKey;
    public static KeyBinding flyDownKey;

    public static void init() {
        flyDownKey = new KeyBinding("key.uselessreptile.flyDownKey", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.uselessreptile");
        primaryAttackKey = new KeyBinding("key.uselessreptile.primaryAttackKey", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category.uselessreptile");
        secondaryAttackKey = new KeyBinding("key.uselessreptile.secondaryAttackKey", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.uselessreptile");

        KeyBindingHelper.registerKeyBinding(flyDownKey);
        KeyBindingHelper.registerKeyBinding(primaryAttackKey);
        KeyBindingHelper.registerKeyBinding(secondaryAttackKey);
    }
}
