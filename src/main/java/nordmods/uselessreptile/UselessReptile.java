package nordmods.uselessreptile;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.init.*;
import org.slf4j.Logger;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UselessReptile implements ModInitializer, PreLaunchEntrypoint {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "uselessreptile";
    public static DataTicket<Double> ANIMATION_TICKS = DataTicket.create("ur_animation_tick", Double.class);//TODO MOVE TO OTHER PROJECT
    public static DataTicket<Double> BONE_RESET_TIME = DataTicket.create("ur_bone_reset_time", Double.class);//TODO MOVE TO OTHER PROJECT

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

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)//TODO MOVE TO OTHER PROJECT
                .registerReloadListener(new IdentifiableResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return UselessReptile.id("geckolib_server_resources");
                    }

                    @Override
                    public CompletableFuture<Void> reload(Synchronizer preparationBarrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
                        return GeckoLibResources.reload(preparationBarrier, resourceManager, backgroundExecutor, gameExecutor);
                    }
                });
    }

    @Override
    public void onPreLaunch() {
        URConfig.init();
    }

    public static Identifier id(String id) {
        return Identifier.of(MODID, id);
    }
}