package nordmods.uselessreptile.mixin.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CommonColors;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
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
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        Set<Component> info = DragonVariant.EQUIPMENT_INFO_MAP.computeIfAbsent(stack.getItem(), item -> {
            Set<Component> list = new HashSet<>();
            level.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_VARIANT).forEach(dragonVariant -> {
                if (checkPresence(dragonVariant.saddleItems().orElse(null), item)) {
                    addListEntry(list, dragonVariant);
                    return;
                }
                if (checkPresence(dragonVariant.helmetItems().orElse(null), item)) {
                    addListEntry(list, dragonVariant);
                    return;
                }
                if (checkPresence(dragonVariant.chestplateItems().orElse(null), item)) {
                    addListEntry(list, dragonVariant);
                    return;
                }
                if (checkPresence(dragonVariant.tailArmorItems().orElse(null), item)) {
                    addListEntry(list, dragonVariant);
                    return;
                }
            });
            return list;
        });
        if (!info.isEmpty()) {
            textConsumer.accept(Component.translatable("tooltip.uselessreptile.can_be_equipped_by").withColor(CommonColors.LIGHT_GRAY));
            info.forEach(textConsumer);
        }
    }

    @Unique
    private static boolean checkPresence(List<ExtraCodecs.TagOrElementLocation> list, Item item) {
        if (list == null) return false;
        for (ExtraCodecs.TagOrElementLocation entry : list) {
            if (entry.tag()) {
                if (item.builtInRegistryHolder().is(TagKey.create(Registries.ITEM, entry.id()))) return true;
            } else if (item.builtInRegistryHolder().is(entry.id())) return true;
        }
        return false;
    }

    @Unique
    private static void addListEntry(Set<Component> set, DragonVariant dragonVariant) {
        set.add(Component
                .literal("- ")
                .withColor(CommonColors.LIGHT_GRAY)
                .append(
                        Component.translatable(
                                dragonVariant.displayNameKey().isPresent() ? dragonVariant.displayNameKey().get()
                                : Util.makeDescriptionId("entity", dragonVariant.dragonId())
                        ).withColor(CommonColors.LIGHT_GRAY)
                )
        );
    }
}
