package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
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
    public static final Version MOD_VERSION = FabricLoader.getInstance().getModContainer(UselessReptile.MODID).get().getMetadata().getVersion();

    @Override
    public void onInitialize() {
        URMobAttributesConfig.init();

        URRegistries.init();
        URFluteModes.init();
        URCommands.init();
        URUseConditionTypes.init();
        URDragonVariantTypes.init();
        URDragonAbilityTypes.init();
        URAttributes.init();
        URResourceKeys.init();
        URSoundEvents.init();
        UREntities.init();
        URItemComponents.init();
        URItems.init();
        URSpawns.init();
        URMobEffects.init();
        URPotions.init();
        URGameEvents.init();
        URModEvents.init();
        URPayloads.init();
        URRecipeSerializers.init();
        URBlocks.init();
        URBlockEntityTypes.init();
        UREntityDataSerializers.init();
        URDamageTypes.init();
        URAttachments.init();
    }

    @Override
    public void onPreLaunch() {
        URConfig.init();
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }
}