package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.util.dragon_variant.DragonVariantLoader;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;
import org.slf4j.Logger;

public class UselessReptile implements ModInitializer, PreLaunchEntrypoint {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "uselessreptile";

    @Override
    public void onInitialize() {
        DragonVariantLoader.init();
        URMobAttributesConfig.init();
        URSounds.init();
        UREntities.init();
        URItems.init();
        URSpawns.init();
        URStatusEffects.init();
        URPotions.init();
        URScreenHandlers.init();
        UREvents.init();

        KeyInputC2SPacket.init();
    }

    @Override
    public void onPreLaunch() {
        URConfig.init();
    }
}