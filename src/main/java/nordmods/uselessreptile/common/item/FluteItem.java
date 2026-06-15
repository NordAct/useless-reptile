package nordmods.uselessreptile.common.item;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FluteListener;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.item.component.FluteComponent;
import nordmods.uselessreptile.common.util.ComponentUtil;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.function.Consumer;

//todo expand functionality to other dragons
public class FluteItem extends Item {
    public static final ImmutableSortedMap<String, Pair<SoundEvent, FluteAction>> FLUTE_MODES = createFluteModeMap();
    public FluteItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.isShiftKeyDown()) {
            String nextMode = getNextMode(itemStack);
            itemStack.set(URItemComponents.FLUTE_MODE, new FluteComponent(nextMode));

            if (world.isClientSide() && user == Minecraft.getInstance().player) {
                Component text = Component.translatable("tooltip.uselessreptile.flute_mode." + getFluteMode(itemStack));
                Minecraft.getInstance().gui.hud.setOverlayMessage(text, false);
            }
            return InteractionResult.SUCCESS;
        }
        user.getCooldowns().addCooldown(itemStack, 40);
        if (user instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
            user.releaseUsingItem();
            user.gameEvent(URGameEvents.FLUTE_USED);
        }
        world.playLocalSound(user, getFluteSound(getFluteMode(itemStack)), SoundSource.PLAYERS, 2, 1);
        return InteractionResult.SUCCESS;
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
        textConsumer.accept(Component.translatable(tooltipString, Component.translatable(tooltipString + "." + getFluteMode(stack))).withStyle(ChatFormatting.GRAY));
    }

    public static SoundEvent getFluteSound(String mode) {
        Pair<SoundEvent, FluteAction> pair = FLUTE_MODES.get(mode);
        return pair != null ? pair.getFirst() : FLUTE_MODES.firstEntry().getValue().getFirst();
    }

    public static String getFluteMode(ItemStack stack) {
        return stack.getComponents().get(URItemComponents.FLUTE_MODE).mode();
    }

    public static FluteAction getFluteModeAction(ItemStack stack) {
        return FLUTE_MODES.get(getFluteMode(stack)).getSecond();
    }

    public static String getNextMode(ItemStack stack) {
        int currentOrdinal = FLUTE_MODES.keySet().asList().indexOf(getFluteMode(stack));
        int nextOrdinal = (currentOrdinal + 1) % FLUTE_MODES.size();
        return FLUTE_MODES.keySet().asList().get(nextOrdinal);
    }

    private static ImmutableSortedMap<String, Pair<SoundEvent, FluteAction>>createFluteModeMap() {
        HashMap<String, Pair<SoundEvent, FluteAction>> mutable = new HashMap<>();
        mutable.put("call", new Pair<>(URSoundEvent.FLUTE_CALL, dragon -> {
            if (!dragon.isOrderedToSit()) dragon.shouldFollow = true;
        }));
        mutable.put("gather", new Pair<>(URSoundEvent.FLUTE_GATHER, dragon -> {
            if (dragon instanceof FluteListener gathererDragon) gathererDragon.startGathering();
        }));
        mutable.put("target", new Pair<>(URSoundEvent.FLUTE_TARGET, dragon -> {
            if (!(dragon.getOwner() instanceof Player player)) return;

            int range = URGameEvents.FLUTE_USED.value().notificationRadius();
            Vec3 rot = player.getViewVector(1);
            EntityHitResult hitResult = ProjectileUtil
                    .getEntityHitResult(player,
                            player.getEyePosition(1),
                            player.getEyePosition(1).add(rot.scale(range)),
                            player.getBoundingBox().expandTowards(rot.scale(range)).inflate(1.0, 1.0, 1.0),
                            entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable(), range * range);

            if (hitResult != null) dragon.setTarget((LivingEntity) hitResult.getEntity());
        }));
        mutable.put("sit_down", new Pair<>(URSoundEvent.FLUTE_SIT_DOWN, dragon -> dragon.setOrderedToSit(true)));
        mutable.put("stand_up", new Pair<>(URSoundEvent.FLUTE_STAND_UP, dragon -> dragon.setOrderedToSit(false)));
        return ImmutableSortedMap.copyOf(mutable);
    }

    public interface FluteAction {
        void run(URDragonEntity dragon);
    }
}
