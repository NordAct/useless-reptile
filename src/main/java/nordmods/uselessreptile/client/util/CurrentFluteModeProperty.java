package nordmods.uselessreptile.client.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.item.component.FluteConfigurationComponent;
import nordmods.uselessreptile.common.item.FluteItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CurrentFluteModeProperty() implements SelectItemModelProperty<FluteItem.FluteMode> {
    private static final Codec<FluteItem.FluteMode> CODEC = URRegistries.FLUTE_MODE.byNameCodec();
    public static final SelectItemModelProperty.Type<CurrentFluteModeProperty, FluteItem.FluteMode> TYPE = SelectItemModelProperty.Type.create(
            MapCodec.unit(new CurrentFluteModeProperty()), CODEC
    );

    @Override
    public FluteItem.@Nullable FluteMode get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        FluteConfigurationComponent component = itemStack.get(URItemComponents.FLUTE_CONFIGURATION);
        return component == null ? null : component.currentMode();
    }

    @Override
    public @NonNull Codec<FluteItem.FluteMode> valueCodec() {
        return CODEC;
    }

    @Override
    public Type<? extends SelectItemModelProperty<FluteItem.FluteMode>, FluteItem.FluteMode> type() {
        return TYPE;
    }
}
