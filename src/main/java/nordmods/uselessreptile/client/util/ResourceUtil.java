package nordmods.uselessreptile.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class ResourceUtil {
    public static boolean isResourceReloadFinished;

    //note: very resource intense, try to avoid repetitive calls
    //note 2: does not recognize models and animations sent by server. Use Biscuit Roll's built-in checkers instead
    public static boolean doesResourceExist(Identifier id, boolean logWarning) {
        boolean exists = id != null && Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
        if (!exists && logWarning) UselessReptile.LOGGER.warn("Unable to find resource {}. Are you sure it exists?", id);
        return exists;
    }

    public static boolean doesResourceExist(Identifier id) {
        return doesResourceExist(id, true);
    }
}
