package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.data.DragonVariantLoader;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.AttackPartOwnerC2SPacket;
import nordmods.uselessreptile.common.network.InteractPartOwnerC2SPacket;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;
import org.slf4j.Logger;

public class UselessReptile implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "uselessreptile";
    public static final Identifier EMPTY_TEXTURE = new Identifier(UselessReptile.MODID, "textures/empty.png");

    @Override
    public void onInitialize() {
        DragonVariantLoader.init();
        URConfig.init();
        UREvents.init();
        URSounds.init();
        UREntities.init();
        URItems.init();
        URSpawns.init();
        URStatusEffects.init();
        URPotions.init();
        URScreenHandlers.init();

        KeyInputC2SPacket.init();
        AttackPartOwnerC2SPacket.init();
        InteractPartOwnerC2SPacket.init();
    }
}