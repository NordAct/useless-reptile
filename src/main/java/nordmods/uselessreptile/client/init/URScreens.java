package nordmods.uselessreptile.client.init;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import nordmods.uselessreptile.client.gui.LightningChaserScreen;
import nordmods.uselessreptile.client.gui.MoleclawScreen;
import nordmods.uselessreptile.client.gui.WyvernScreen;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class URScreens {

    public static void init() {
        HandledScreens.register(URScreenHandlers.WYVERN_INVENTORY, WyvernScreen::new);
        HandledScreens.register(URScreenHandlers.MOLECLAW_INVENTORY, MoleclawScreen::new);
        HandledScreens.register(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, LightningChaserScreen::new);
    }
}
