package nordmods.uselessreptile.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import org.joml.Vector3f;

public class MagmamuncherScreen extends URDragonScreen<URDragonScreenHandler> {
    public MagmamuncherScreen(URDragonScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        entityRenderSize = 30;
        entityScreenOffset = new Vector3f(0, -0.2f, 0);
    }
}
