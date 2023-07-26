package nordmods.uselessreptile.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;

public class WyvernScreen extends URDragonScreen<WyvernScreenHandler> {
    public WyvernScreen(WyvernScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        hasArmor = false;
        hasSaddle = true;
        hasBanner = true;
        storageSize = URDragonScreenHandler.StorageSize.SMALL;
    }
}
