package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.event.DragonEquipmentTooltipEntryEvent;
import nordmods.uselessreptile.common.event.DragonOnItemConsumedEvent;
import nordmods.uselessreptile.common.event.MoleclawGetBlockMiningLevelEvent;
import nordmods.uselessreptile.common.network.URPacketHelper;

import java.util.ArrayList;
import java.util.List;

public class URModEvents {
    public static void init() {
        spawnLightningChaser();
        addDragonEquipmentTooltipEntries();
        getDefaultBlockMiningLevelForMoleclaw();
        onItemConsumedEvents();
    }

    private static void spawnLightningChaser() {
        //Lightning Chaser spawn event
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (world.isThundering()) {
                if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) return;
                if (world.useless_reptile$getTimer() > 0) {
                    world.useless_reptile$setTimer(world.useless_reptile$getTimer() - 1);
                    return;
                }
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player.getY() < 62) continue;
                    if (player.useless_reptile$getTimer() > 0) continue;
                    if (URConfig.getConfig().lightningChaserThunderstormSpawnChance >= player.getRandom().nextFloat() * 100) {
                        double cos = Math.cos(Math.toRadians(player.getHeadYaw() + 180)); //Lightning Chaser will always spawn behind the player
                        double sin = Math.sin(Math.toRadians(player.getHeadYaw() + 180));
                        BlockPos pos = player.getBlockPos();
                        BlockPos spawnPos = new BlockPos((int) (pos.getX() + sin * 128),
                                world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) (pos.getX() + sin * 128), (int) (pos.getZ() + cos * 128)) + 16,
                                (int) (pos.getZ() + cos * 128));
                        while (!world.getBlockState(spawnPos).isAir()) spawnPos = spawnPos.up();
                        if (DragonSpawnUtil.getAvailableVariants(world, spawnPos, EntityType.getId(UREntities.LIGHTNING_CHASER_ENTITY)).findFirst().isEmpty()) {
                            world.useless_reptile$setTimer(1200);
                            return;
                        }
                        LightningChaserEntity lightningChaser = UREntities.LIGHTNING_CHASER_ENTITY.spawn(world, spawnPos, SpawnReason.EVENT);
                        if (lightningChaser != null) {
                            lightningChaser.setFlying(true);
                            lightningChaser.setHomePoint(new BlockPos(pos.getX(),
                                    world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()),
                                    pos.getZ()));
                            URDragonEntity.SoundInfo soundInfo = lightningChaser.getSoundInfo("roar");
                            if (soundInfo != null)
                                URPacketHelper.playSound(lightningChaser, SoundEvent.of(soundInfo.id()), lightningChaser.getSoundCategory(), soundInfo.volume(), lightningChaser.getRandom().nextTriangular(soundInfo.pitch(), soundInfo.pitchDeviation()), 1);
                        }
                        player.useless_reptile$setTimer(URConfig.getConfig().lightningChaserThunderstormSpawnTimerCooldown);
                        break;
                    }
                }
                world.useless_reptile$setTimer(1200);
            }
        });
    }

    private static void addDragonEquipmentTooltipEntries() {
        DragonEquipmentTooltipEntryEvent.EVENT.register(item -> {
            List<EntityType<?>> entityTypes = new ArrayList<>();
            RegistryEntry<Item> entry = Registries.ITEM.getEntry(item);

            if (entry.isIn(URTags.MOLECLAW_TAIL_ARMOR)
                    || entry.isIn(URTags.MOLECLAW_CHESTPLATES)
                    || entry.isIn(URTags.MOLECLAW_HELMETS)
                    || entry.isIn(URTags.MOLECLAW_SADDLES))
                entityTypes.add(UREntities.MOLECLAW_ENTITY);

            if (entry.isIn(URTags.LIGHTNING_CHASER_TAIL_ARMOR)
                    || entry.isIn(URTags.LIGHTNING_CHASER_CHESTPLATES)
                    || entry.isIn(URTags.LIGHTNING_CHASER_HELMETS)
                    || entry.isIn(URTags.LIGHTNING_CHASER_SADDLES))
                entityTypes.add(UREntities.LIGHTNING_CHASER_ENTITY);

            if (entry.isIn(URTags.WYVERN_SADDLES))
                entityTypes.add(UREntities.WYVERN_ENTITY);

            return entityTypes;
        });
    }

    private static void getDefaultBlockMiningLevelForMoleclaw() {
        MoleclawGetBlockMiningLevelEvent.EVENT.register(blockState -> {
            if (blockState.isIn(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)) return 5;
            if (blockState.isIn(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) return 4;
            if (blockState.isIn(BlockTags.INCORRECT_FOR_IRON_TOOL)) return 3;
            if (blockState.isIn(BlockTags.INCORRECT_FOR_STONE_TOOL)) return 2;
            if (blockState.isIn(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) return 1;
            return 0;
        });
    }

    private static void onItemConsumedEvents() {
        DragonOnItemConsumedEvent.EVENT.register((user, original, remainder, hand) -> {
            if (original.isIn(ConventionalItemTags.ENTITY_WATER_BUCKETS)) {
                ItemStack toGive = Items.WATER_BUCKET.getDefaultStack();
                if (user instanceof PlayerEntity player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getStackInHand(hand).isEmpty()) player.giveItemStack(toGive);
                    else player.setStackInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }

            if (!original.getItem().getRecipeRemainder().isEmpty()) {
                ItemStack toGive = original.getItem().getRecipeRemainder();
                if (user instanceof PlayerEntity player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getStackInHand(hand).isEmpty()) player.giveItemStack(toGive);
                    else player.setStackInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }

            if (original.get(DataComponentTypes.USE_REMAINDER) != null) {
                ItemStack toGive = original.get(DataComponentTypes.USE_REMAINDER).convertInto();
                if (user instanceof PlayerEntity player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getStackInHand(hand).isEmpty()) player.giveItemStack(toGive);
                    else player.setStackInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }
        });
    }
}
