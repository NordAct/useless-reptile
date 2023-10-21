package nordmods.uselessreptile.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.gui.LightningChaserScreenHandler;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public class LightningChaserScreen extends URDragonScreen<LightningChaserScreenHandler>{
    public LightningChaserScreen(LightningChaserScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        hasArmor = true;
        hasSaddle = true;
        hasBanner = true;
        storageSize = URDragonScreenHandler.StorageSize.MEDIUM;
    }
}
