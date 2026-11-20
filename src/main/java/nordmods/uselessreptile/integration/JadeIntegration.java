package nordmods.uselessreptile.integration;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(JadeDragonVariantProvider.INSTANCE, URDragonEntity.class);
    }

    protected static class JadeDragonVariantProvider implements IEntityComponentProvider {
        public static final JadeDragonVariantProvider INSTANCE = new JadeDragonVariantProvider();
        @Override
        public void appendTooltip(@NonNull ITooltip iTooltip, EntityAccessor entityAccessor, @NonNull IPluginConfig iPluginConfig) {
            if (!(entityAccessor.getEntity() instanceof URDragonEntity dragon)) return;
            iTooltip.add(Component.translatable(
                    "variant.uselessreptile",
                    dragon.isInvalidVariant() ?
                            Component.literal("UNREGISTERED VARIANT (" + dragon.getVariant() + ")").withStyle(ChatFormatting.DARK_RED) :
                            Component.translatable(dragon.getDragonVariant().common().variantNameKey()).withStyle(ChatFormatting.GOLD)
                    )
            );
            if (!dragon.isTame()) {
                if (dragon.isTameable()) {
                    float progress = (float) (dragon.getBaseTamingProgress() - dragon.getTamingProgress()) * 100f / (float) Math.max(dragon.getBaseTamingProgress(), 1);
                    ChatFormatting color;
                    if (progress >= 100) color = ChatFormatting.AQUA;
                    else if (progress > 66.66667f) color = ChatFormatting.GREEN;
                    else if (progress > 33.333334f) color = ChatFormatting.YELLOW;
                    else color = ChatFormatting.RED;
                    iTooltip.add(Component.translatable("variant.uselessreptile.taming_progress",
                            Component.literal(String.format("%.1f", progress))
                                    .withStyle(color)
                                    .append("%")
                    ));
                } else iTooltip.add(Component.translatable("variant.uselessreptile.untameable"));
            }
        }

        @Override
        public Identifier getUid() {
            return UselessReptile.id("dragon_info");
        }
    }
}
