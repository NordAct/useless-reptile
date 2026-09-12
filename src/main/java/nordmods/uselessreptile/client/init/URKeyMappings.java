package nordmods.uselessreptile.client.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import nordmods.uselessreptile.UselessReptile;

@Environment(EnvType.CLIENT)
public class URKeyMappings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(UselessReptile.id("main"));
    public static final KeyMapping PRIMARY_ATTACK_KEY = new KeyMapping("key.uselessreptile.primaryAttackKey", InputConstants.KEY_G, CATEGORY);
    public static final KeyMapping SECONDARY_ATTACK_KEY = new KeyMapping("key.uselessreptile.secondaryAttackKey", InputConstants.KEY_V, CATEGORY);
    public static final KeyMapping FLY_DOWN_KEY = new KeyMapping("key.uselessreptile.flyDownKey", InputConstants.KEY_LCONTROL, CATEGORY);
    public static final KeyMapping FREE_LOOK_KEY = new KeyMapping("key.uselessreptile.freeLookKey", InputConstants.KEY_V, CATEGORY);

    public static void init() {
        KeyMappingHelper.registerKeyMapping(FLY_DOWN_KEY);
        KeyMappingHelper.registerKeyMapping(PRIMARY_ATTACK_KEY);
        KeyMappingHelper.registerKeyMapping(SECONDARY_ATTACK_KEY);
        KeyMappingHelper.registerKeyMapping(FREE_LOOK_KEY);
    }
}
