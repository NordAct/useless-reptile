package nordmods.uselessreptile.integration;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
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
        public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
            if (!(entityAccessor.getEntity() instanceof URDragonEntity dragon)) return;
            iTooltip.add(Component.translatable(
                    "variant.uselessreptile",
                    dragon.isInvalidVariant() ?
                            Component.literal("UNREGISTERED VARIANT (" + dragon.getVariant() + ")").withStyle(ChatFormatting.DARK_RED) :
                            Component.translatable(dragon.getDragonActualVariant().variantNameKey()).withStyle(ChatFormatting.GOLD)
                    )
            );
        }

        @Override
        public Identifier getUid() {
            return UselessReptile.id("dragon_variant");
        }
    }
}
