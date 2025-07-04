package nordmods.uselessreptile.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.gui.MagmamuncherScreenHandler;
import org.joml.Vector3f;

public class MagmamuncherScreen extends URDragonScreen<MagmamuncherScreenHandler> {
    public MagmamuncherScreen(MagmamuncherScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        entityRenderSize = 30;
        entityScreenOffset = new Vector3f(0, -0.2f, 0);
    }
}
