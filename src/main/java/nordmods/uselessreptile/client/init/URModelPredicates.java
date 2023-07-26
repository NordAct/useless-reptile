package nordmods.uselessreptile.client.init;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.common.init.URItems;

public class URModelPredicates {
    public static void init() {
        ModelPredicateProviderRegistry.register(URItems.FLUTE, new Identifier("mode"),
                (stack, world, entity, seed) -> stack.hasNbt() ? (float)stack.getNbt().getInt("Mode")/2 : 0.0F);
    }
}
