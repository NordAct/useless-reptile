package nordmods.uselessreptile.datagen.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
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
    public URAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        HolderGetter<Item> registryEntryLookup = registryLookup.lookupOrThrow(Registries.ITEM);

        AdvancementHolder root = Advancement.Builder.recipeAdvancement()
                .display(URItems.WYVERN_SKIN,
                        Component.translatable("advancement.uselessreptile.root"),
                        Component.translatable("advancement.uselessreptile.root.desc"),
                        ResourceLocation.parse("minecraft:block/dirt"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("tick", AdvancementCriterions.gameTickCondition())
                .build(UselessReptile.id("dragon/root"));

        AdvancementHolder tameWyvern = tamingAdvancementEntry(registryLookup, UREntities.WYVERN_ENTITY, root);
        AdvancementHolder tameMoleclaw = tamingAdvancementEntry(registryLookup, UREntities.MOLECLAW_ENTITY, root);
        AdvancementHolder tameLightningChaser = tamingAdvancementEntry(registryLookup, UREntities.LIGHTNING_CHASER_ENTITY, root);
        AdvancementHolder tameRiverPikehorn = tamingAdvancementEntry(registryLookup, UREntities.RIVER_PIKEHORN_ENTITY, root);
        AdvancementHolder tameMagmamuncher = tamingAdvancementEntry(registryLookup, UREntities.MAGMAMUNCHER_ENTITY, root);

        AdvancementHolder useFlute = Advancement.Builder.recipeAdvancement()
                .display(URItems.FLUTE,
                        Component.translatable("advancement.uselessreptile.use_flute"),
                        Component.translatable("advancement.uselessreptile.use_flute.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("use_item", AdvancementCriterions.useItemCondition(registryEntryLookup, URItems.FLUTE))
                .parent(tameRiverPikehorn)
                .build(UselessReptile.id("dragon/use_flute"));

        AdvancementHolder moleclawHelmet = Advancement.Builder.recipeAdvancement()
                .display(URItems.MOLECLAW_HELMET_GOLD,
                        Component.translatable("advancement.uselessreptile.moleclaw_helmet"),
                        Component.translatable("advancement.uselessreptile.moleclaw_helmet.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_item", AdvancementCriterions.obtainItem(registryEntryLookup, URTags.PROTECTS_MOLECLAW_FROM_LIGHT))
                .parent(tameMoleclaw)
                .build(UselessReptile.id("dragon/moleclaw_helmet"));

        ItemStack potion = new ItemStack(Items.POTION);
        potion.applyComponents(DataComponentMap.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(URPotions.ACID)).build());
        AdvancementHolder gatherAcid = Advancement.Builder.recipeAdvancement()
                .display(potion,
                        Component.translatable("advancement.uselessreptile.gather_acid"),
                        Component.translatable("advancement.uselessreptile.gather_acid.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("obtain_item", AdvancementCriterions.obtainItem(registryEntryLookup, DataComponentMap.EMPTY, potion))
                .parent(tameWyvern)
                .build(UselessReptile.id("dragon/gather_acid"));

        AdvancementHolder magmamuncherApplyFireResistance = Advancement.Builder.recipeAdvancement()
                .display(Items.LAVA_BUCKET,
                        Component.translatable("advancement.uselessreptile.magmamuncher_apply_fire_resistance"),
                        Component.translatable("advancement.uselessreptile.magmamuncher_apply_fire_resistance.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(tameMagmamuncher)
                .build(UselessReptile.id("dragon/magmamuncher_apply_fire_resistance"));

        AdvancementHolder sitDownDragon = Advancement.Builder.recipeAdvancement()
                .display(Items.STICK,
                        Component.translatable("advancement.uselessreptile.sit_down_dragon"),
                        Component.translatable("advancement.uselessreptile.sit_down_dragon.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(root)
                .build(UselessReptile.id("dragon/sit_down_dragon"));

        AdvancementHolder useHorn = Advancement.Builder.recipeAdvancement()
                .display(Items.GOAT_HORN,
                        Component.translatable("advancement.uselessreptile.use_horn"),
                        Component.translatable("advancement.uselessreptile.use_horn.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/use_horn"));

        AdvancementHolder equipFullDiamondDragonArmor = Advancement.Builder.recipeAdvancement()
                .display(URItems.DRAGON_HELMET_DIAMOND,
                        Component.translatable("advancement.uselessreptile.equip_full_diamond_dragon_armor"),
                        Component.translatable("advancement.uselessreptile.equip_full_diamond_dragon_armor.desc"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/equip_full_diamond_dragon_armor"));

        AdvancementHolder equipFullNetheriteDragonArmor = Advancement.Builder.recipeAdvancement()
                .display(URItems.DRAGON_HELMET_NETHERITE,
                        Component.translatable("advancement.uselessreptile.equip_full_netherite_dragon_armor"),
                        Component.translatable("advancement.uselessreptile.equip_full_netherite_dragon_armor.desc"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(equipFullDiamondDragonArmor)
                .build(UselessReptile.id("dragon/equip_full_netherite_dragon_armor"));

        AdvancementHolder getVortexHorn = Advancement.Builder.recipeAdvancement()
                .display(URItems.VORTEX_HORN,
                        Component.translatable("advancement.uselessreptile.get_vortex_horn"),
                        Component.translatable("advancement.uselessreptile.get_vortex_horn.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("get_vortex_horn", AdvancementCriterions.obtainItem(registryEntryLookup, URTags.VORTEX_HORNS))
                .parent(useHorn)
                .build(UselessReptile.id("dragon/get_vortex_horn"));

        ItemStack vortexHorn = URItems.NETHERITE_VORTEX_HORN.getDefaultInstance();
        DataComponentMap netheriteVortexHornStorage = DataComponentMap.builder()
                .set(
                        URItems.VORTEX_HORN_CAPACITY_COMPONENT,
                        new VortexHornCapacityComponent(
                                ((VortexHornItem)vortexHorn.getItem()).getMaxCapacity(vortexHorn)
                                , ((VortexHornItem)vortexHorn.getItem()).getMaxCapacity(vortexHorn)
                        )
                ).build();
        vortexHorn.applyComponents(netheriteVortexHornStorage);

        AdvancementHolder fullVortexHorn = Advancement.Builder.recipeAdvancement()
                .display(URItems.NETHERITE_VORTEX_HORN,
                        Component.translatable("advancement.uselessreptile.full_vortex_horn"),
                        Component.translatable("advancement.uselessreptile.full_vortex_horn.desc"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false)
                .addCriterion("get_full_horn", AdvancementCriterions
                        .obtainItem(
                                registryEntryLookup,
                                vortexHorn.getPrototype().filter(componentType -> componentType != URItems.VORTEX_HORN_CAPACITY_COMPONENT),
                                vortexHorn
                        )
                )
                .parent(getVortexHorn)
                .build(UselessReptile.id("dragon/full_vortex_horn"));

        AdvancementHolder eatFromInventory = Advancement.Builder.recipeAdvancement()
                .display(Items.CHICKEN,
                        Component.translatable("advancement.uselessreptile.eat_from_inventory"),
                        Component.translatable("advancement.uselessreptile.eat_from_inventory.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
                .parent(sitDownDragon)
                .build(UselessReptile.id("dragon/eat_from_inventory"));

        ItemStack potion1 = new ItemStack(Items.POTION);
        potion1.applyComponents(DataComponentMap.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.STRENGTH)).build());
        AdvancementHolder givePotion = Advancement.Builder.recipeAdvancement()
                .display(potion1,
                        Component.translatable("advancement.uselessreptile.give_potion"),
                        Component.translatable("advancement.uselessreptile.give_potion.desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("triggered_from_code", AdvancementCriterions.triggeredFromCode())
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

    private static AdvancementHolder tamingAdvancementEntry(HolderLookup.Provider registryLookup,EntityType<? extends Entity> type, AdvancementHolder parent) {
        HolderGetter<EntityType<?>> registryEntryLookup = registryLookup.lookupOrThrow(Registries.ENTITY_TYPE);
        String id = EntityType.getKey(type).getPath();
        return Advancement.Builder.recipeAdvancement()
                .display(
                        BuiltInRegistries.ITEM.get(UselessReptile.id(id + "_spawn_egg")).get().value(),
                        Component.translatable("advancement.uselessreptile.tame_" + id),
                        Component.translatable("advancement.uselessreptile.tame_" + id + ".desc"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("entity_tamed", AdvancementCriterions.entityTamedCondition(registryEntryLookup, type))
                .parent(parent)
                .build(UselessReptile.id("dragon/tame_" + id));
    }
}
