package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URFluteModes;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.init.URResourceKeys;
import nordmods.uselessreptile.common.item.FluteItem;

import java.util.List;

public record FluteConfigurationComponent(EntityType<?> dragon, FluteItem.FluteMode currentMode, List<FluteItem.FluteMode> availableModes) {
    public static final FluteConfigurationComponent DEFAULT = new FluteConfigurationComponent(UREntities.RIVER_PIKEHORN, URFluteModes.GATHER, RiverPikehorn.FLUTE_MODES);
    public static final Codec<FluteConfigurationComponent> CODEC =  RecordCodecBuilder.create(instance -> instance.group(
            EntityType.CODEC.fieldOf("dragon").forGetter(FluteConfigurationComponent::dragon),
            URRegistries.FLUTE_MODE.byNameCodec().fieldOf("current_mode").forGetter(FluteConfigurationComponent::currentMode),
            URRegistries.FLUTE_MODE.byNameCodec().listOf().fieldOf("available_modes").forGetter(FluteConfigurationComponent::availableModes)
    ).apply(instance, FluteConfigurationComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluteConfigurationComponent> PACKET_CODEC = StreamCodec.of(
            (byteBuf, component) -> {
                EntityType.STREAM_CODEC.encode(byteBuf, component.dragon());
                ByteBufCodecs.registry(URResourceKeys.FLUTE_MODE).encode(byteBuf, component.currentMode);
                ByteBufCodecs.holderSet(URResourceKeys.FLUTE_MODE).encode(
                        byteBuf,
                        HolderSet.direct(Holder::direct, component.availableModes)
                );
            },
            (byteBuf) -> {
                EntityType<?> dragon = EntityType.STREAM_CODEC.decode(byteBuf);
                FluteItem.FluteMode currentMode = ByteBufCodecs.registry(URResourceKeys.FLUTE_MODE).decode(byteBuf);
                List<FluteItem.FluteMode> availableModes = ByteBufCodecs.holderSet(URResourceKeys.FLUTE_MODE).decode(byteBuf).stream().map(Holder::value).toList();
                return new FluteConfigurationComponent(dragon, currentMode, availableModes);
            });
}
