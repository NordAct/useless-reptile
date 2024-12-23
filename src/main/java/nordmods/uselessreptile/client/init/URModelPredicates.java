package nordmods.uselessreptile.client.init;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.component.FluteComponent;
import org.jetbrains.annotations.Nullable;

public class URModelPredicates {
    public static void init() {
        register(URItems.FLUTE, UselessReptile.id("flute_mode"), (stack, world, entity, seed) ->
                ((FluteComponent)stack.getOrDefault(URItems.FLUTE_MODE_COMPONENT, CustomModelDataComponent.DEFAULT)).mode());

        registerVortexHornPredicate(URItems.VORTEX_HORN);
        registerVortexHornPredicate(URItems.IRON_VORTEX_HORN);
        registerVortexHornPredicate(URItems.GOLD_VORTEX_HORN);
        registerVortexHornPredicate(URItems.DIAMOND_VORTEX_HORN);
        registerVortexHornPredicate(URItems.NETHERITE_VORTEX_HORN);
    }

    private static void register(Item item, Identifier id, UnclampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, id, provider);
    }

    private static void registerVortexHornPredicate(Item item) {
        register(item, Identifier.ofVanilla("tooting"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
    }

    //idk why there's no public method to pass not clamped provider with custom id
    private interface UnclampedModelPredicateProvider extends ClampedModelPredicateProvider {
        @Override
        default float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            return unclampedCall(stack, world, entity, seed);
        }
    }
}
