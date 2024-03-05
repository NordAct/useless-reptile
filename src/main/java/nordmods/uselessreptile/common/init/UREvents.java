package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.network.URPacketHelper;
import nordmods.uselessreptile.common.util.LightningChaserSpawnTimer;

public class UREvents {

    public static void init() {
        //Lightning Chaser spawn event
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (world instanceof LightningChaserSpawnTimer timer && world.isThundering()) {
                if (timer.getTimer() > 0) timer.setTimer(timer.getTimer() - 1);
                else {
                    for (ServerPlayerEntity player : world.getPlayers()) {
                        if (player.getY() < 60) continue;
                        if (URConfig.getConfig().lightningChaserThunderstormSpawnChance >= player.getRandom().nextFloat() * 100) {
                            double cos = Math.cos(Math.toRadians(player.getHeadYaw() + 180));
                            double sin = Math.sin(Math.toRadians(player.getHeadYaw() + 180));
                            BlockPos pos = player.getBlockPos();
                            BlockPos spawnPos = new BlockPos((int) (pos.getX() + sin * 128),
                                    world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) (pos.getX() + sin * 128), (int) (pos.getZ() + cos * 128)) + 16,
                                    (int) (pos.getZ() + cos * 128));
                            while (!world.getBlockState(spawnPos).isAir()) spawnPos = spawnPos.up();

                            LightningChaserEntity lightningChaser = UREntities.LIGHTNING_CHASER_ENTITY.spawn(world, spawnPos, SpawnReason.EVENT);
                            if (lightningChaser != null) {
                                lightningChaser.setFlying(true);
                                lightningChaser.roamingSpot = new BlockPos(pos.getX(),
                                        world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()),
                                        pos.getZ());
                                URPacketHelper.playSound(lightningChaser, URSounds.LIGHTNING_CHASER_DISTANT_ROAR, lightningChaser.getSoundCategory(), 1, 1, 1);
                            }
                            break;
                        }
                    }
                    timer.setTimer(600);
                }
            }
        });
    }
}
