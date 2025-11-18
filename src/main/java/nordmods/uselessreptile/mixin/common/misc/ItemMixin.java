package nordmods.uselessreptile.mixin.common.misc;

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
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract Item asItem();

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void addDragonEquipmentEntries(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type, CallbackInfo ci) {
        if (URClientConfig.getConfig().hideEquipmentInfo) return;
        List<EntityType<? extends Entity>> entries = new ArrayList<>(DragonEquipmentTooltipEntryEvent.EVENT.invoker().getEntries(asItem()));
        if (entries.isEmpty()) return;

        textConsumer.accept(Component.translatable("tooltip.uselessreptile.can_be_equipped_by").withColor(CommonColors.LIGHT_GRAY));
        for (EntityType<?> entityType : entries) {
            textConsumer.accept(Component.literal("- ").withColor(CommonColors.LIGHT_GRAY).append(Component.translatable(entityType.getDescriptionId()).withColor(CommonColors.LIGHT_GRAY)));
        }
    }
}
