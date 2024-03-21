package nordmods.uselessreptile.client.util;

import net.minecraft.client.MinecraftClient;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {
    //check if resource reload is finished because Minecraft acknowledges new resources before Geckolib does, which leads to bad stuff
    public static boolean isResourceReloadFinished;

    public static String parseName(URDragonEntity dragon) {
        if (!dragon.hasCustomName()) return "";
        String name = dragon.getName().getString().toLowerCase();
        name = name.replace(" ", "_");
        name = replaceCyrillic(name);
        if (!name.matches("^[a-zA-Z0-9_]+$")) name = "";
        return name;
    }

    public static boolean doesExist(Identifier id) {
        return  id != null && MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
    }

    private static final Map<String, String> letters = new HashMap<>();
    static {
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "yo");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "j");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "c");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "shch");
        letters.put("ь", "");
        letters.put("ы", "y");
        letters.put("ъ", "");
        letters.put("э", "e");
        letters.put("ю", "yu");
        letters.put("я", "ya");
    }

    private static String replaceCyrillic(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            String l = text.substring(i, i+1);
            sb.append(letters.getOrDefault(l, l));
        }
        return sb.toString();
    }
}
