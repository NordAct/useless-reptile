package nordmods.uselessreptile.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class ResourceUtil {
    //check if resource reload is finished because Minecraft acknowledges new resources before Geckolib does, which leads to bad stuff
    public static boolean isResourceReloadFinished;

    //note: very resource intense, try to avoid repetitive calls
    public static boolean doesExist(Identifier id, boolean logWarning) {
        boolean exists = id != null && MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
        if (!exists && logWarning) UselessReptile.LOGGER.warn("Unable to find {}. Are you sure it exists?", id);
        return exists;
    }

    public static boolean doesExist(Identifier id) {
        return doesExist(id, true);
    }
}
