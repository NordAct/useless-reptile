package nordmods.uselessreptile.client.init;

import net.minecraft.client.gui.screens.MenuScreens;
import nordmods.uselessreptile.client.gui.MagmamuncherScreen;
import nordmods.uselessreptile.client.gui.RiverPikehornScreen;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.init.URMenus;

public class URScreens {
    public static void init() {
        MenuScreens.register(URMenus.WYVERN_INVENTORY, URDragonScreen<URDragonMenu>::new);
        MenuScreens.register(URMenus.MOLECLAW_INVENTORY, URDragonScreen<URDragonMenu>::new);
        MenuScreens.register(URMenus.LIGHTNING_CHASER_INVENTORY, URDragonScreen<URDragonMenu>::new);
        MenuScreens.register(URMenus.RIVER_PIKEHORN_INVENTORY, RiverPikehornScreen::new);
        MenuScreens.register(URMenus.MAGMAMUNCHER_INVENTORY, MagmamuncherScreen::new);
    }
}
