package nordmods.uselessreptile.mixin.common.misc;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.event.DragonEquipmentTooltipEntryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract Item asItem();

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void addDragonEquipmentEntries(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (URClientConfig.getConfig().hideEquipmentInfo) return;
        List<EntityType<? extends Entity>> entries = new ArrayList<>(DragonEquipmentTooltipEntryEvent.EVENT.invoker().getEntries(asItem()));
        if (entries.isEmpty()) return;

        textConsumer.accept(Text.translatable("tooltip.uselessreptile.can_be_equipped_by").withColor(Colors.LIGHT_GRAY));
        for (EntityType<?> entityType : entries) {
            textConsumer.accept(Text.literal("- ").withColor(Colors.LIGHT_GRAY).append(Text.translatable(entityType.getTranslationKey()).withColor(Colors.LIGHT_GRAY)));
        }
    }
}
