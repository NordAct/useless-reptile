package nordmods.uselessreptile.common.item;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
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
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

//todo expand functionality to other dragons
public class FluteItem extends Item {
    public static final ImmutableSortedMap<String, Tuple<SoundEvent, FluteAction>> FLUTE_MODES = createFluteModeMap();
    public FluteItem(Properties settings) {
        super(settings);
        ItemStack itemStack = getDefaultInstance();
        itemStack.set(URItemComponents.FLUTE_MODE, FluteComponent.DEFAULT);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.isShiftKeyDown()) {
            String nextMode = getNextMode(itemStack);
            itemStack.set(URItemComponents.FLUTE_MODE, new FluteComponent(nextMode));

            if (world.isClientSide() && user == Minecraft.getInstance().player) {
                Component text = Component.translatable("tooltip.uselessreptile.flute_mode." + getFluteMode(itemStack));
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
                if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)) textConsumer.accept(Component.translatable("tooltip.uselessreptile.hidden").withStyle(ChatFormatting.DARK_GRAY));
        else for (Component text : getParsedText()) textConsumer.accept(((MutableComponent) text).withStyle(ChatFormatting.GRAY));
        String tooltipString = "tooltip.uselessreptile.flute_mode";
        textConsumer.accept(Component.translatable(tooltipString, Component.translatable(tooltipString + "." + getFluteMode(stack))).withStyle(ChatFormatting.GRAY));
    }

    private static List<Component> getParsedText() {
        List<Component> toReturn = new ArrayList<>();

        if (I18n.exists("tooltip.uselessreptile.flute")) {
            String info = I18n.get("tooltip.uselessreptile.flute");
            String[] infoLines = info.split("\\r?\\n");
            for (String infoLine : infoLines) toReturn.add(Component.literal(infoLine));
        } else toReturn.add(Component.literal(I18n.get("tooltip.uselessreptile.flute")));

        return toReturn;
    }

    public static SoundEvent getFluteSound(String mode) {
        Tuple<SoundEvent, FluteAction> pair = FLUTE_MODES.get(mode);
        return pair != null ? pair.getA() : FLUTE_MODES.firstEntry().getValue().getA();
    }

    public static String getFluteMode(ItemStack stack) {
        return stack.getComponents().get(URItemComponents.FLUTE_MODE).mode();
    }

    public static FluteAction getFluteModeAction(ItemStack stack) {
        return FLUTE_MODES.get(getFluteMode(stack)).getB();
    }

    public static String getNextMode(ItemStack stack) {
        int currentOrdinal = FLUTE_MODES.keySet().asList().indexOf(getFluteMode(stack));
        int nextOrdinal = (currentOrdinal + 1) % FLUTE_MODES.size();
        return FLUTE_MODES.keySet().asList().get(nextOrdinal);
    }

    private static ImmutableSortedMap<String, Tuple<SoundEvent, FluteAction>>createFluteModeMap() {
        HashMap<String, Tuple<SoundEvent, FluteAction>> mutable = new HashMap<>();
        mutable.put("call", new Tuple<>(URSoundEvent.FLUTE_CALL, dragon -> {
            if (!dragon.isOrderedToSit()) dragon.shouldFollow = true;
        }));
        mutable.put("gather", new Tuple<>(URSoundEvent.FLUTE_GATHER, dragon -> {
            if (dragon instanceof FluteListener gathererDragon) gathererDragon.startGathering();
        }));
        mutable.put("target", new Tuple<>(URSoundEvent.FLUTE_TARGET, dragon -> {
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
        mutable.put("sit_down", new Tuple<>(URSoundEvent.FLUTE_SIT_DOWN, dragon -> dragon.setOrderedToSit(true)));
        mutable.put("stand_up", new Tuple<>(URSoundEvent.FLUTE_STAND_UP, dragon -> dragon.setOrderedToSit(false)));
        return ImmutableSortedMap.copyOf(mutable);
    }

    public interface FluteAction {
        void run(URDragonEntity dragon);
    }
}
