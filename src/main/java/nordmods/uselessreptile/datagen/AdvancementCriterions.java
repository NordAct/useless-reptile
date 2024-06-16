package nordmods.uselessreptile.datagen;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.Optional;

public class AdvancementCriterions {
    public static AdvancementCriterion<TameAnimalCriterion.Conditions> entityTamedCondition(EntityType<? extends Entity> type) {
        return Criteria.TAME_ANIMAL.create(TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type(type)).conditions());
    }

    public static AdvancementCriterion<TickCriterion.Conditions> gameTickCondition() {
        return Criteria.TICK.create(TickCriterion.Conditions.createTick().conditions());
    }

    public static AdvancementCriterion<ConsumeItemCriterion.Conditions> useItemCondition(Item item) {
        return Criteria.CONSUME_ITEM.create(ConsumeItemCriterion.Conditions.predicate(ItemPredicate.Builder.create().items(item)).conditions());
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> obtainItem(ItemStack itemStack) {
        return Criteria.INVENTORY_CHANGED.create(new InventoryChangedCriterion.Conditions(Optional.empty(),
                InventoryChangedCriterion.Conditions.Slots.ANY,
                List.of(ItemPredicate.Builder.create().items(itemStack.getItem()).component(ComponentPredicate.of(itemStack.getComponents())).build())
                ));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> obtainItem(TagKey<Item> tag) {
        return Criteria.INVENTORY_CHANGED.create(InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(tag)).conditions());
    }
}
