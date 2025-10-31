package nordmods.uselessreptile.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URRecipeSerializers;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

public class VortexHornSmithingRecipe implements SmithingRecipe {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;
    public VortexHornSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return this.template.test(input.template())
                && this.base.test(input.base())
                && this.addition.test(input.addition());

    }

    public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack instrument = getInstrumentStack(smithingRecipeInput);
        if (!instrument.isEmpty()) {
            ItemStack result = getResult(wrapperLookup).copy();
            result.set(DataComponentTypes.INSTRUMENT, instrument.get(DataComponentTypes.INSTRUMENT));
            result.set(URItems.DRAGON_STORAGE_COMPONENT, instrument.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT));
            result.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, instrument.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT));
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result;
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

    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    public static class Serializer implements RecipeSerializer<VortexHornSmithingRecipe> {
        private static final MapCodec<VortexHornSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(r -> r.template),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(r -> r.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(r -> r.addition),
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(r -> r.result)
                        ).apply(instance, VortexHornSmithingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, VortexHornSmithingRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC,
                r -> r.template,
                Ingredient.PACKET_CODEC,
                r -> r.base,
                Ingredient.PACKET_CODEC,
                r -> r.addition,
                ItemStack.PACKET_CODEC,
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
