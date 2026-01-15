package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URItemTagProvider extends FabricTagProvider.ItemTagProvider{
    public URItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        //Moleclaw armor items
        getOrCreateRawBuilder(URTags.PROTECTS_MOLECLAW_FROM_LIGHT)
                .addElement(URItems.MOLECLAW_HELMET_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.MOLECLAW_HELMET_NETHERITE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.MOLECLAW_CHESTPLATES)
                .addElement(URItems.DRAGON_CHESTPLATE_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_NETHERITE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.MOLECLAW_TAIL_ARMOR)
                .addElement(URItems.DRAGON_TAIL_ARMOR_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_NETHERITE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.MOLECLAW_HELMETS)
                .addElement(URItems.DRAGON_HELMET_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_NETHERITE.builtInRegistryHolder().key().identifier())
                .addTag(URTags.PROTECTS_MOLECLAW_FROM_LIGHT.location());

        //Lightning chaser armor items
        getOrCreateRawBuilder(URTags.LIGHTNING_CHASER_CHESTPLATES)
                .addElement(URItems.DRAGON_CHESTPLATE_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_CHESTPLATE_NETHERITE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.LIGHTNING_CHASER_TAIL_ARMOR)
                .addElement(URItems.DRAGON_TAIL_ARMOR_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_TAIL_ARMOR_NETHERITE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.LIGHTNING_CHASER_HELMETS)
                .addElement(URItems.DRAGON_HELMET_IRON.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_GOLD.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_DIAMOND.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DRAGON_HELMET_NETHERITE.builtInRegistryHolder().key().identifier());

        //Saddles
        getOrCreateRawBuilder(URTags.WYVERN_SADDLES)
                .addElement(Items.SADDLE.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DUAL_SADDLE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.LIGHTNING_CHASER_SADDLES)
                .addElement(Items.SADDLE.builtInRegistryHolder().key().identifier());

        getOrCreateRawBuilder(URTags.MOLECLAW_SADDLES)
                .addElement(Items.SADDLE.builtInRegistryHolder().key().identifier());

        //other items
        getOrCreateRawBuilder(URTags.VORTEX_HORNS)
                .addElement(URItems.VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.IRON_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.GOLD_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.DIAMOND_VORTEX_HORN.builtInRegistryHolder().key().identifier())
                .addElement(URItems.NETHERITE_VORTEX_HORN.builtInRegistryHolder().key().identifier());
    }
}
