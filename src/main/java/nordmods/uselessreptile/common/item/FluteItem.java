package nordmods.uselessreptile.common.item;

import com.google.common.collect.ImmutableSortedMap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.FluteListener;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.item.component.FluteComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

//todo expand functionality to other dragons
public class FluteItem extends Item {
    public static final ImmutableSortedMap<String, Pair<SoundEvent, FluteAction>> FLUTE_MODES = createFluteModeMap();
    public FluteItem(Settings settings) {
        super(settings);
        ItemStack itemStack = getDefaultStack();
        itemStack.set(URItems.FLUTE_MODE_COMPONENT, FluteComponent.DEFAULT);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            String nextMode = getNextMode(itemStack);
            itemStack.set(URItems.FLUTE_MODE_COMPONENT, new FluteComponent(nextMode));

            if (world.isClient() && user == MinecraftClient.getInstance().player) {
                Text text = Text.translatable("tooltip.uselessreptile.flute_mode." + getFluteMode(itemStack));
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, false);
            }
            return ActionResult.SUCCESS;
        }
        user.getItemCooldownManager().set(itemStack, 40);
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, itemStack);
            user.stopUsingItem();
            user.emitGameEvent(URGameEvents.FLUTE_USED);
        }
        world.playSoundFromEntityClient(user, getFluteSound(getFluteMode(itemStack)), SoundCategory.PLAYERS, 2, 1);
        return ActionResult.SUCCESS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
                if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT)) textConsumer.accept(Text.translatable("tooltip.uselessreptile.hidden").formatted(Formatting.DARK_GRAY));
        else for (Text text : getParsedText("tooltip.uselessreptile.flute")) textConsumer.accept(((MutableText) text).formatted(Formatting.GRAY));
        String tooltipString = "tooltip.uselessreptile.flute_mode";
        textConsumer.accept(Text.translatable(tooltipString, Text.translatable(tooltipString + "." + getFluteMode(stack))).formatted(Formatting.GRAY));
    }

    private static List<Text> getParsedText(String key) {
        List<Text> toReturn = new ArrayList<>();

        if (I18n.hasTranslation(key)) {
            String info = I18n.translate(key);
            String[] infoLines = info.split("\\r?\\n");
            for (String infoLine : infoLines) toReturn.add(Text.literal(infoLine));
        } else toReturn.add(Text.literal(I18n.translate(key)));

        return toReturn;
    }

    public static SoundEvent getFluteSound(String mode) {
        Pair<SoundEvent, FluteAction> pair = FLUTE_MODES.get(mode);
        return pair != null ? pair.getLeft() : FLUTE_MODES.firstEntry().getValue().getLeft();
    }

    public static String getFluteMode(ItemStack stack) {
        return stack.getComponents().get(URItems.FLUTE_MODE_COMPONENT).mode();
    }

    public static FluteAction getFluteModeAction(ItemStack stack) {
        return FLUTE_MODES.get(getFluteMode(stack)).getRight();
    }

    public static String getNextMode(ItemStack stack) {
        int currentOrdinal = FLUTE_MODES.keySet().asList().indexOf(getFluteMode(stack));
        int nextOrdinal = (currentOrdinal + 1) % FLUTE_MODES.size();
        return FLUTE_MODES.keySet().asList().get(nextOrdinal);
    }

    private static ImmutableSortedMap<String, Pair<SoundEvent, FluteAction>>createFluteModeMap() {
        HashMap<String, Pair<SoundEvent, FluteAction>> mutable = new HashMap<>();
        mutable.put("call", new Pair<>(URSounds.FLUTE_CALL, dragon -> dragon.shouldFollow = true));
        mutable.put("gather", new Pair<>(URSounds.FLUTE_GATHER, dragon -> {
            if (dragon instanceof FluteListener gathererDragon) gathererDragon.startGathering();
        }));
        mutable.put("target", new Pair<>(URSounds.FLUTE_TARGET, dragon -> {
            if (!(dragon.getOwner() instanceof PlayerEntity player)) return;

            int range = URGameEvents.FLUTE_USED.value().notificationRadius();
            Vec3d rot = player.getRotationVec(1);
            EntityHitResult hitResult = ProjectileUtil
                    .raycast(player,
                            player.getCameraPosVec(1),
                            player.getCameraPosVec(1).add(rot.multiply(range)),
                            player.getBoundingBox().stretch(rot.multiply(range)).expand(1.0, 1.0, 1.0),
                            entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.canHit(), range * range);

            if (hitResult != null) dragon.setTarget((LivingEntity) hitResult.getEntity());
        }));
        mutable.put("sit_down", new Pair<>(URSounds.FLUTE_SIT_DOWN, dragon -> dragon.setSitting(true)));
        mutable.put("stand_up", new Pair<>(URSounds.FLUTE_STAND_UP, dragon -> dragon.setSitting(false)));
        return ImmutableSortedMap.copyOf(mutable);
    }

    public interface FluteAction {
        void run(URDragonEntity dragon);
    }
}
