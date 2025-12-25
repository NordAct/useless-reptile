package nordmods.uselessreptile.datagen.data;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class AdvancementCriterions {
    public static Criterion<TameAnimalTrigger.TriggerInstance> entityTamedCondition(HolderGetter<EntityType<?>> registryEntryLookup, EntityType<? extends Entity> type) {
        return CriteriaTriggers.TAME_ANIMAL.createCriterion(TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(registryEntryLookup, type)).triggerInstance());
    }

    public static Criterion<PlayerTrigger.TriggerInstance> gameTickCondition() {
        return CriteriaTriggers.TICK.createCriterion(PlayerTrigger.TriggerInstance.tick().triggerInstance());
    }

    public static Criterion<ConsumeItemTrigger.TriggerInstance> useItemCondition(HolderGetter<Item> registryEntryLookup, Item item) {
        return CriteriaTriggers.CONSUME_ITEM.createCriterion(ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(registryEntryLookup, item)).triggerInstance());
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> obtainItem(HolderGetter<Item> registryEntryLookup, DataComponentMap ignored, ItemStack... itemStacks) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(),
                InventoryChangeTrigger.TriggerInstance.Slots.ANY, Util.make(new ArrayList<>(), list -> {
                    Arrays.stream(itemStacks).forEach(itemStack -> {
                        list.add(
                                ItemPredicate.Builder.item()
                                        .of(registryEntryLookup, itemStack.getItem())
                                        .withComponents(DataComponentMatchers.Builder.components()
                                                .exact(DataComponentExactPredicate.allOf(itemStack
                                                        .getComponents().filter(componentType -> !ignored.has(componentType))))
                                                .build())
                                        .build());
                    });
                }))
        );
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> obtainItem(HolderGetter<Item> registryEntryLookup, TagKey<Item> tag) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(registryEntryLookup, tag)).triggerInstance());
    }

    public static Criterion<ImpossibleTrigger.TriggerInstance> triggeredFromCode() {
        return CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());
    }
}
