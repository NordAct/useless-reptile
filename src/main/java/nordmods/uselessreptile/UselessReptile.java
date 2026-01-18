package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.init.*;
import org.slf4j.Logger;

public class UselessReptile implements ModInitializer, PreLaunchEntrypoint {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ProblemReporter.ScopedCollector ERROR_REPORTER = new ProblemReporter.ScopedCollector(LOGGER);
    public static final String MODID = "uselessreptile";

    @Override
    public void onInitialize() {
        URMobAttributesConfig.init();
        
        URMobAttributesConfig.init();
        URResourceKeys.init();
        URSoundEvent.init();
        UREntities.init();
        URItems.init();
        URSpawns.init();
        URMobEffect.init();
        URPotions.init();
        URGameEvents.init();
        URModEvents.init();
        URPayloads.init();
        URRecipeSerializers.init();
        URBlocks.init();
        UREntityDataSerializers.init();
    }

    @Override
    public void onPreLaunch() {
        URConfig.init();
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }
}