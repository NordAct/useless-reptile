package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import org.jetbrains.annotations.Nullable;

public class URDragonScreenHandler extends ScreenHandler {
    protected final DragonInventory inventory;
    protected final DragonInventory.StorageSize storageSize;
    public static final int SLOT_SIDE = 18;
    public static final int ENTITY_WINDOW_SIDE = 54;
    public static final int EDGE_OFFSET = 8;

    public URDragonScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, DragonInventory inventory) {
        super(type, syncId);
        this.inventory = inventory;
        this.storageSize = inventory.storageSize;
        inventory.onOpen(playerInventory.player);

        if (inventory.hasSaddle) {
            addSlot(new DragonEquipmentSlot(inventory, DragonInventory.SADDLE_INDEX, EDGE_OFFSET, SLOT_SIDE) {
                public boolean canTakeItems(PlayerEntity playerEntity) {
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

        addPlayerSlots(playerInventory, EDGE_OFFSET, 84);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                for (int i = 0; i < DragonInventory.getInventorySize(storageSize); i++)
                    if (!insertItem(originalStack, i, inventory.size(), false)) return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }

        return newStack;
    }

    public static class DragonEquipmentSlot extends Slot {
        public DragonEquipmentSlot(DragonInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return getInventory().canInsertInSlot(stack, getIndex());
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }

        public DragonInventory getInventory() {
            return (DragonInventory) inventory;
        }
    }
}
