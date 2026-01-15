package nordmods.uselessreptile.common.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import org.jspecify.annotations.NonNull;

public class URDragonMenu extends AbstractContainerMenu {
    protected final DragonInventory inventory;
    protected final DragonInventory.StorageSize storageSize;
    public static final int SLOT_SIDE = 18;
    public static final int ENTITY_WINDOW_SIDE = 54;
    public static final int EDGE_OFFSET = 8;

    public URDragonMenu(int syncId, Inventory playerInventory, DragonInventory inventory) {
        super(null, syncId);
        this.inventory = inventory;
        this.storageSize = inventory.storageSize;
        inventory.startOpen(playerInventory.player);

        if (inventory.hasSaddle) {
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.SADDLE_INDEX, EDGE_OFFSET, SLOT_SIDE) {
                public boolean mayPickup(@NonNull Player playerEntity) {
                    return !(playerEntity.getVehicle() instanceof URRideableDragonEntity);
                }
            });
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.BANNER_INDEX, EDGE_OFFSET, SLOT_SIDE *2));
        }

        if (inventory.hasHelmet) addSlot(new DragonEquipmentSlot(inventory, DragonInventory.HELMET_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE));
        if (inventory.hasChestplate) addSlot(new DragonEquipmentSlot(inventory, DragonInventory.CHESTPLATE_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE *2));
        if (inventory.hasTailArmor) addSlot(new DragonEquipmentSlot(inventory, DragonInventory.TAIL_ARMOR_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE *3));

        //dragon storage
        int size = storageSize.getSize();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                int column = i / 3;
                int row = i % 3;
                int offset = inventory.hasHelmet || inventory.hasChestplate || inventory.hasTailArmor ? 2 : 1;
                addSlot(new Slot(inventory, DragonInventory.INVENTORY_START_INDEX + i, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE *offset+ SLOT_SIDE *column, SLOT_SIDE + SLOT_SIDE *row));
            }
        }

        addStandardInventorySlots(playerInventory, EDGE_OFFSET, 84);
    }

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        inventory.stopOpen(player);
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return inventory.stillValid(player);
    }


    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < inventory.getContainerSize()) {
                if (!moveItemStackTo(originalStack, inventory.getContainerSize(), this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                for (int i = 0; i < DragonInventory.getInventorySize(storageSize); i++)
                    if (!moveItemStackTo(originalStack, i, inventory.getContainerSize(), false)) return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return newStack;
    }

    public static class DragonEquipmentSlot extends Slot {
        public DragonEquipmentSlot(DragonInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {
            return getInventory().canInsertInSlot(stack, getContainerSlot());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        public DragonInventory getInventory() {
            return (DragonInventory) container;
        }
    }
}
