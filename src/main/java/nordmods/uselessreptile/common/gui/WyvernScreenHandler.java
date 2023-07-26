package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class WyvernScreenHandler extends URDragonScreenHandler {

    public WyvernScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.WYVERN_INVENTORY, syncId, playerInventory, inventory, StorageSize.SMALL, true, false, true);
    }

    public WyvernScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(maxStorageSize));
    }

    public static WyvernScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new WyvernScreenHandler(syncId, playerInventory, inventory);
    }
}
