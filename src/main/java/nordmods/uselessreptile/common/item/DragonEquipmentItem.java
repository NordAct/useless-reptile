package nordmods.uselessreptile.common.item;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

import java.util.function.Supplier;

public class DragonEquipmentItem extends Item {

    public DragonEquipmentItem(Supplier<AttributeModifiersComponent> dragonEquipmentAttributes, Settings settings) {
        super(settings);
    }

    public static Identifier equipmentModifierID(EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case HEAD -> UselessReptile.id("dragon_head_equipment");
            case LEGS -> UselessReptile.id("dragon_leg_equipment");
            case CHEST -> UselessReptile.id("dragon_chest_equipment");
            case FEET -> UselessReptile.id("dragon_feet_equipment");
            case BODY -> UselessReptile.id("dragon_body_equipment");
            case OFFHAND -> UselessReptile.id("dragon_offhand_equipment");
            case MAINHAND -> UselessReptile.id("dragon_mainhand_equipment");
        };
    }
}
