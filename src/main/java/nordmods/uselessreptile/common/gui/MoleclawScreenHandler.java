package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.init.URScreenHandlers;
import nordmods.uselessreptile.common.init.URTags;

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
    protected boolean canEquip(EquipmentSlot equipmentSlot, ItemStack item) {
        return switch (equipmentSlot) {
            case EquipmentSlot.HEAD -> item.isIn(URTags.MOLECLAW_HELMETS);
            case EquipmentSlot.CHEST -> item.isIn(URTags.MOLECLAW_CHESTPLATES);
            case EquipmentSlot.LEGS -> item.isIn(URTags.MOLECLAW_TAIL_ARMOR);
            default -> false;
        };
    }
}
