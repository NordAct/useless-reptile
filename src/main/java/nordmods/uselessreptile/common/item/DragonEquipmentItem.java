package nordmods.uselessreptile.common.item;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

import java.util.function.Supplier;

public class DragonEquipmentItem extends Item {

    private final Supplier<AttributeModifiersComponent> dragonEquipmentAttributes;
    private final EquipmentSlot equipmentSlot;

    public DragonEquipmentItem(EquipmentSlot equipmentSlot, Supplier<AttributeModifiersComponent> dragonEquipmentAttributes, net.minecraft.item.Item.Settings settings) {
        super(settings);
        this.equipmentSlot = equipmentSlot;
        this.dragonEquipmentAttributes = dragonEquipmentAttributes;
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return dragonEquipmentAttributes.get();
    }

    public static Identifier equipmentModifierID(EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case HEAD -> UselessReptile.id("dragon_head");
            case LEGS -> UselessReptile.id("dragon_legs");
            case CHEST -> UselessReptile.id("dragon_chest");
            case FEET -> UselessReptile.id("dragon_feet");
            case BODY -> UselessReptile.id("dragon_body");
            case OFFHAND -> UselessReptile.id("dragon_offhand");
            case MAINHAND -> UselessReptile.id("dragon_mainhand");
        };
    }

    public EquipmentSlot getSlotType() {
        return equipmentSlot;
    }
}
