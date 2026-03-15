package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;
import org.jspecify.annotations.NonNull;

public class VortexHornRecipe extends ShapedRecipe {
    public static final MapCodec<VortexHornRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                            CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                            ShapedRecipePattern.MAP_CODEC.forGetter(o -> o.pattern),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
                    )
                    .apply(i, VortexHornRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VortexHornRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            CraftingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            ShapedRecipePattern.STREAM_CODEC,
            o -> o.pattern,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            VortexHornRecipe::new
    );
    public static final RecipeSerializer<VortexHornRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public VortexHornRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull CraftingInput input) {
        ItemStack instrument = getInstrumentStack(input);
        if (!instrument.isEmpty()) {
            ItemStack result = this.result.create();
            result.set(DataComponents.INSTRUMENT, instrument.get(DataComponents.INSTRUMENT));
            result.set(URItemComponents.DRAGON_STORAGE, instrument.getOrDefault(URItemComponents.DRAGON_STORAGE, URDragonDataStorageComponent.DEFAULT));
            VortexHornCapacityComponent original = instrument.getOrDefault(URItemComponents.VORTEX_HORN_CAPACITY, VortexHornCapacityComponent.DEFAULT);
            VortexHornCapacityComponent next = result.getOrDefault(URItemComponents.VORTEX_HORN_CAPACITY, VortexHornCapacityComponent.DEFAULT);
            result.set(URItemComponents.VORTEX_HORN_CAPACITY, new VortexHornCapacityComponent(original.currentCapacity(), next.maxCapacity()));
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() { //questions shall not be questioned
        return (RecipeSerializer<ShapedRecipe>)(Object)URRecipeSerializers.VORTEX_HORN;
    }

    protected ItemStack getInstrumentStack(CraftingInput craftingRecipeInput) {
        for (int x = 0; x < pattern.width(); x++)
            for (int y = 0; y < pattern.height(); y++) {
                ItemStack stack = craftingRecipeInput.getItem(x, y);
                if (stack.has(DataComponents.INSTRUMENT)) return stack;
            }
        return ItemStack.EMPTY;
    }
}
