package nordmods.uselessreptile.common.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;

public class URScreenHandlers{

    public static ScreenHandlerType<WyvernScreenHandler> WYVERN_INVENTORY = null;
    public static ScreenHandlerType<MoleclawScreenHandler> MOLECLAW_INVENTORY = null;

    public static void init() {
        WYVERN_INVENTORY = Registry.register(Registries.SCREEN_HANDLER,"wyvern_inventory", new ScreenHandlerType<>(WyvernScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
        MOLECLAW_INVENTORY = Registry.register(Registries.SCREEN_HANDLER,"moleclaw_inventory", new ScreenHandlerType<>(MoleclawScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    }
}
