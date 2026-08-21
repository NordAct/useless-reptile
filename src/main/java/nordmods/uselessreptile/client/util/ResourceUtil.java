package nordmods.uselessreptile.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.resource_managers.ClientAnimationManager;
import nordmods.biscuit_roll.client.resource_managers.ClientModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.uselessreptile.UselessReptile;

public class ResourceUtil {
    public static boolean isResourceReloadFinished;

    //note: very resource intense, try to avoid repetitive calls
    //note 2: does not recognize models and animations sent by server. Use doesAnimationExist and doesModelExist instead
    public static boolean doesResourceExist(Identifier id, boolean logWarning) {
        boolean exists = id != null && Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
        if (!exists && logWarning) UselessReptile.LOGGER.warn("Unable to find resource {}. Are you sure it exists?", id);
        return exists;
    }

    public static boolean doesResourceExist(Identifier id) {
        return doesResourceExist(id, true);
    }

    //todo move checks to library
    public static boolean doesAnimationExist(Identifier id, boolean isClient, boolean logWarning) {
        boolean exists = isClient ? ClientAnimationManager.instance().getRegistry().containsKey(id) || ServerAnimationManager.instance().getRegistry().containsKey(id)
                : ServerAnimationManager.instance().getRegistry().containsKey(id);
        if (!exists && logWarning) UselessReptile.LOGGER.warn("Unable to find animation {}. Are you sure it exists?", id);
        return exists;
    }

    public static boolean doesAnimationExist(Identifier id, boolean isClient) {
        return doesAnimationExist(id, isClient, true);
    }

    public static boolean doesModelExist(Identifier id, boolean isClient, boolean logWarning) {
        boolean exists = isClient ? ClientModelManager.instance().getRegistryRaw().containsKey(id) || ServerModelManager.instance().getRegistryRaw().containsKey(id)
                : ServerModelManager.instance().getRegistryRaw().containsKey(id);
        if (!exists && logWarning) UselessReptile.LOGGER.warn("Unable to find model {}. Are you sure it exists?", id);
        return exists;
    }

    public static boolean doesModelExist(Identifier id, boolean isClient) {
        return doesModelExist(id, isClient, true);
    }
}
