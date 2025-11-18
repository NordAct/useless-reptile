package nordmods.uselessreptile.common.item;

import nordmods.uselessreptile.UselessReptile;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class DragonEquipmentItem extends Item {

    public DragonEquipmentItem(Supplier<ItemAttributeModifiers> dragonEquipmentAttributes, Properties settings) {
        super(settings.attributes(dragonEquipmentAttributes.get()));
    }

    public static ResourceLocation equipmentModifierID(EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case HEAD -> UselessReptile.id("dragon_head_equipment");
            case LEGS -> UselessReptile.id("dragon_leg_equipment");
            case CHEST -> UselessReptile.id("dragon_chest_equipment");
            case FEET -> UselessReptile.id("dragon_feet_equipment");
            case BODY -> UselessReptile.id("dragon_body_equipment");
            case OFFHAND -> UselessReptile.id("dragon_offhand_equipment");
            case MAINHAND -> UselessReptile.id("dragon_mainhand_equipment");
            case SADDLE -> UselessReptile.id("dragon_saddle_equipment");
        };
    }
}
