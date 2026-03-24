package nordmods.uselessreptile.mixin.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.DragonVariantUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.common.util.ComponentUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Unique
    private static final Component CAN_BE_EQUIPPED = Component.translatable("tooltip.uselessreptile.can_be_equipped_by");
    /// Adds tooltip if any of dragons can equip this item
    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void addDragonEquipmentEntries(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type, CallbackInfo ci) {
        if (URClientConfig.getConfig().hideEquipmentInfo) return;
        if (context.registries() == null) return;
        Set<Component> info = new HashSet<>();
        info.add(CAN_BE_EQUIPPED);
        info.addAll(CommonDragonVariantData.EQUIPMENT_INFO_MAP.computeIfAbsent(stack.getItem(), item -> {
            Set<Component> set = new HashSet<>();
            context.registries().lookupOrThrow(URResourceKeys.DRAGON_VARIANT)
                    .listElements().map(Holder.Reference::value)
                    .forEach(dragonVariant -> {
                        Map<Identifier, EquipmentModelData.Equipment> equipmentMap = DragonVariantUtil.getEquipmentModelDataMap(dragonVariant, context.registries());
                        if (equipmentMap.containsKey(BuiltInRegistries.ITEM.getKey(item))) addSetEntry(set, dragonVariant, equipmentMap.get(BuiltInRegistries.ITEM.getKey(item)).slot());
                    });
            return set;
        }));
        if (info.size() > 1) {
            ComponentUtil.addHidden(textConsumer, info, ChatFormatting.GRAY);
        }
    }

    @Unique
    private static void addSetEntry(Set<Component> set, DragonVariant dragonVariant, DragonInventory.Slot slot) {
        set.add(Component
                .literal("- ")
                .append(
                        Component.translatable(
                                dragonVariant.common().displayNameKey().isPresent() ?
                                        dragonVariant.common().displayNameKey().get() :
                                        dragonVariant.getType().getTranslationKey()
                        )
                )
                .append(" (")
                .append(Component.translatable(slot.getTranslationKey()))
                .append(")")
        );
    }
}
