package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

import java.util.List;
import java.util.Optional;

public class VortexHornSmithingRecipe implements SmithingRecipe {
    private final Optional<Ingredient> template;
    private final Ingredient base;
    private final Optional<Ingredient> addition;
    private final TransmuteRecipeResult result;
    private IngredientPlacement ingredientPlacement;
    public VortexHornSmithingRecipe(Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, TransmuteRecipeResult result) {

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack instrument = getInstrumentStack(smithingRecipeInput);
        if (!instrument.isEmpty()) {
            ItemStack result = this.result.apply(smithingRecipeInput.base());
            result.set(DataComponentTypes.INSTRUMENT, instrument.get(DataComponentTypes.INSTRUMENT));
            result.set(URItems.DRAGON_STORAGE_COMPONENT, instrument.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT));
            VortexHornCapacityComponent original = instrument.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            VortexHornCapacityComponent next = result.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            result.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, new VortexHornCapacityComponent(original.currentCapacity(), next.maxCapacity()));
            return result;
        }
        return ItemStack.EMPTY;
    }

    protected ItemStack getInstrumentStack(SmithingRecipeInput smithingRecipeInput) {
        ItemStack stack = smithingRecipeInput.getStackInSlot(1);
        if (stack.contains(DataComponentTypes.INSTRUMENT)) return stack;
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<VortexHornSmithingRecipe> getSerializer() {
        return URRecipeSerializers.VORTEX_HORN_SMITHING;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, Optional.of(this.base), this.addition));
        }

        return this.ingredientPlacement;
    }

    @Override
    public Optional<Ingredient> template() {
        return template;
    }

    @Override
    public Ingredient base() {
        return base;
    }

    @Override
    public Optional<Ingredient> addition() {
        return addition;
    }

    public static class Serializer implements RecipeSerializer<VortexHornSmithingRecipe> {
        private static final MapCodec<VortexHornSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.optionalFieldOf("template").forGetter(VortexHornSmithingRecipe::template),
                                Ingredient.CODEC.fieldOf("base").forGetter(VortexHornSmithingRecipe::base),
                                Ingredient.CODEC.optionalFieldOf("addition").forGetter(VortexHornSmithingRecipe::addition),
                                TransmuteRecipeResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        ).apply(instance, VortexHornSmithingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, VortexHornSmithingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.OPTIONAL_PACKET_CODEC,
                VortexHornSmithingRecipe::template,
                Ingredient.PACKET_CODEC,
                VortexHornSmithingRecipe::base,
                Ingredient.OPTIONAL_PACKET_CODEC,
                VortexHornSmithingRecipe::addition,
                TransmuteRecipeResult.PACKET_CODEC,
                recipe -> recipe.result,
                VortexHornSmithingRecipe::new
        );

        @Override
        public MapCodec<VortexHornSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, VortexHornSmithingRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
