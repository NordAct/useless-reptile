package nordmods.uselessreptile.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider{
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture, null);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //Moleclaw armor items
        getOrCreateTagBuilder(URTags.MOLECLAW_PROTECTS_FROM_LIGHT)
                .add(URItems.MOLECLAW_HELMET_IRON)
                .add(URItems.MOLECLAW_HELMET_GOLD)
                .add(URItems.MOLECLAW_HELMET_DIAMOND);

        getOrCreateTagBuilder(URTags.MOLECLAW_CHESTPLATES)
                .add(URItems.DRAGON_CHESTPLATE_IRON)
                .add(URItems.DRAGON_CHESTPLATE_GOLD)
                .add(URItems.DRAGON_CHESTPLATE_DIAMOND);

        getOrCreateTagBuilder(URTags.MOLECLAW_TAIL_ARMOR)
                .add(URItems.DRAGON_TAIL_ARMOR_IRON)
                .add(URItems.DRAGON_TAIL_ARMOR_GOLD)
                .add(URItems.DRAGON_TAIL_ARMOR_DIAMOND);

        getOrCreateTagBuilder(URTags.MOLECLAW_HELMETS)
                .add(URItems.DRAGON_HELMET_IRON)
                .add(URItems.DRAGON_HELMET_GOLD)
                .add(URItems.DRAGON_HELMET_DIAMOND)
                .addTag(URTags.MOLECLAW_PROTECTS_FROM_LIGHT);

        //Lightning chaser armor items
        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_CHESTPLATES)
                .add(URItems.DRAGON_CHESTPLATE_IRON)
                .add(URItems.DRAGON_CHESTPLATE_GOLD)
                .add(URItems.DRAGON_CHESTPLATE_DIAMOND);

        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_TAIL_ARMOR)
                .add(URItems.DRAGON_TAIL_ARMOR_IRON)
                .add(URItems.DRAGON_TAIL_ARMOR_GOLD)
                .add(URItems.DRAGON_TAIL_ARMOR_DIAMOND);

        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_HELMETS)
                .add(URItems.DRAGON_HELMET_IRON)
                .add(URItems.DRAGON_HELMET_GOLD)
                .add(URItems.DRAGON_HELMET_DIAMOND);
    }
}
