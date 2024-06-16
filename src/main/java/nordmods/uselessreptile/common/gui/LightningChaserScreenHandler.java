package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.init.URScreenHandlers;
import nordmods.uselessreptile.common.init.URTags;

public class LightningChaserScreenHandler extends URDragonScreenHandler{
    public LightningChaserScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, syncId, playerInventory, inventory, StorageSize.MEDIUM, true, true, true);
    }

    public LightningChaserScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(maxStorageSize));
    }

    public static LightningChaserScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        return new LightningChaserScreenHandler(syncId, playerInventory, inventory);
    }

    @Override
    protected boolean canEquip(EquipmentSlot equipmentSlot, ItemStack item) {
        return switch (equipmentSlot) {
            case EquipmentSlot.HEAD -> item.isIn(URTags.LIGHTNING_CHASER_HELMETS);
            case EquipmentSlot.CHEST -> item.isIn(URTags.LIGHTNING_CHASER_CHESTPLATES);
            case EquipmentSlot.LEGS -> item.isIn(URTags.LIGHTNING_CHASER_TAIL_ARMOR);
            default -> false;
        };
    }
}
