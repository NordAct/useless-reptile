package nordmods.uselessreptile.client.init;

import net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class URAtlases {
    public static final Identifier ANIMATED_TEXTURES_ATLAS_ID = UselessReptile.id("animated_dragon_textures");
    public static final Identifier ANIMATED_TEXTURES_SHEET = AtlasRegistry.generateTextureLocation(ANIMATED_TEXTURES_ATLAS_ID);
    public static final SpriteMapper ANIMATED_TEXTURES_ATLAS_MAPPER = new SpriteMapper(ANIMATED_TEXTURES_SHEET, "entity/" + ANIMATED_TEXTURES_ATLAS_ID.getPath());
    public static void init() {
        AtlasRegistry.register(new AtlasManager.AtlasConfig(ANIMATED_TEXTURES_SHEET, ANIMATED_TEXTURES_ATLAS_ID, false));
    }
}
