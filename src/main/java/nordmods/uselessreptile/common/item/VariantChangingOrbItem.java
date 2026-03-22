package nordmods.uselessreptile.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.item.component.DragonVariantComponent;
import nordmods.uselessreptile.common.network.s2c.OpenVariantChangingOrbScreenPayload;
import nordmods.uselessreptile.common.util.ComponentUtil;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class VariantChangingOrbItem extends Item {
    public VariantChangingOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (URDragonPart.getPartParent(user) instanceof URDragonEntity dragon) entity = dragon;
        if (entity instanceof URDragonEntity dragon && (dragon.getOwner() == user || user.isCreative())) {
            DragonVariantComponent component = user.getItemInHand(hand).get(URItemComponents.DRAGON_VARIANT);
            if (component.type() == dragon.getVariantType() && DragonVariant.get(component.type(), component.variant(), user.level()) != null) {
                if (!user.level().isClientSide()) {
                    dragon.setVariant(component.variant());
                    stack.consume(1, user);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        ComponentUtil.addHidden(consumer, ComponentUtil.getParsedText("tooltip.uselessreptile.variant_changing_orb"), ChatFormatting.GRAY);
        DragonVariantComponent component = itemStack.get(URItemComponents.DRAGON_VARIANT);
        DragonVariant variant = DragonVariant.get(component.type(), component.variant(), Minecraft.getInstance().level);
        if (variant != null) {
            consumer.accept(Component.translatable("tooltip.uselessreptile.can_be_applied_to", Component.translatable(component.type().getTranslationKey())).withStyle(ChatFormatting.GRAY));
            variant.common().displayNameKey().ifPresent(key -> {
                consumer.accept(Component.translatable("tooltip.uselessreptile.dragon_display_name", Component.translatable(key)).withStyle(ChatFormatting.GRAY));
            });
            consumer.accept(Component.translatable("tooltip.uselessreptile.dragon_variant", Component.translatable(variant.common().variantNameKey())).withStyle(ChatFormatting.GRAY));

        }
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.isCreative()) {
            DragonVariantComponent component = player.getItemInHand(hand).get(URItemComponents.DRAGON_VARIANT);
            OpenVariantChangingOrbScreenPayload.send(serverPlayer, component.type(), component.variant());
        }
        return InteractionResult.SUCCESS;
    }
}
