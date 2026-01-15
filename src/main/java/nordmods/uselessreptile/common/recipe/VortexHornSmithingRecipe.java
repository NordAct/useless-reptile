package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.TransmuteResult;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class VortexHornSmithingRecipe implements SmithingRecipe {
    private final Optional<Ingredient> template;
    private final Ingredient base;
    private final Optional<Ingredient> addition;
    private final TransmuteResult result;
    private PlacementInfo ingredientPlacement;
    public VortexHornSmithingRecipe(Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, TransmuteResult result) {

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public @NonNull ItemStack assemble(SmithingRecipeInput smithingRecipeInput, HolderLookup.@NonNull Provider wrapperLookup) {
        ItemStack instrument = getInstrumentStack(smithingRecipeInput);
        if (!instrument.isEmpty()) {
            ItemStack result = this.result.apply(smithingRecipeInput.base());
            result.set(DataComponents.INSTRUMENT, instrument.get(DataComponents.INSTRUMENT));
            result.set(URItems.DRAGON_STORAGE_COMPONENT, instrument.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT));
            VortexHornCapacityComponent original = instrument.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            VortexHornCapacityComponent next = result.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT);
            result.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, new VortexHornCapacityComponent(original.currentCapacity(), next.maxCapacity()));
            return result;
        }
        return ItemStack.EMPTY;
    }

    protected ItemStack getInstrumentStack(SmithingRecipeInput smithingRecipeInput) {
        ItemStack stack = smithingRecipeInput.getItem(1);
        if (stack.has(DataComponents.INSTRUMENT)) return stack;
        return ItemStack.EMPTY;
    }

    @Override
    public @NonNull RecipeSerializer<VortexHornSmithingRecipe> getSerializer() {
        return URRecipeSerializers.VORTEX_HORN_SMITHING;
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
        }

        return this.ingredientPlacement;
    }

    @Override
    public @NonNull Optional<Ingredient> templateIngredient() {
        return template;
    }

    @Override
    public @NonNull Ingredient baseIngredient() {
        return base;
    }

    @Override
    public @NonNull Optional<Ingredient> additionIngredient() {
        return addition;
    }

    public static class Serializer implements RecipeSerializer<VortexHornSmithingRecipe> {
        private static final MapCodec<VortexHornSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.CODEC.optionalFieldOf("template").forGetter(VortexHornSmithingRecipe::templateIngredient),
                                Ingredient.CODEC.fieldOf("base").forGetter(VortexHornSmithingRecipe::baseIngredient),
                                Ingredient.CODEC.optionalFieldOf("addition").forGetter(VortexHornSmithingRecipe::additionIngredient),
                                TransmuteResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                        ).apply(instance, VortexHornSmithingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, VortexHornSmithingRecipe> PACKET_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                VortexHornSmithingRecipe::templateIngredient,
                Ingredient.CONTENTS_STREAM_CODEC,
                VortexHornSmithingRecipe::baseIngredient,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                VortexHornSmithingRecipe::additionIngredient,
                TransmuteResult.STREAM_CODEC,
                recipe -> recipe.result,
                VortexHornSmithingRecipe::new
        );

        @Override
        public @NonNull MapCodec<VortexHornSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NonNull StreamCodec<RegistryFriendlyByteBuf, VortexHornSmithingRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
