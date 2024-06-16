package nordmods.uselessreptile.common.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.item.component.FluteComponent;

import java.util.ArrayList;
import java.util.List;

public class FluteItem extends Item {
    public FluteItem(net.minecraft.item.Item.Settings settings) {
        super(settings);
        ItemStack itemStack = getDefaultStack();
        itemStack.set(URItems.FLUTE_MODE_COMPONENT, FluteComponent.DEFAULT);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
       int mode = getFluteMode(itemStack);
        if (user.isSneaking()) {
            switch (mode) {
                case 1 -> mode = 2;
                case 2 -> mode = 0;
                default -> mode = 1;
            }
            itemStack.set(URItems.FLUTE_MODE_COMPONENT, new FluteComponent((byte) mode));

            if (world.isClient() && user == MinecraftClient.getInstance().player) {
                Text text = Text.translatable("tooltip.uselessreptile.flute_mode" + mode);
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, false);
            }
            return TypedActionResult.success(itemStack);
        } else {
            user.getItemCooldownManager().set(this, 40);
            if (user instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                user.stopUsingItem();
                switch (mode) {
                    case 1 -> world.playSoundFromEntity(null, user, URSounds.FLUTE_GATHER, SoundCategory.PLAYERS, 10, 1);
                    case 2 -> world.playSoundFromEntity(null, user, URSounds.FLUTE_TARGET, SoundCategory.PLAYERS, 10, 1);
                    default -> world.playSoundFromEntity(null, user, URSounds.FLUTE_CALL, SoundCategory.PLAYERS, 10, 1);
                }
            }
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int mode = getFluteMode(itemStack);
        String tooltipString = "tooltip.uselessreptile.flute_mode" + mode;

        if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_SHIFT)) tooltip.add(Text.translatable("tooltip.uselessreptile.hidden").formatted(Formatting.DARK_GRAY));
        else for (Text text : getParsedText("tooltip.uselessreptile.flute")) tooltip.add(((MutableText) text).formatted(Formatting.GRAY));

        tooltip.add(Text.translatable(tooltipString).formatted(Formatting.GRAY));
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

    public static int getFluteMode(ItemStack itemStack) {
        return itemStack.getComponents().get(URItems.FLUTE_MODE_COMPONENT).mode();
    }
}
