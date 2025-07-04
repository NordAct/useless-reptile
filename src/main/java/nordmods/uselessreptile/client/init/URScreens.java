package nordmods.uselessreptile.client.init;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import nordmods.uselessreptile.client.gui.*;
import nordmods.uselessreptile.common.gui.LightningChaserScreenHandler;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class URScreens {

    public static void init() {
        HandledScreens.register(URScreenHandlers.WYVERN_INVENTORY, URDragonScreen<WyvernScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.MOLECLAW_INVENTORY, URDragonScreen<MoleclawScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, URDragonScreen<LightningChaserScreenHandler>::new);
        HandledScreens.register(URScreenHandlers.MAGMAMUNCHER_INVENTORY, MagmamuncherScreen::new);
    }
}
