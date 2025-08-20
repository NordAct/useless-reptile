package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class URItemTagProvider extends FabricTagProvider.ItemTagProvider{
    public URItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //Moleclaw armor items
        getTagBuilder(URTags.PROTECTS_MOLECLAW_FROM_LIGHT)
                .add(URItems.MOLECLAW_HELMET_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.MOLECLAW_HELMET_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.MOLECLAW_HELMET_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.MOLECLAW_HELMET_NETHERITE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.MOLECLAW_CHESTPLATES)
                .add(URItems.DRAGON_CHESTPLATE_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_NETHERITE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.MOLECLAW_TAIL_ARMOR)
                .add(URItems.DRAGON_TAIL_ARMOR_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_NETHERITE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.MOLECLAW_HELMETS)
                .add(URItems.DRAGON_HELMET_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_NETHERITE.getRegistryEntry().registryKey().getValue())
                .addTag(URTags.PROTECTS_MOLECLAW_FROM_LIGHT.id());

        //Lightning chaser armor items
        getTagBuilder(URTags.LIGHTNING_CHASER_CHESTPLATES)
                .add(URItems.DRAGON_CHESTPLATE_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_CHESTPLATE_NETHERITE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.LIGHTNING_CHASER_TAIL_ARMOR)
                .add(URItems.DRAGON_TAIL_ARMOR_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_TAIL_ARMOR_NETHERITE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.LIGHTNING_CHASER_HELMETS)
                .add(URItems.DRAGON_HELMET_IRON.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_GOLD.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_DIAMOND.getRegistryEntry().registryKey().getValue())
                .add(URItems.DRAGON_HELMET_NETHERITE.getRegistryEntry().registryKey().getValue());

        //Saddles
        getTagBuilder(URTags.WYVERN_SADDLES)
                .add(Items.SADDLE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.LIGHTNING_CHASER_SADDLES)
                .add(Items.SADDLE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.MOLECLAW_SADDLES)
                .add(Items.SADDLE.getRegistryEntry().registryKey().getValue());

        getTagBuilder(URTags.DRAGON_SADDLES)
                .addOptionalTag(URTags.WYVERN_SADDLES.id())
                .addOptionalTag(URTags.LIGHTNING_CHASER_SADDLES.id())
                .addOptionalTag(URTags.MOLECLAW_SADDLES.id());

        //Dragon food
        getTagBuilder(URTags.LIGHTNING_CHASER_FOOD)
                .addOptionalTag(ItemTags.MEAT.id());
        getTagBuilder(URTags.WYVERN_FOOD)
                .add(Items.CHICKEN.getRegistryEntry().registryKey().getValue());
        getTagBuilder(URTags.RIVER_PIKEHORN_FOOD)
                .addOptionalTag(ItemTags.FISHES.id());
        getTagBuilder(URTags.MOLECLAW_FOOD)
                .add(Items.BEETROOT.getRegistryEntry().registryKey().getValue());
        getTagBuilder(URTags.MAGMAMUNCHER_FOOD)
                .add(Items.MAGMA_BLOCK.getRegistryEntry().registryKey().getValue())
                .add(Items.MAGMA_CREAM.getRegistryEntry().registryKey().getValue());

        //Dragon taming items
        getTagBuilder(URTags.WYVERN_TAMING_ITEM)
                .addOptionalTag(URTags.WYVERN_FOOD.id());
        getTagBuilder(URTags.RIVER_PIKEHORN_TAMING_ITEM)
                .add(Items.TROPICAL_FISH_BUCKET.getRegistryEntry().registryKey().getValue());
        getTagBuilder(URTags.MOLECLAW_TAMING_ITEM)
                .addOptionalTag(URTags.MOLECLAW_FOOD.id());
        getTagBuilder(URTags.MAGMAMUNCHER_TAMING_ITEM)
                .add(Items.BLAZE_ROD.getRegistryEntry().registryKey().getValue());
    }
}
