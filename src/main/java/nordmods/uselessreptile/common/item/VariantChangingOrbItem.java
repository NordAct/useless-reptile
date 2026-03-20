package nordmods.uselessreptile.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.client.gui.VariantChangingOrbScreen;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class VariantChangingOrbItem extends Item {
    public VariantChangingOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (stack.getComponents().has(DataComponents.CUSTOM_NAME)) {
            if (URDragonPart.getPartParent(user) instanceof URDragonEntity dragon) entity = dragon;
            if (entity instanceof URDragonEntity dragon && (dragon.getOwner() == user || user.isCreative())) {
                String variant = stack.get(DataComponents.CUSTOM_NAME).getString();
                if (DragonVariant.get(dragon.getVariantType(), variant, user.level()) != null) {
                    dragon.setVariant(variant);
                    stack.consume(1, user);
                    return InteractionResult.SUCCESS;
                } else {
                    user.sendOverlayMessage(Component.translatable("other.uselessreptile.variant_not_found", variant));
                }
            }
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable("tooltip.uselessreptile.variant_changing_orb").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new VariantChangingOrbScreen(URDragonVariantTypes.WYVERN, "green"));
        }
        return InteractionResult.SUCCESS;
    }
}
