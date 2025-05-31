package nordmods.sap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
//S - serverside
//A - animation
//P - processor
//(I don't think I supposed to do this, but I really want those damn entity parts to be in sync with animation)
public class SAP implements ModInitializer, PreLaunchEntrypoint {
    public static DataTicket<Double> ANIMATION_TICKS = DataTicket.create("sap_animation_tick", Double.class);
    public static DataTicket<Double> BONE_RESET_TIME = DataTicket.create("sap_bone_reset_time", Double.class);
    public static DataTicket<Collection> PROCESSABLE_BONES = DataTicket.create("sap_processable_bones", Collection.class); //string set

    @Override
    public void onInitialize() {

        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new IdentifiableResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of("sap","geckolib_server_resources");
                    }

                    @Override
                    public CompletableFuture<Void> reload(Synchronizer preparationBarrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
                        return GeckoLibResources.reload(preparationBarrier, resourceManager, backgroundExecutor, gameExecutor);
                    }
                });
    }

    @Override
    public void onPreLaunch() {
        //exists for sake of correct load order
    }

    //idk better way to do that
    public static boolean isServerSide() {
        return !Thread.currentThread().getName().equals("Render thread");
    }
}
