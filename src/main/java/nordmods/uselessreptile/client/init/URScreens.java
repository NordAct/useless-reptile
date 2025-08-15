package nordmods.uselessreptile.client.init;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import nordmods.uselessreptile.client.gui.MagmamuncherScreen;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class URScreens {

    public static void init() {
        HandledScreens.register(URScreenHandlers.WYVERN_INVENTORY, URDragonScreen<URDragonScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.MOLECLAW_INVENTORY, URDragonScreen<URDragonScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, URDragonScreen<URDragonScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.MAGMAMUNCHER_INVENTORY, MagmamuncherScreen::new);
    }
}
