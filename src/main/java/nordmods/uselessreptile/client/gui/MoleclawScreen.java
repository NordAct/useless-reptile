package nordmods.uselessreptile.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;


public class MoleclawScreen extends URDragonScreen<MoleclawScreenHandler> {

    public MoleclawScreen(MoleclawScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        hasArmor = true;
        hasSaddle = true;
        hasBanner = true;
        storageSize = URDragonScreenHandler.StorageSize.LARGE;
    }
}
