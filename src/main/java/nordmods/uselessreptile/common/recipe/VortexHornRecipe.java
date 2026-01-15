package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;
import org.jspecify.annotations.NonNull;

public class VortexHornRecipe extends ShapedRecipe {
    public VortexHornRecipe(String group, CraftingBookCategory category, ShapedRecipePattern raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull CraftingInput input, HolderLookup.@NonNull Provider lookup) {
        ItemStack instrument = getInstrumentStack(input);
        if (!instrument.isEmpty()) {
            ItemStack result = this.result.copy();
            result.set(DataComponents.INSTRUMENT, instrument.get(DataComponents.INSTRUMENT));
            result.set(URItems.DRAGON_STORAGE_COMPONENT, instrument.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT));
            VortexHornCapacityComponent original = instrument.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            VortexHornCapacityComponent next = result.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            result.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, new VortexHornCapacityComponent(original.currentCapacity(), next.maxCapacity()));
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NonNull RecipeSerializer<? extends ShapedRecipe> getSerializer() {
        return URRecipeSerializers.VORTEX_HORN;
    }

    protected ItemStack getInstrumentStack(CraftingInput craftingRecipeInput) {
        for (int x = 0; x < pattern.width(); x++)
            for (int y = 0; y < pattern.height(); y++) {
                ItemStack stack = craftingRecipeInput.getItem(x, y);
                if (stack.has(DataComponents.INSTRUMENT)) return stack;
            }
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<VortexHornRecipe> {
        public static final MapCodec<VortexHornRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                        ShapedRecipePattern.MAP_CODEC.forGetter((recipe) -> recipe.pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter((recipe) -> recipe.result),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification))
                        .apply(instance, VortexHornRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, VortexHornRecipe> PACKET_CODEC = StreamCodec.of(nordmods.uselessreptile.common.recipe.VortexHornRecipe.Serializer::write, nordmods.uselessreptile.common.recipe.VortexHornRecipe.Serializer::read);

        public @NonNull MapCodec<VortexHornRecipe> codec() {
            return CODEC;
        }

        public @NonNull StreamCodec<RegistryFriendlyByteBuf, VortexHornRecipe> streamCodec() {
            return PACKET_CODEC;
        }

        private static VortexHornRecipe read(RegistryFriendlyByteBuf buf) {
            String string = buf.readUtf();
            CraftingBookCategory craftingRecipeCategory = buf.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern rawShapedRecipe = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new VortexHornRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryFriendlyByteBuf buf, VortexHornRecipe recipe) {
            buf.writeUtf(recipe.group());
            buf.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification());
        }
    }
}
