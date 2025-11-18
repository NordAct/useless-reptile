package nordmods.uselessreptile.common.entity.misc;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class DragonInventory extends SimpleContainer {
    public static final int INVENTORY_START_INDEX = 5;
    public static final int SADDLE_INDEX = 0;
    public static final int HELMET_INDEX = 1;
    public static final int CHESTPLATE_INDEX = 2;
    public static final int TAIL_ARMOR_INDEX = 3;
    public static final int BANNER_INDEX = 4;
    public final StorageSize storageSize;
    public final boolean hasArmor;
    public final boolean hasSaddle;
    public final boolean hasBanner;
    public final Function<ItemStack, Boolean> isSaddle;
    public final Function<ItemStack, Boolean> isHelmet;
    public final Function<ItemStack, Boolean> isChestplate;
    public final Function<ItemStack, Boolean> isTailArmor;
    public final Function<ItemStack, Boolean> isBanner;

    public DragonInventory(@Nullable URDragonEntity dragon, StorageSize storageSize, boolean hasArmor, boolean hasSaddle, boolean hasBanner) {
        super(getInventorySize(storageSize));
        this.storageSize = storageSize;
        this.hasArmor = hasArmor;
        this.hasSaddle = hasSaddle;
        this.hasBanner = hasBanner;
        if (dragon == null) { //for sake of even being able to register screen handlers
            this.isSaddle = itemStack -> false;
            this.isHelmet = itemStack -> false;
            this.isChestplate = itemStack -> false;
            this.isTailArmor = itemStack -> false;
            this.isBanner = itemStack -> false;
        }
        else {
            this.isSaddle = dragon::isSaddle;
            this.isHelmet = dragon::isHelmet;
            this.isChestplate = dragon::isChestplate;
            this.isTailArmor = dragon::isTailArmor;
            this.isBanner = dragon::isBanner;
        }
    }

    @Override
    public boolean canAddItem(ItemStack stack) {
        for (int i = 0; i < getContainerSize(); i++) {
            if (canInsertInSlot(stack, i)) return true;
        }
        return false;
    }

    public boolean canInsertInSlot(ItemStack stack, int slot) {
        ItemStack itemStack = items.get(slot);
        switch (slot) {
            case SADDLE_INDEX -> {
                if (hasSaddle && itemStack.isEmpty() && isSaddle.apply(stack)) return true;
            }
            case HELMET_INDEX -> {
                if (hasArmor && itemStack.isEmpty() && isHelmet.apply(stack)) return true;
            }
            case CHESTPLATE_INDEX -> {
                if (hasArmor && itemStack.isEmpty() && isChestplate.apply(stack)) return true;
            }
            case TAIL_ARMOR_INDEX -> {
                if (hasArmor && itemStack.isEmpty() && isTailArmor.apply(stack)) return true;
            }
            case BANNER_INDEX -> {
                if (hasBanner && itemStack.isEmpty() && isBanner.apply(stack)) return true;
            }
            default -> {
                if (itemStack.isEmpty() || ItemStack.isSameItemSameComponents(itemStack, stack) && itemStack.getCount() < itemStack.getMaxStackSize()) return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack addItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack itemStack = stack.copy();
        for (int i = 0; i < getContainerSize(); i++) {
            if (canInsertInSlot(stack, i)) {
                ItemStack target = getItem(i);
                if (target.isEmpty()) {
                    setItem(i, itemStack);
                    return ItemStack.EMPTY;
                }
                else {
                    moveItemsBetweenStacks(itemStack, getItem(i));
                    if (itemStack.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        return itemStack;
    }

    public static int getInventorySize(StorageSize storageSize) {
        return storageSize.size + INVENTORY_START_INDEX;
    }

    private void moveItemsBetweenStacks(ItemStack source, ItemStack target) {
        int i = getMaxStackSize(target);
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.grow(j);
            source.shrink(j);
            setChanged();
        }
    }

    @SuppressWarnings("unused")
    public enum StorageSize {
        NO_INVENTORY(-INVENTORY_START_INDEX),
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
