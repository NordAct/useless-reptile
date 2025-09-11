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
import net.minecraft.potion.Potions;
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
import nordmods.uselessreptile.common.item.VortexHornItem;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

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
                        Text.translatable("advancement.uselessreptile.root"),
                        Text.translatable("advancement.uselessreptile.root.desc"),
                        Identifier.of("minecraft:block/dirt"),
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
                .criterion("obtain_item", AdvancementCriterions.obtainItem(registryEntryLookup, ComponentMap.EMPTY, potion))
                .parent(tameWyvern)
                .build(UselessReptile.id("dragon/gather_acid"));

        AdvancementEntry magmamuncherApplyFireResistance = Advancement.Builder.createUntelemetered()
                .display(Items.LAVA_BUCKET,
                        Text.translatable("advancement.uselessreptile.magmamuncher_apply_fire_resistance"),
                        Text.translatable("advancement.uselessreptile.magmamuncher_apply_fire_resistance.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(tameMagmamuncher)
                .build(UselessReptile.id("dragon/magmamuncher_apply_fire_resistance"));

        AdvancementEntry sitDownDragon = Advancement.Builder.createUntelemetered()
                .display(Items.STICK,
                        Text.translatable("advancement.uselessreptile.sit_down_dragon"),
                        Text.translatable("advancement.uselessreptile.sit_down_dragon.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(root)
                .build(UselessReptile.id("dragon/sit_down_dragon"));

        AdvancementEntry useHorn = Advancement.Builder.createUntelemetered()
                .display(Items.GOAT_HORN,
                        Text.translatable("advancement.uselessreptile.use_horn"),
                        Text.translatable("advancement.uselessreptile.use_horn.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/use_horn"));

        AdvancementEntry equipFullDiamondDragonArmor = Advancement.Builder.createUntelemetered()
                .display(URItems.DRAGON_HELMET_DIAMOND,
                        Text.translatable("advancement.uselessreptile.equip_full_diamond_dragon_armor"),
                        Text.translatable("advancement.uselessreptile.equip_full_diamond_dragon_armor.desc"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/equip_full_diamond_dragon_armor"));

        AdvancementEntry equipFullNetheriteDragonArmor = Advancement.Builder.createUntelemetered()
                .display(URItems.DRAGON_HELMET_NETHERITE,
                        Text.translatable("advancement.uselessreptile.equip_full_netherite_dragon_armor"),
                        Text.translatable("advancement.uselessreptile.equip_full_netherite_dragon_armor.desc"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(equipFullDiamondDragonArmor)
                .build(UselessReptile.id("dragon/equip_full_netherite_dragon_armor"));

        AdvancementEntry getVortexHorn = Advancement.Builder.createUntelemetered()
                .display(URItems.VORTEX_HORN,
                        Text.translatable("advancement.uselessreptile.get_vortex_horn"),
                        Text.translatable("advancement.uselessreptile.get_vortex_horn.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("get_vortex_horn", AdvancementCriterions.obtainItem(registryEntryLookup, URTags.VORTEX_HORNS))
                .parent(useHorn)
                .build(UselessReptile.id("dragon/get_vortex_horn"));

        ItemStack vortexHorn = URItems.NETHERITE_VORTEX_HORN.getDefaultStack();
        ComponentMap netheriteVortexHornStorage = ComponentMap.builder()
                .add(
                        URItems.VORTEX_HORN_CAPACITY_COMPONENT,
                        new VortexHornCapacityComponent(
                                ((VortexHornItem)vortexHorn.getItem()).getMaxCapacity(vortexHorn)
                                , ((VortexHornItem)vortexHorn.getItem()).getMaxCapacity(vortexHorn)
                        )
                ).build();
        vortexHorn.applyComponentsFrom(netheriteVortexHornStorage);

        AdvancementEntry fullVortexHorn = Advancement.Builder.createUntelemetered()
                .display(URItems.NETHERITE_VORTEX_HORN,
                        Text.translatable("advancement.uselessreptile.full_vortex_horn"),
                        Text.translatable("advancement.uselessreptile.full_vortex_horn.desc"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false)
                .criterion("get_full_horn", AdvancementCriterions
                        .obtainItem(
                                registryEntryLookup,
                                vortexHorn.getDefaultComponents().filtered(componentType -> componentType != URItems.VORTEX_HORN_CAPACITY_COMPONENT),
                                vortexHorn
                        )
                )
                .parent(getVortexHorn)
                .build(UselessReptile.id("dragon/full_vortex_horn"));

        AdvancementEntry eatFromInventory = Advancement.Builder.createUntelemetered()
                .display(Items.CHICKEN,
                        Text.translatable("advancement.uselessreptile.eat_from_inventory"),
                        Text.translatable("advancement.uselessreptile.eat_from_inventory.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/eat_from_inventory"));

        ItemStack potion1 = new ItemStack(Items.POTION);
        potion1.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.STRENGTH)).build());
        AdvancementEntry givePotion = Advancement.Builder.createUntelemetered()
                .display(potion1,
                        Text.translatable("advancement.uselessreptile.give_potion"),
                        Text.translatable("advancement.uselessreptile.give_potion.desc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false)
                .criterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(eatFromInventory)
                .build(UselessReptile.id("dragon/give_potion"));

        consumer.accept(root);
        consumer.accept(tameWyvern);
        consumer.accept(tameMoleclaw);
        consumer.accept(tameLightningChaser);
        consumer.accept(tameRiverPikehorn);
        consumer.accept(tameMagmamuncher);
        consumer.accept(useFlute);
        consumer.accept(moleclawHelmet);
        consumer.accept(gatherAcid);
        consumer.accept(magmamuncherApplyFireResistance);
        consumer.accept(sitDownDragon);
        consumer.accept(useHorn);
        consumer.accept(equipFullDiamondDragonArmor);
        consumer.accept(equipFullNetheriteDragonArmor);
        consumer.accept(getVortexHorn);
        consumer.accept(fullVortexHorn);
        consumer.accept(sitDownDragon);
        consumer.accept(eatFromInventory);
        consumer.accept(givePotion);
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
