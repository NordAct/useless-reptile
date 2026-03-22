package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.item.component.DragonVariantComponent;
import nordmods.uselessreptile.common.item.component.FluteComponent;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;

import java.util.function.UnaryOperator;

public class URItemComponents {
    public static final DataComponentType<FluteComponent> FLUTE_MODE = register("flute_mode",
            builder -> builder.persistent(FluteComponent.CODEC).networkSynchronized(FluteComponent.PACKET_CODEC));
    public static final DataComponentType<URDragonDataStorageComponent> DRAGON_STORAGE = register("dragon_storage",
            builder -> builder.persistent(URDragonDataStorageComponent.CODEC).networkSynchronized(URDragonDataStorageComponent.PACKET_CODEC));
    public static final DataComponentType<VortexHornCapacityComponent> VORTEX_HORN_CAPACITY = register("vortex_horn_capacity",
            builder -> builder.persistent(VortexHornCapacityComponent.CODEC).networkSynchronized(VortexHornCapacityComponent.PACKET_CODEC));
    public static final DataComponentType<DragonVariantComponent> DRAGON_VARIANT = register("dragon_variant",
            builder -> builder.persistent(DragonVariantComponent.CODEC).networkSynchronized(DragonVariantComponent.PACKET_CODEC));

    public static void init() {}

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, UselessReptile.id(id), (builderOperator.apply(DataComponentType.builder())).build());
    }
}
