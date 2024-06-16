package nordmods.uselessreptile.client.init;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.type.CustomModelDataComponent;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.component.FluteComponent;

public class URModelPredicates {
    public static void init() {
        ModelPredicateProviderRegistry.register(URItems.FLUTE, UselessReptile.id("flute_mode"),
                (stack, world, entity, seed) -> ((FluteComponent)stack.getOrDefault(URItems.FLUTE_MODE_COMPONENT, CustomModelDataComponent.DEFAULT)).mode()/10f);
    }
}
