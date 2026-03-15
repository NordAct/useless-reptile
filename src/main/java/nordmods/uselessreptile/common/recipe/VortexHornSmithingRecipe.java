package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

import java.util.List;
import java.util.Optional;

public class VortexHornSmithingRecipe extends SimpleSmithingRecipe {
    private final Optional<Ingredient> template;
    private final Ingredient base;
    private final Optional<Ingredient> addition;
    private final ItemStackTemplate result;
    public static final MapCodec<VortexHornSmithingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                    Ingredient.CODEC.optionalFieldOf("template").forGetter(VortexHornSmithingRecipe::templateIngredient),
                    Ingredient.CODEC.fieldOf("base").forGetter(VortexHornSmithingRecipe::baseIngredient),
                    Ingredient.CODEC.optionalFieldOf("addition").forGetter(VortexHornSmithingRecipe::additionIngredient),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            ).apply(instance, VortexHornSmithingRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VortexHornSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
            VortexHornSmithingRecipe::templateIngredient,
            Ingredient.CONTENTS_STREAM_CODEC,
            VortexHornSmithingRecipe::baseIngredient,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
            VortexHornSmithingRecipe::additionIngredient,
            ItemStackTemplate.STREAM_CODEC,
            recipe -> recipe.result,
            VortexHornSmithingRecipe::new
    );
    public static final RecipeSerializer<VortexHornSmithingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    public VortexHornSmithingRecipe(Recipe.CommonInfo commonInfo, Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result) {
        super(commonInfo);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public @NonNull ItemStack assemble(SmithingRecipeInput smithingRecipeInput) {
        ItemStack instrument = getInstrumentStack(smithingRecipeInput);
        if (!instrument.isEmpty()) {
            ItemStack result = this.result.apply(smithingRecipeInput.base().getComponentsPatch());
            result.set(DataComponents.INSTRUMENT, instrument.get(DataComponents.INSTRUMENT));
            result.set(URItemComponents.DRAGON_STORAGE, instrument.getOrDefault(URItemComponents.DRAGON_STORAGE, URDragonDataStorageComponent.DEFAULT));
            VortexHornCapacityComponent original = instrument.getOrDefault(URItemComponents.VORTEX_HORN_CAPACITY, VortexHornCapacityComponent.DEFAULT);
            VortexHornCapacityComponent next = result.getOrDefault(URItemComponents.VORTEX_HORN_CAPACITY, VortexHornCapacityComponent.DEFAULT);
            result.set(URItemComponents.VORTEX_HORN_CAPACITY, new VortexHornCapacityComponent(original.currentCapacity(), next.maxCapacity()));
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
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(List.of(template, Optional.of(base), addition));
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
}
