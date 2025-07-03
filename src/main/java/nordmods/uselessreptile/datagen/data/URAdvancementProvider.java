package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URPotions;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class URAdvancementProvider extends FabricAdvancementProvider {
    public URAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        RegistryEntryLookup<Item> registryEntryLookup = registryLookup.getOrThrow(RegistryKeys.ITEM);

        AdvancementEntry root = Advancement.Builder.createUntelemetered()
                .display(URItems.WYVERN_SKIN,
                        Text.literal("Useless Reptile"),
                        Text.translatable("advancement.uselessreptile.root.desc"),
                        Identifier.of("minecraft:textures/block/dirt.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("tick", AdvancementCriterions.gameTickCondition())
                .build(UselessReptile.id("dragon/root"));

        AdvancementEntry tameWyvern = tamingAdvancementEntry(registryLookup, UREntities.WYVERN_ENTITY, root);
        AdvancementEntry tameMoleclaw = tamingAdvancementEntry(registryLookup, UREntities.MOLECLAW_ENTITY, root);
        AdvancementEntry tameLightningChaser = tamingAdvancementEntry(registryLookup, UREntities.LIGHTNING_CHASER_ENTITY, root);
        AdvancementEntry tameRiverPikehorn = tamingAdvancementEntry(registryLookup, UREntities.RIVER_PIKEHORN_ENTITY, root);
        AdvancementEntry tameMagmamuncher = tamingAdvancementEntry(registryLookup, UREntities.MAGMAMUNCHER_ENTITY, root);

        AdvancementEntry useFlute = Advancement.Builder.createUntelemetered()
                .display(URItems.FLUTE,
                        Text.translatable("advancement.uselessreptile.use_flute"),
                        Text.translatable("advancement.uselessreptile.use_flute.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("use_item", AdvancementCriterions.useItemCondition(registryEntryLookup, URItems.FLUTE))
                .parent(tameRiverPikehorn)
                .build(UselessReptile.id("dragon/use_flute"));

        AdvancementEntry moleclawHelmet = Advancement.Builder.createUntelemetered()
                .display(URItems.MOLECLAW_HELMET_GOLD,
                        Text.translatable("advancement.uselessreptile.moleclaw_helmet"),
                        Text.translatable("advancement.uselessreptile.moleclaw_helmet.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("obtain_item", AdvancementCriterions.obtainItem(registryEntryLookup, URTags.PROTECTS_MOLECLAW_FROM_LIGHT))
                .parent(tameMoleclaw)
                .build(UselessReptile.id("dragon/moleclaw_helmet"));

        ItemStack potion = new ItemStack(Items.POTION);
        potion.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(URPotions.ACID)).build());
        AdvancementEntry gatherAcid = Advancement.Builder.createUntelemetered()
                .display(potion,
                        Text.translatable("advancement.uselessreptile.gather_acid"),
                        Text.translatable("advancement.uselessreptile.gather_acid.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("obtain_item", AdvancementCriterions.obtainItem(registryEntryLookup, potion))
                .parent(tameWyvern)
                .build(UselessReptile.id("dragon/gather_acid"));

        consumer.accept(root);
        consumer.accept(tameWyvern);
        consumer.accept(tameMoleclaw);
        consumer.accept(tameLightningChaser);
        consumer.accept(tameRiverPikehorn);
        consumer.accept(tameMagmamuncher);
        consumer.accept(useFlute);
        consumer.accept(moleclawHelmet);
        consumer.accept(gatherAcid);
    }

    private static AdvancementEntry tamingAdvancementEntry(RegistryWrapper.WrapperLookup registryLookup,EntityType<? extends Entity> type, AdvancementEntry parent) {
        RegistryEntryLookup<EntityType<?>> registryEntryLookup = registryLookup.getOrThrow(RegistryKeys.ENTITY_TYPE);
        String id = EntityType.getId(type).getPath();
        return Advancement.Builder.createUntelemetered()
                .display(
                        Registries.ITEM.getEntry(UselessReptile.id(id + "_spawn_egg")).get().value(),
                        Text.translatable("advancement.uselessreptile.tame_" + id),
                        Text.translatable("advancement.uselessreptile.tame_" + id + ".desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("entity_tamed", AdvancementCriterions.entityTamedCondition(registryEntryLookup, type))
                .parent(parent)
                .build(UselessReptile.id("dragon/tame_" + id));
    }
}
