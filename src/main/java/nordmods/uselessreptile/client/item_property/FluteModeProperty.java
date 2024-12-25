package nordmods.uselessreptile.client.item_property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.init.URItems;
import org.jetbrains.annotations.Nullable;

public record FluteModeProperty() implements NumericProperty {
    public static final MapCodec<FluteModeProperty> CODEC = MapCodec.unit(new FluteModeProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (stack.contains(URItems.FLUTE_MODE_COMPONENT)) return stack.get(URItems.FLUTE_MODE_COMPONENT).mode();
        return 0;
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}
