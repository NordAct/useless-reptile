package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.init.*;
import org.slf4j.Logger;

public class UselessReptile implements ModInitializer, PreLaunchEntrypoint {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ErrorReporter.Logging ERROR_REPORTER = new ErrorReporter.Logging(LOGGER);
    public static final String MODID = "uselessreptile";

    @Override
    public void onInitialize() {
        URMobAttributesConfig.init();
        
        URMobAttributesConfig.init();
        URRegistryKeys.init();
        URSounds.init();
        UREntities.init();
        URItems.init();
        URSpawns.init();
        URStatusEffects.init();
        URPotions.init();
        URScreenHandlers.init();
        URGameEvents.init();
        URModEvents.init();
        URPackets.init();
        URRecipeSerializers.init();
        URBlocks.init();
    }

    @Override
    public void onPreLaunch() {
        URConfig.init();
    }

    public static Identifier id(String id) {
        return Identifier.of(MODID, id);
    }
}