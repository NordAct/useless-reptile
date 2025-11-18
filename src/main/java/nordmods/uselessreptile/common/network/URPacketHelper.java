package nordmods.uselessreptile.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

public class URPacketHelper {
    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource category, float volume, float pitch, int span) {
        if (entity.level().getServer() == null) return;
        BlockPos pos = entity.blockPosition();
        ClientboundSoundPacket packet = new ClientboundSoundPacket(Holder.direct(sound), category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, entity.getRandom().nextInt(span));

        entity.level().getServer().getPlayerList().broadcastAll(packet);
    }

    public static void playSound(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, int span) {
        if (entity.level().getServer() == null) return;
        BlockPos pos = entity.blockPosition();
        ClientboundSoundPacket packet = new ClientboundSoundPacket(Holder.direct(sound), category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, new LegacyRandomSource(entity.getId()).nextInt(span));

        entity.level().getServer().getPlayerList().broadcastAll(packet);
    }
}
