package nordmods.uselessreptile.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import nordmods.uselessreptile.common.init.URModEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds entries to {@link nordmods.uselessreptile.common.item.DragonEquipmentItem#appendHoverText(ItemStack, Item.TooltipContext, List, TooltipFlag)} to show which entities can equip this item
 * For usage example refer to {@link URModEvents#addDragonEquipmentTooltipEntries()}
 */
public interface DragonEquipmentTooltipEntryEvent {
    Event<DragonEquipmentTooltipEntryEvent> EVENT = EventFactory.createArrayBacked(
            DragonEquipmentTooltipEntryEvent.class,
            callbacks -> ((item) -> {
                List<EntityType<? extends Entity>> added = new ArrayList<>();
                for (DragonEquipmentTooltipEntryEvent event : callbacks) added.addAll(event.getEntries(item));
                return added;
            })
    );
    List<EntityType<? extends Entity>> getEntries(Item item);
}
