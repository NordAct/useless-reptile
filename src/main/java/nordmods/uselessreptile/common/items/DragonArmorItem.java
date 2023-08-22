package nordmods.uselessreptile.common.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;

public class DragonArmorItem extends Item {
    public DragonArmorItem(int armorBonus, EquipmentSlot slot, Settings settings) {
        super(settings);
        this.armorBonus = armorBonus;
        this.slot = slot;
    }
    private final int armorBonus;
    private final EquipmentSlot slot;

    public int getArmorBonus() {return this.armorBonus;}

    public EquipmentSlot getSlotType() {return this.slot;}
}
