package nordmods.uselessreptile.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.CheckedRandom;

import java.util.Random;

public class URPacketHelper {
    public static void playSound(LivingEntity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, int span) {
        if (entity.getServer() == null) return;
        BlockPos pos = entity.getBlockPos();
        PlaySoundS2CPacket packet = new PlaySoundS2CPacket(RegistryEntry.of(sound), category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, entity.getRandom().nextInt(span));

        entity.getServer().getPlayerManager().sendToAll(packet);
    }

    public static void playSound(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, int span) {
        if (entity.getServer() == null) return;
        BlockPos pos = entity.getBlockPos();
        PlaySoundS2CPacket packet = new PlaySoundS2CPacket(RegistryEntry.of(sound), category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, new CheckedRandom(entity.getId()).nextInt(span));

        entity.getServer().getPlayerManager().sendToAll(packet);
    }
}
