package nordmods.uselessreptile.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import org.joml.Vector3f;

public class MagmamuncherScreen extends URDragonScreen<URDragonScreenHandler> {
    public MagmamuncherScreen(URDragonScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        entityRenderSize = 30;
        entityScreenOffset = new Vector3f(0, -0.2f, 0);
    }
}
