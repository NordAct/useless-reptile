package nordmods.uselessreptile.common.entity.misc;

import net.minecraft.inventory.SimpleInventory;

public class DragonInventory extends SimpleInventory {
    public static final int INVENTORY_START_INDEX = 5;
    public final StorageSize storageSize;
    public final boolean hasArmor;
    public final boolean hasSaddle;
    public final boolean hasBanner;

    public DragonInventory(StorageSize storageSize, boolean hasArmor, boolean hasSaddle, boolean hasBanner) {
        super(getInventorySize(storageSize));
        this.storageSize = storageSize;
        this.hasArmor = hasArmor;
        this.hasSaddle = hasSaddle;
        this.hasBanner = hasBanner;
    }

    public static int getInventorySize(StorageSize storageSize) {
        return storageSize.size + INVENTORY_START_INDEX;
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
