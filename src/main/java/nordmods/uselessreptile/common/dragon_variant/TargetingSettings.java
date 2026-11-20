package nordmods.uselessreptile.common.dragon_variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.List;

public record TargetingSettings(
        List<ExtraCodecs.TagOrElementLocation> untamedTargets,
        List<ExtraCodecs.TagOrElementLocation> tamedTargets,
        boolean attacksOwnerTarget,
        boolean defendsOwner
) {
    public static final Codec<TargetingSettings> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("untamed_targets").forGetter(TargetingSettings::untamedTargets),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("tamed_targets").forGetter(TargetingSettings::tamedTargets),
                    Codec.BOOL.fieldOf("attacks_owner_target").forGetter(TargetingSettings::attacksOwnerTarget),
                    Codec.BOOL.fieldOf("defends_owner").forGetter(TargetingSettings::defendsOwner)
            ).apply(instance, TargetingSettings::new)
    );

    public static final Codec<Holder<TargetingSettings>> CODEC = RegistryFileCodec.create(URResourceKeys.DRAGON_TARGETING_SETTINGS, DIRECT_CODEC);

    public boolean canTargetUntamed(LivingEntity target) {
        for (ExtraCodecs.TagOrElementLocation entry : untamedTargets) {
            if (entry.tag()) {
                if (target.is(TagKey.create(Registries.ENTITY_TYPE, entry.id()))) return true;
            } else {
                if (target.is(ResourceKey.create(Registries.ENTITY_TYPE, entry.id()))) return true;
            }
        }
        return false;
    }

    public boolean canTargetTamed(LivingEntity target) {
        for (ExtraCodecs.TagOrElementLocation entry : tamedTargets) {
            if (entry.tag()) {
                if (target.is(TagKey.create(Registries.ENTITY_TYPE, entry.id()))) return true;
            } else {
                if (target.is(ResourceKey.create(Registries.ENTITY_TYPE, entry.id()))) return true;
            }
        }
        return false;
    }
}
