package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class LightningChaserScreenHandler extends URDragonScreenHandler{
    public LightningChaserScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, syncId, playerInventory, inventory, StorageSize.MEDIUM, true, true, true);
    }

    public LightningChaserScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(maxStorageSize));
    }
}
