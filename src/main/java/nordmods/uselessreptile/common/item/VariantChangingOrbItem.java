package nordmods.uselessreptile.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
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
                if (DragonVariant.getByVariant(dragon.getDragonId(), variant, user.level()) != null) {
                    dragon.setVariant(variant);
                    stack.consume(1, user);
                    return InteractionResult.SUCCESS;
                } else {
                    user.displayClientMessage(Component.translatable("other.uselessreptile.variant_not_found", variant), true);
                }
            }
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable("tooltip.uselessreptile.variant_changing_orb"));
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }
}
