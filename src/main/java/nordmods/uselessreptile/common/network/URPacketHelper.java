package nordmods.uselessreptile.common.network;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class URPacketHelper {
    public static void playSound(LivingEntity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, int span) {
        if (entity.getServer() == null) return;
        BlockPos pos = entity.getBlockPos();
        PlaySoundS2CPacket packet = new PlaySoundS2CPacket(sound, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, entity.getRandom().nextInt(span));

        entity.getServer().getPlayerManager().sendToAll(packet);
    }
}
