package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnUtil;
import nordmods.uselessreptile.common.entity.LightningChaser;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.event.DragonEquipmentTooltipEntryEvent;
import nordmods.uselessreptile.common.event.DragonOnItemConsumedEvent;
import nordmods.uselessreptile.common.event.MoleclawGetBlockMiningLevelEvent;
import nordmods.uselessreptile.common.network.URNetworkHelper;

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
                if (!world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) return;
                if (world.useless_reptile$getTimer() > 0) {
                    world.useless_reptile$setTimer(world.useless_reptile$getTimer() - 1);
                    return;
                }
                for (ServerPlayer player : world.players()) {
                    if (player.getY() < 62) continue;
                    if (player.useless_reptile$getTimer() > 0) continue;
                    if (URConfig.getConfig().lightningChaserThunderstormSpawnChance >= player.getRandom().nextFloat() * 100) {
                        double cos = Math.cos(Math.toRadians(player.getYHeadRot() + 180)); //Lightning Chaser will always spawn behind the player
                        double sin = Math.sin(Math.toRadians(player.getYHeadRot() + 180));
                        BlockPos pos = player.blockPosition();
                        BlockPos spawnPos = new BlockPos((int) (pos.getX() + sin * 128),
                                world.getHeight(Heightmap.Types.WORLD_SURFACE, (int) (pos.getX() + sin * 128), (int) (pos.getZ() + cos * 128)) + 16,
                                (int) (pos.getZ() + cos * 128));
                        while (!world.getBlockState(spawnPos).isAir()) spawnPos = spawnPos.above();
                        if (DragonSpawnUtil.getAvailableVariants(world, spawnPos, EntityType.getKey(UREntities.LIGHTNING_CHASER_ENTITY)).findFirst().isEmpty()) {
                            world.useless_reptile$setTimer(1200);
                            return;
                        }
                        LightningChaser lightningChaser = UREntities.LIGHTNING_CHASER_ENTITY.spawn(world, spawnPos, EntitySpawnReason.EVENT);
                        if (lightningChaser != null) {
                            lightningChaser.setFlying(true);
                            lightningChaser.setHomePoint(new BlockPos(pos.getX(),
                                    world.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()),
                                    pos.getZ()));
                            URDragonEntity.SoundInfo soundInfo = lightningChaser.getSoundInfo("roar");
                            if (soundInfo != null)
                                URNetworkHelper.playSound(lightningChaser, SoundEvent.createVariableRangeEvent(soundInfo.id()), lightningChaser.getSoundSource(), soundInfo.volume(), lightningChaser.getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()), 1);
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
            Holder<Item> entry = BuiltInRegistries.ITEM.wrapAsHolder(item);

            if (entry.is(URTags.MOLECLAW_TAIL_ARMOR)
                    || entry.is(URTags.MOLECLAW_CHESTPLATES)
                    || entry.is(URTags.MOLECLAW_HELMETS)
                    || entry.is(URTags.MOLECLAW_SADDLES))
                entityTypes.add(UREntities.MOLECLAW_ENTITY);

            if (entry.is(URTags.LIGHTNING_CHASER_TAIL_ARMOR)
                    || entry.is(URTags.LIGHTNING_CHASER_CHESTPLATES)
                    || entry.is(URTags.LIGHTNING_CHASER_HELMETS)
                    || entry.is(URTags.LIGHTNING_CHASER_SADDLES))
                entityTypes.add(UREntities.LIGHTNING_CHASER_ENTITY);

            if (entry.is(URTags.WYVERN_SADDLES))
                entityTypes.add(UREntities.WYVERN_ENTITY);

            return entityTypes;
        });
    }

    private static void getDefaultBlockMiningLevelForMoleclaw() {
        MoleclawGetBlockMiningLevelEvent.EVENT.register(blockState -> {
            if (blockState.is(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)) return 5;
            if (blockState.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) return 4;
            if (blockState.is(BlockTags.INCORRECT_FOR_IRON_TOOL)) return 3;
            if (blockState.is(BlockTags.INCORRECT_FOR_STONE_TOOL)) return 2;
            if (blockState.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) return 1;
            return 0;
        });
    }

    private static void onItemConsumedEvents() {
        DragonOnItemConsumedEvent.EVENT.register((user, original, remainder, hand) -> {
            if (original.is(ConventionalItemTags.ENTITY_WATER_BUCKETS)) {
                ItemStack toGive = Items.WATER_BUCKET.getDefaultInstance();
                if (user instanceof Player player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getItemInHand(hand).isEmpty()) player.addItem(toGive);
                    else player.setItemInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }

            if (!original.getItem().getCraftingRemainder().isEmpty()) {
                ItemStack toGive = original.getItem().getCraftingRemainder();
                if (user instanceof Player player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getItemInHand(hand).isEmpty()) player.addItem(toGive);
                    else player.setItemInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }

            if (original.get(DataComponents.USE_REMAINDER) != null) {
                ItemStack toGive = original.get(DataComponents.USE_REMAINDER).convertInto();
                if (user instanceof Player player && !player.isCreative()) {
                    if (!remainder.isEmpty() || !player.getItemInHand(hand).isEmpty()) player.addItem(toGive);
                    else player.setItemInHand(hand, toGive);
                }
                if (user instanceof URDragonEntity dragon) dragon.giveItemStack(toGive);
            }
        });
    }
}
