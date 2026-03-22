package nordmods.uselessreptile.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ComponentUtil {
    public static List<Component> getParsedText(String translationKey, ChatFormatting... formats) {
        List<Component> toReturn = new ArrayList<>();

        if (I18n.exists(translationKey)) {
            String info = I18n.get(translationKey);
            String[] infoLines = info.split("\\r?\\n");
            for (String infoLine : infoLines) toReturn.add(Component.literal(infoLine).withStyle(formats));
        } else toReturn.add(Component.literal(I18n.get(translationKey)).withStyle(formats));

        return toReturn;
    }

    public static void addParsed(Consumer<Component> textConsumer, String translationKey, ChatFormatting... formats) {
        for (Component text : getParsedText(translationKey, formats)) textConsumer.accept((text));
    }

    public static void addHidden(Consumer<Component> textConsumer, Collection<Component> components, ChatFormatting... formats) {
        if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)) textConsumer.accept(Component.translatable("tooltip.uselessreptile.hidden").withStyle(ChatFormatting.DARK_GRAY));
        else for (Component text : components) textConsumer.accept(((MutableComponent) text).withStyle(formats));
    }
}
