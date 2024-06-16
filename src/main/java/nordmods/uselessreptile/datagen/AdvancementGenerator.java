package nordmods.uselessreptile.datagen;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
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

public class AdvancementGenerator extends FabricAdvancementProvider {
    protected AdvancementGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
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

        AdvancementEntry tameWyvern = tamingAdvancementEntry(UREntities.WYVERN_ENTITY, root);
        AdvancementEntry tameMoleclaw = tamingAdvancementEntry(UREntities.MOLECLAW_ENTITY, root);
        AdvancementEntry tameLightningChaser = tamingAdvancementEntry(UREntities.LIGHTNING_CHASER_ENTITY, root);
        AdvancementEntry tameRiverPikehorn = tamingAdvancementEntry(UREntities.RIVER_PIKEHORN_ENTITY, root);

        AdvancementEntry useFlute = Advancement.Builder.createUntelemetered()
                .display(URItems.FLUTE,
                        Text.translatable("advancement.uselessreptile.use_flute"),
                        Text.translatable("advancement.uselessreptile.use_flute.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("use_item", AdvancementCriterions.useItemCondition(URItems.FLUTE))
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
                .criterion("obtain_item", AdvancementCriterions.obtainItem(URTags.MOLECLAW_HELMETS))
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
                .criterion("obtain_item", AdvancementCriterions.obtainItem(potion))
                .parent(tameWyvern)
                .build(UselessReptile.id("dragon/gather_acid"));

        consumer.accept(root);
        consumer.accept(tameWyvern);
        consumer.accept(tameMoleclaw);
        consumer.accept(tameLightningChaser);
        consumer.accept(tameRiverPikehorn);
        consumer.accept(useFlute);
        consumer.accept(moleclawHelmet);
        consumer.accept(gatherAcid);
    }

    private static AdvancementEntry tamingAdvancementEntry(EntityType<? extends Entity> type, AdvancementEntry parent) {
        String id = EntityType.getId(type).getPath();
        return Advancement.Builder.createUntelemetered()
                .display(Registries.ITEM.get(UselessReptile.id(id + "_spawn_egg")),
                        Text.translatable("advancement.uselessreptile.tame_" + id),
                        Text.translatable("advancement.uselessreptile.tame_" + id + ".desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("entity_tamed", AdvancementCriterions.entityTamedCondition(type))
                .parent(parent)
                .build(UselessReptile.id("dragon/tame_" + id));
    }
}
