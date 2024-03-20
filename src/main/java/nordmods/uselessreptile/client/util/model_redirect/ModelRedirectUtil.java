package nordmods.uselessreptile.client.util.model_redirect;

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
        String name = ResourceUtil.parseName(dragon);
        String model = ModelRedirectUtil.getModel(dragonID, name, true);
        if (model.contains(":")) return new Identifier(model);
        return new Identifier(UselessReptile.MODID, "geo/entity/" + dragonID + "/" + model);
    }

    public static Identifier getVariantModelPath(URDragonEntity dragon, String dragonID) {
        String model = ModelRedirectUtil.getModel(dragonID, dragon.getVariant(), false);
        if (model.contains(":")) return new Identifier(model);
        return new Identifier(UselessReptile.MODID, "geo/entity/" + dragonID + "/" + model);
    }

    public static String getModel(String dragon, String name, boolean viaNameTag) {
       if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name)) {
           ModelRedirect modelRedirect = dragonModelRedirects.get(dragon).get(name);
           return modelRedirect.animation() == null || (!modelRedirect.nametagAccessible() && viaNameTag) ?
                   ".json" : modelRedirect.model();
       }
       else return ".json";
    }

    public static Identifier getCustomAnimationPath(URDragonEntity dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String animation = ModelRedirectUtil.getAnimation(dragonID, name, true);
        if (animation.contains(":")) return new Identifier(animation);
        return new Identifier(UselessReptile.MODID, "animations/entity/" + dragonID + "/" + animation);
    }

    public static Identifier getVariantAnimationPath(URDragonEntity dragon, String dragonID) {
        String animation = ModelRedirectUtil.getAnimation(dragonID, (dragon).getVariant(), false);
        if (animation.contains(":")) return new Identifier(animation);
        return new Identifier(UselessReptile.MODID, "animations/entity/" + dragonID + "/" + animation);
    }

    public static String getAnimation(String dragonID, String name, boolean viaNameTag) {
        if (dragonModelRedirects.containsKey(dragonID) && dragonModelRedirects.get(dragonID).containsKey(name)) {
            ModelRedirect modelRedirect = dragonModelRedirects.get(dragonID).get(name);
            return modelRedirect.animation() == null || (!modelRedirect.nametagAccessible() && viaNameTag) ?
                    ".json" : modelRedirect.animation();
        }
        else return ".json";
    }

    public static Identifier getCustomTexturePath(URDragonEntity dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String texture = getMainTexture(dragonID, name, true);
        if (texture.contains(":")) return new Identifier(texture);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + dragonID + "/" + texture);
    }

    public static Identifier getVariantTexturePath(String variant, String dragonID) {
        String texture = getMainTexture(dragonID, variant, false);
        if (texture.contains(":")) return new Identifier(texture);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + dragonID + "/" + texture);
    }

    public static String getMainTexture(String dragonID, String name, boolean viaNameTag) {
        if (dragonModelRedirects.containsKey(dragonID) && dragonModelRedirects.get(dragonID).containsKey(name)) {
            ModelRedirect modelRedirect = dragonModelRedirects.get(dragonID).get(name);
            return !modelRedirect.nametagAccessible() && viaNameTag ?
                    ".png" : modelRedirect.texture();
        }
        else return name + ".png";
    }

    public static Identifier getCustomSaddleTexturePath(URDragonEntity dragon, String dragonID) {
        String name = ResourceUtil.parseName(dragon);
        String texture = ModelRedirectUtil.getSaddleTexture(dragonID, name, true);
        if (texture.contains(":")) return new Identifier(texture);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + dragonID + "/" + texture);
    }

    public static Identifier getVariantSaddleTexturePath(URDragonEntity dragon, String dragonID) {
        String texture = ModelRedirectUtil.getSaddleTexture(dragonID, (dragon).getVariant(), false);
        if (texture.contains(":")) return new Identifier(texture);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + dragonID + "/" + texture);
    }

    public static String getSaddleTexture(String dragon, String name, boolean viaNameTag) {
        if (dragonModelRedirects.containsKey(dragon) && dragonModelRedirects.get(dragon).containsKey(name)) {
            ModelRedirect modelRedirect = dragonModelRedirects.get(dragon).get(name);
            return modelRedirect.saddle() == null || (!modelRedirect.nametagAccessible() && viaNameTag) ?
                    ".png" : modelRedirect.saddle();
        }
        else return ".png";
    }

    public static synchronized void add(String dragon, Map<String, ModelRedirect> redirects) {
        Map<String, ModelRedirect> content = dragonModelRedirects.get(dragon);
        if (content != null) {
            content.putAll(redirects);
            dragonModelRedirects.put(dragon, content);
        } else dragonModelRedirects.put(dragon, redirects);
    }

    //todo add config
    public static void debugPrint() {
        for (Map.Entry<String, Map<String, ModelRedirect>> entry : dragonModelRedirects.entrySet()) {
            for ( Map.Entry<String, ModelRedirect> redirects : entry.getValue().entrySet()) {
                    UselessReptile.LOGGER.info("{}: variant/name {} was redirected to {}", entry.getKey(), redirects.getKey(), redirects.getValue());
            }
        }
    }
}
