package nordmods.uselessreptile.common.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import org.jetbrains.annotations.Nullable;

public class URDragonScreenHandler extends AbstractContainerMenu {
    protected final DragonInventory inventory;
    protected final DragonInventory.StorageSize storageSize;
    public static final int SLOT_SIDE = 18;
    public static final int ENTITY_WINDOW_SIDE = 54;
    public static final int EDGE_OFFSET = 8;

    public URDragonScreenHandler(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, DragonInventory inventory) {
        super(type, syncId);
        this.inventory = inventory;
        this.storageSize = inventory.storageSize;
        inventory.startOpen(playerInventory.player);

        if (inventory.hasSaddle) {
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.SADDLE_INDEX, EDGE_OFFSET, SLOT_SIDE) {
                public boolean mayPickup(Player playerEntity) {
                    return !(playerEntity.getVehicle() instanceof URRideableDragonEntity);
                }
            });
        }

        if (inventory.hasArmor) {
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.HELMET_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE));
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.CHESTPLATE_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE *2));
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.TAIL_ARMOR_INDEX, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE, SLOT_SIDE *3));
        }

        if (inventory.hasBanner) {
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.BANNER_INDEX, EDGE_OFFSET, SLOT_SIDE *2));
        }

        //dragon storage
        int size = storageSize.getSize();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                int column = i / 3;
                int row = i % 3;
                int offset = inventory.hasArmor ? 2 : 1;
                addSlot(new Slot(inventory, DragonInventory.INVENTORY_START_INDEX + i, EDGE_OFFSET+ ENTITY_WINDOW_SIDE + SLOT_SIDE *offset+ SLOT_SIDE *column, SLOT_SIDE + SLOT_SIDE *row));
            }
        }

        addStandardInventorySlots(playerInventory, EDGE_OFFSET, 84);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        inventory.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }


    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
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
        public boolean mayPlace(ItemStack stack) {
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
