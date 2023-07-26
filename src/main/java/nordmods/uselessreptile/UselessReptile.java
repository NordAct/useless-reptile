package nordmods.uselessreptile;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.data.DragonVariantLoader;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;

/*todo:
        1) Сделать систему, при которой бы все возможные варианты дракона для спауна перечислялись в файле где-то в data
        2) Запилить речного пикорога
*/

public class UselessReptile implements ModInitializer {
    public static final String MODID = "uselessreptile";
    public static final Identifier EMPTY_TEXTURE = new Identifier(UselessReptile.MODID, "textures/empty.png");

    @Override
    public void onInitialize() {
        DragonVariantLoader.init();
        URConfig.init();
        URSounds.init();
        UREntities.init();
        URItems.init();
        URSpawns.init();
        URStatusEffects.init();
        URPotions.init();
        URScreenHandlers.init();
        KeyInputC2SPacket.init();
    }
}