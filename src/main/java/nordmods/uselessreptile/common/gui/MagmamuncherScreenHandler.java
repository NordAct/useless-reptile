package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.init.URScreenHandlers;

public class MagmamuncherScreenHandler extends URDragonScreenHandler{
    public MagmamuncherScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.MAGMAMUNCHER_INVENTORY, syncId, playerInventory, inventory, StorageSize.SMALL, false, false, false);
    }

    public MagmamuncherScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(MAX_STORAGE_SIZE));
    }

    public static MagmamuncherScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new MagmamuncherScreenHandler(syncId, playerInventory, inventory);
    }

    @Override
    protected boolean canEquip(EquipmentSlot equipmentSlot, ItemStack item) {
        return false;
    }

    @Override
    protected boolean isSaddleItem(ItemStack itemStack) {
        return false;
    }
}
