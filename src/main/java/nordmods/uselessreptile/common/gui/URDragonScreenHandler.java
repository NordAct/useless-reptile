package nordmods.uselessreptile.common.gui;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import org.jetbrains.annotations.Nullable;

public abstract class URDragonScreenHandler extends ScreenHandler {

    protected Inventory inventory;
    protected StorageSize storageSize;
    public static final int maxStorageSize = 20;

    protected URDragonScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, StorageSize storageSize, boolean hasSaddle, boolean hasArmor, boolean hasBanner) {
        super(type, syncId);

        this.inventory = inventory;
        this.storageSize = storageSize;
        inventory.onOpen(playerInventory.player);

        if (hasSaddle) {
            this.addSlot(new Slot(inventory, 0, 8, 18) {
                public boolean canInsert(ItemStack stack) {
                    return stack.isOf(Items.SADDLE) && !this.hasStack();
                }
                public int getMaxItemCount() {
                    return 1;
                }
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    return !(playerEntity.getVehicle() instanceof URRideableDragonEntity);
                }
            });
        }

        if (hasArmor) {
            this.addSlot(new Slot(inventory, 1, 8+54+18, 18) {
                public boolean canInsert(ItemStack stack) {
                    if (!(stack.getItem() instanceof DragonArmorItem dragonArmorItem)) return false;
                    return dragonArmorItem.getSlotType() == EquipmentSlot.HEAD && !this.hasStack();
                }
                public int getMaxItemCount() {
                    return 1;
                }
            });
            this.addSlot(new Slot(inventory, 2, 8+54+18, 18*2) {
                public boolean canInsert(ItemStack stack) {
                    if (!(stack.getItem() instanceof DragonArmorItem dragonArmorItem)) return false;
                    return dragonArmorItem.getSlotType() == EquipmentSlot.CHEST && !this.hasStack();
                }
                public int getMaxItemCount() {
                    return 1;
                }
            });
            this.addSlot(new Slot(inventory, 3, 8+54+18, 18*3) {
                public boolean canInsert(ItemStack stack) {
                    if (!(stack.getItem() instanceof DragonArmorItem dragonArmorItem)) return false;
                    return dragonArmorItem.getSlotType() == EquipmentSlot.LEGS && !this.hasStack();
                }
                public int getMaxItemCount() {
                    return 1;
                }
            });
        }

        if (hasBanner) {
            this.addSlot(new Slot(inventory, 4, 8, 18*2) {
                public boolean canInsert(ItemStack stack) {
                    return stack.getItem() instanceof BannerItem && !this.hasStack();
                }
                public int getMaxItemCount() {
                    return 1;
                }
            });
        }

        //dragon storage
        int size = storageSize.getSize();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                int column = i / 3;
                int row = i % 3;
                int offset = hasArmor ? 2 : 1;
                this.addSlot(new Slot(inventory, 5 + i, 8+54+18*offset+18*column, 18+18*row));
            }
        }

        //Draws player inv + hotbar
        int i;
        int j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 102 + i * 18 - 18));
            }
        }
        for (j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        System.out.println(invSlot);
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                for (int i = 0; i < maxStorageSize; i++)
                    if (!this.insertItem(originalStack, i, this.inventory.size(), false)) return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }

        return newStack;
    }

    public enum StorageSize {
        NONE(0),
        SMALL(3),
        MEDIUM(6),
        LARGE(9),
        VERY_LARGE(12),
        MAX(15);

        private final int size;

        StorageSize(int size) {
            this.size = size;
        }

        public int getSize() {return size;}
    }
}
