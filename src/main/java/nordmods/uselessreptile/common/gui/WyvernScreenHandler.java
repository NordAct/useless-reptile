package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URScreenHandlers;
import nordmods.uselessreptile.common.init.URTags;

public class WyvernScreenHandler extends URDragonScreenHandler {

    public WyvernScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.WYVERN_INVENTORY, syncId, playerInventory, inventory, DragonInventory.StorageSize.SMALL, true, false, true);
    }

    public WyvernScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(DragonInventory.getInventorySize(DragonInventory.StorageSize.SMALL)));
    }

    public static WyvernScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new WyvernScreenHandler(syncId, playerInventory, inventory);
    }

    @Override
    protected boolean canEquip(EquipmentSlot equipmentSlot, ItemStack item) {
        return false;
    }

    @Override
    protected boolean isSaddleItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.WYVERN_SADDLES);
    }
}
