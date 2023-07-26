package nordmods.uselessreptile.common.items;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URSounds;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluteItem extends Item {
    public static final String MODE_TAG = "Mode";
    public FluteItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getNbt();
        if (user.isSneaking()) {
            if (nbt == null || !nbt.contains(MODE_TAG)) {
                if (nbt == null) nbt = new NbtCompound();
                nbt.putInt(MODE_TAG, 1);
                if (world.isClient() && user == MinecraftClient.getInstance().player) {
                    Text text = Text.translatable("other.uselessreptile.flute_mode1");
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, false);
                }
                itemStack.setNbt(nbt);
                return TypedActionResult.success(itemStack);
            }

            int data = nbt.getInt(MODE_TAG);
            switch (data) {
                case 1 -> data = 2;
                case 2 -> data = 0;
                default -> data = 1;
            }
            nbt.putInt(MODE_TAG, data);

            if (world.isClient() && user == MinecraftClient.getInstance().player) {
                Text text = Text.translatable("other.uselessreptile.flute_mode" + data);
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, false);
            }
            return TypedActionResult.success(itemStack);
        } else {
            int mode = nbt == null || !nbt.contains(MODE_TAG) ? 0 : nbt.getInt(MODE_TAG);
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

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        int mode = nbt == null || !nbt.contains(MODE_TAG) ? 0 : nbt.getInt(MODE_TAG);
        String tooltipString = "other.uselessreptile.flute_mode" + mode;

        tooltip.add(Text.translatable(tooltipString).formatted(Formatting.GRAY));
    }
}
