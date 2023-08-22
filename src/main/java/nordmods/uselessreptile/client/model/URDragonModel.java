package nordmods.uselessreptile.client.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public abstract class URDragonModel<T extends URDragonEntity> extends DefaultedEntityGeoModel<T> {

    public final String dragonName;
    public final String defaultVariant;

    protected URDragonModel(String dragonName, String defaultVariant) {
        super(new Identifier(UselessReptile.MODID, dragonName + "/" + dragonName));
        this.dragonName = dragonName;
        this.defaultVariant = defaultVariant;
    }

    @Override
    public Identifier getAnimationResource(T dragon) {
        return new Identifier(UselessReptile.MODID, "animations/" + dragonName + ".animation.json");
    }

    @Override
    public Identifier getModelResource(T dragon) {
        return new Identifier(UselessReptile.MODID, "geo/"+ dragonName +".geo.json");
    }

    @Override
    public Identifier getTextureResource(T dragon){
        if (!URConfig.getConfig().disableNamedTextures && dragon.getCustomName() != null) {
            Identifier id = getCustomTexturePath(dragon);
            if (MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent()) return id;
        }
        Identifier id = new Identifier(UselessReptile.MODID, "textures/entity/"+ dragonName + "/" + dragon.getVariant() + ".png");
        if (MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent()) return id;
        return getDefaultVariant();
    }

    protected Identifier getDefaultVariant(){
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ dragonName + "/" + defaultVariant + ".png");
    }

    protected Identifier getCustomTexturePath(T dragon) {
        String name = dragon.getName().getString().toLowerCase();
        name = name.replace(" ", "_");
        name = replaceCyrillic(name);
        if (!name.matches("^[a-zA-Z0-9_]+$")) name = "";
        return new Identifier(UselessReptile.MODID, "textures/entity/"+ dragonName + "/" + name + ".png");
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture) {
        return RenderLayer.getEntityCutout(texture);
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

    public static String replaceCyrillic(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            String l = text.substring(i, i+1);
            sb.append(letters.getOrDefault(l, l));
        }
        return sb.toString();
    }
}
