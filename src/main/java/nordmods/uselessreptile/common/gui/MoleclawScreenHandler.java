package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.init.URScreenHandlers;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.items.DragonArmorItem;

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

    @Override
    protected boolean canEquip(DragonArmorItem item) {
        return new ItemStack(item).isIn(URTags.MOLECLAW_CAN_EQUIP);
    }
}
