package nordmods.uselessreptile.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.item.component.FluteConfigurationComponent;
import nordmods.uselessreptile.common.util.ComponentUtil;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class FluteItem extends Item {
    public FluteItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.isShiftKeyDown()) {
            FluteMode nextMode = getNextMode(itemStack);
            FluteConfigurationComponent component = getFluteConfig(itemStack);
            itemStack.set(URItemComponents.FLUTE_CONFIGURATION, new FluteConfigurationComponent(component.dragon(), nextMode, component.availableModes()));

            if (world.isClientSide() && user == Minecraft.getInstance().player) {
                Component text = Component.translatable(getFluteModeLocalisationKey(itemStack));
                Minecraft.getInstance().gui.setOverlayMessage(text, false);
            }
            return InteractionResult.SUCCESS;
        }
        user.getCooldowns().addCooldown(itemStack, 40);
        if (user instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
            user.releaseUsingItem();
            user.gameEvent(URGameEvents.FLUTE_USED);
        }
        world.playLocalSound(user, getFluteMode(itemStack).sound(), SoundSource.PLAYERS, 2, 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack itemStack, @NonNull Player player, @NonNull LivingEntity target, @NonNull InteractionHand type) {
        if (target instanceof URDragonEntity dragonEntity) {
            FluteConfigurationComponent component = new FluteConfigurationComponent(dragonEntity.getType(), dragonEntity.getPermittedFluteModes().getFirst(), dragonEntity.getPermittedFluteModes());
            itemStack.set(URItemComponents.FLUTE_CONFIGURATION, component);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(itemStack, player, target, type);
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        ComponentUtil.addHidden(textConsumer, ComponentUtil.getParsedText("tooltip.uselessreptile.flute"), ChatFormatting.GRAY);
        String tooltipString = "tooltip.uselessreptile.flute_mode";
        textConsumer.accept(Component.translatable(tooltipString, Component.translatable(getFluteModeLocalisationKey(stack))).withStyle(ChatFormatting.GRAY));
    }

    public static String getFluteModeLocalisationKey(ItemStack stack) {
        Identifier fluteModeId = URRegistries.FLUTE_MODE.getKey(getFluteMode(stack));
        return "tooltip." + fluteModeId.getNamespace() + ".flute_mode." + fluteModeId.getPath();
    }

    public static FluteMode getFluteMode(ItemStack stack) {
        return getFluteConfig(stack).currentMode();
    }

    public static FluteConfigurationComponent getFluteConfig(ItemStack stack) {
        return stack.getComponents().get(URItemComponents.FLUTE_CONFIGURATION);
    }

    public static FluteMode getNextMode(ItemStack stack) {
        FluteConfigurationComponent config = getFluteConfig(stack);
        int currentOrdinal = config.availableModes().indexOf(config.currentMode());
        int nextOrdinal = (currentOrdinal + 1) % config.availableModes().size();
        return config.availableModes().get(nextOrdinal);
    }

    public static record FluteMode(SoundEvent sound, FluteAction action) {

        public interface FluteAction {
            void run(URDragonEntity dragon);
        }
    }
}
