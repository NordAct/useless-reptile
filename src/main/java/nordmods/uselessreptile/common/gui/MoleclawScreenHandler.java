package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class MoleclawScreenHandler extends URDragonScreenHandler {
    public MoleclawScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.MOLECLAW_INVENTORY, syncId, playerInventory, inventory, StorageSize.LARGE, true, true, true);

    }

    public MoleclawScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(maxStorageSize));
    }

    public static MoleclawScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new MoleclawScreenHandler(syncId, playerInventory, inventory);
    }
}
