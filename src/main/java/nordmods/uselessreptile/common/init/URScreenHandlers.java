package nordmods.uselessreptile.common.init;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;

public class URScreenHandlers{

    public static ScreenHandlerType<WyvernScreenHandler> WYVERN_INVENTORY = null;
    public static ScreenHandlerType<MoleclawScreenHandler> MOLECLAW_INVENTORY = null;

    public static void init() {
        WYVERN_INVENTORY = Registry.register(Registry.SCREEN_HANDLER,"wyvern_inventory", new ScreenHandlerType<>(WyvernScreenHandler::new));
        MOLECLAW_INVENTORY = Registry.register(Registry.SCREEN_HANDLER,"moleclaw_inventory", new ScreenHandlerType<>(MoleclawScreenHandler::new));
    }
}
