package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

public class VortexHornRecipe extends ShapedRecipe {
    private final RawShapedRecipe raw;

    public VortexHornRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
        this.raw = raw;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack instrument = getInstrumentStack(input);
        if (!instrument.isEmpty()) {
            ItemStack result = getResult(lookup);
            result.set(DataComponentTypes.INSTRUMENT, instrument.get(DataComponentTypes.INSTRUMENT));
            result.set(URItems.DRAGON_STORAGE_COMPONENT, instrument.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT));
            result.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, instrument.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT));
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return URRecipeSerializers.VORTEX_HORN;
    }

    protected ItemStack getInstrumentStack(CraftingRecipeInput craftingRecipeInput) {
        for (int x = 0; x < raw.getWidth(); x++)
            for (int y = 0; y < raw.getHeight(); y++) {
                ItemStack stack = craftingRecipeInput.getStackInSlot(x, y);
                if (stack.contains(DataComponentTypes.INSTRUMENT)) return stack;
            }
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<VortexHornRecipe> {
        public static final MapCodec<VortexHornRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                        CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(ShapedRecipe::getCategory),
                        RawShapedRecipe.CODEC.forGetter((recipe) -> recipe.raw),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> recipe.getResult(null)),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification))
                        .apply(instance, VortexHornRecipe::new));
        public static final PacketCodec<RegistryByteBuf, VortexHornRecipe> PACKET_CODEC = PacketCodec.ofStatic(VortexHornRecipe.Serializer::write, VortexHornRecipe.Serializer::read);

        public MapCodec<VortexHornRecipe> codec() {
            return CODEC;
        }

        public PacketCodec<RegistryByteBuf, VortexHornRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static VortexHornRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe rawShapedRecipe = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new VortexHornRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryByteBuf buf, VortexHornRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
            buf.writeBoolean(recipe.showNotification());
        }
    }
}
