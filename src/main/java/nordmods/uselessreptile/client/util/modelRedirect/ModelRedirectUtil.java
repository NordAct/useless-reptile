package nordmods.uselessreptile.client.util.modelRedirect;

import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ModelRedirectUtil {
    //key - dragon id
    //value - redirects per name/variant
    public static final Map<String, Map<String, ModelRedirect>> dragonModelRedirects = new HashMap<>();

    public static Identifier getCustomModelPath(URDragonEntity dragon, String dragonID) {
        String model = ModelRedirectUtil.getModel(dragonID, ResourceUtil.parseName(dragon), true);
        return new Identifier(UselessReptile.MODID,
                "geo/entity/" + dragonID + "/" + model);
    }

    public static Identifier getVariantModelPath(URDragonEntity dragon, String dragonID) {
        String model = ModelRedirectUtil.getModel(dragonID, dragon.getVariant(), false);
        return new Identifier(UselessReptile.MODID,
                "geo/entity/" + dragonID + "/" + model);
    }

    public static String getModel(String dragon, String name, boolean viaNameTag) {
       if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name)) {
           ModelRedirect modelRedirect = dragonModelRedirects.get(dragon).get(name);
           return modelRedirect.animation() == null || (!modelRedirect.nameTagAccessible() && viaNameTag) ?
                   ".json" : modelRedirect.model();
       }
       else return ".json";
    }

    public static Identifier getCustomAnimationPath(URDragonEntity dragon, String dragonID) {
        String animation = ModelRedirectUtil.getAnimation(dragonID, ResourceUtil.parseName(dragon), true);
        return new Identifier(UselessReptile.MODID,
                "animations/entity/" + dragonID + "/" + animation);
    }

    public static Identifier getVariantAnimationPath(URDragonEntity dragon, String dragonID) {
        String animation = ModelRedirectUtil.getAnimation(dragonID, (dragon).getVariant(), false);
        return new Identifier(UselessReptile.MODID,
                "animations/entity/" + dragonID + "/" + animation);
    }

    public static String getAnimation(String dragon, String name, boolean viaNameTag) {
        if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name)) {
            ModelRedirect modelRedirect = dragonModelRedirects.get(dragon).get(name);
            return modelRedirect.animation() == null || (!modelRedirect.nameTagAccessible() && viaNameTag) ?
                    ".json" : modelRedirect.animation();
        }
        else return ".json";
    }

    public static Identifier getCustomTexturePath(URDragonEntity dragon, String id) {
        String name = ResourceUtil.parseName(dragon);
        if (dragonModelRedirects.containsKey(id) && dragonModelRedirects.get(id).containsKey(name)) {
            ModelRedirect modelRedirect = dragonModelRedirects.get(id).get(name);
            if (!modelRedirect.nameTagAccessible()) name = "";
        }
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ id + "/" + name +".png");
    }

    public static Identifier getVariantTexturePath(String variant, String id) {
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ id + "/" + variant + ".png");
    }

    public static synchronized void add(String dragon, Map<String, ModelRedirect> redirects) {
        Map<String, ModelRedirect> content = dragonModelRedirects.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            dragonModelRedirects.put(dragon, content);
        } else dragonModelRedirects.put(dragon, redirects);
    }

    public static void debugPrint() {
        for (Map.Entry<String, Map<String, ModelRedirect>> entry : dragonModelRedirects.entrySet()) {
            for ( Map.Entry<String, ModelRedirect> redirects : entry.getValue().entrySet()) {
                    UselessReptile.LOGGER.debug("{}: variant/name {} was redirected to {}", entry.getKey(), redirects.getKey(), redirects.getValue());
            }
        }
    }
}
