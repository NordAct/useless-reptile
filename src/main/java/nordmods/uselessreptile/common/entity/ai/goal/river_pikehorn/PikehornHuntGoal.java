package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class PikehornHuntGoal extends Goal {

    private final RiverPikehornEntity entity;
    private FishEntity fish;
    private BlockPos huntSpot;
    private boolean closeToSpot = false;
    private int calls;
    private BlockPos startingPos;

    public PikehornHuntGoal(RiverPikehornEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        //locating the hunt spot
        if (!entity.isHunting() || entity.getTarget() != null) return false;
        if (entity.isTamed() || startingPos == null) {
            startingPos = entity.getOwner() != null ? entity.getOwner().getBlockPos() : entity.getBlockPos();
            startingPos = adjustToWater(startingPos);
        }
        if (huntSpot == null) findFishyPlace(50);
        return huntSpot != null && startingPos != null;
    }

    @Override
    public void start() {
        calls = 0;
        entity.forceTargetInWater = true;
    }

    @Override
    public void stop() {
        stopHunt();
        entity.forceTargetInWater = false;
    }

    @Override
    public void tick() {
        if (isGoingToDrown()) entity.forceTargetInWater = false;
        if (!hasFish() && !tooManyCalls() && entity.forceTargetInWater) {
            //lookup for dropped fish first
            Box box = entity.getBoundingBox().expand(20);
            List<ItemEntity> drops = entity.getWorld().getEntitiesByClass(ItemEntity.class, box.withMinY(box.minY - 20), (item) -> {
                ItemStack itemStack = item.getStack();
                return itemStack.isIn(ItemTags.FISHES) && item.isAlive() && !item.cannotPickup();
            });

            if (!drops.isEmpty()) entity.getNavigation().startMovingTo(drops.get(0), 1);
            else {
                //check if fish is valid
                if (fish != null && (fish.isDead() || fish.isRemoved())) fish = null;

                if (fish == null) {
                    //checking if it's above water
                    if (aboveWater(huntSpot)) {
                        huntSpot = adjustToWater(huntSpot);
                        //checking if dragon is close to it or found target
                        FishEntity target = entity.getWorld().getClosestEntity(FishEntity.class,
                                TargetPredicate.DEFAULT, null,
                                entity.getX(), entity.getY(), entity.getZ(),
                                box.withMinY(box.minY - 30));
                        if (target != null) fish = target;
                        if (closeToSpot && target == null) {
                            findFishyPlace(30);
                            closeToSpot = false;
                        } else {
                            //if not close to the spot, move to it closer
                            double distance = huntSpot.getSquaredDistance(entity.getPos());
                            if (distance < 32) closeToSpot = true;
                            else entity.getNavigation().startMovingTo(huntSpot.getX(), huntSpot.getY(), huntSpot.getZ(), 1);
                        }
                    } else findFishyPlace(30);

                } else {
                    //kill the fish
                    double attackDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);
                    double distance = entity.squaredDistanceTo(fish);
                    entity.lookAtEntity(fish, entity.getRotationSpeed(), 180);
                    entity.getNavigation().startMovingTo(fish, 1);
                    if (entity.getPrimaryAttackCooldown() > 0 || distance >= attackDistance) return;
                    entity.attackMelee(fish);
                }
            }
        } else {
            if (entity.getAir() >= entity.getMaxAir() * 0.9 && !entity.forceTargetInWater) entity.forceTargetInWater = true;
            //else go back to starting pos
            entity.getNavigation().startMovingTo(startingPos.getX(), startingPos.getY() + 1, startingPos.getZ(), 1);
            if (entity.forceTargetInWater) {
                double distance = entity.getBlockPos().getSquaredDistance(startingPos.up());
                if (distance < entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f)) {
                    stopHunt();
                }
            }
        }
    }

    private boolean hasFish() {
        return !entity.getMainHandStack().isEmpty();
    }

    @Nullable
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = entity.getHeightMod() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = entity.getWorld().getBlockState(destination.up(y));
            if (!blockState.getFluidState().isEmpty()) {
                adjustment = y;
                break;
            }
        }
        return destination.up(adjustment);
    }

    @Nullable
    protected BlockPos findRandomAirSpot(int radius) {
        BlockPos div = null;
        for (int i = 0; i < 5; i++) {
            BlockPos fuzz = FuzzyPositions.localFuzz(entity.getRandom(), radius, 5);
            BlockPos result = entity.getBlockPos().add(fuzz);
            if (entity.getWorld().getBlockState(result).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

    private boolean biomeHasFish(BlockPos blockPos) {
        World world = entity.getWorld();
        Biome biome = world.getBiome(blockPos).value();
        List<SpawnSettings.SpawnEntry> entries = biome.getSpawnSettings().getSpawnEntries(SpawnGroup.WATER_AMBIENT).getEntries();
        return !entries.isEmpty();
    }

    //check if spot is above water
    private boolean aboveWater(BlockPos blockPos) {
        BlockPos pos = new BlockPos(blockPos);
        World world = entity.getWorld();

        while (world.getBlockState(pos).isOf(Blocks.AIR) && pos.getY() > -64) pos = pos.down();
        return world.getBlockState(pos).isOf(Blocks.WATER);
    }

    private void findFishyPlace(int radius) {
        calls++;
        boolean spotFound = false;
        for (int i = 0; i < 20; i++) {
            BlockPos newSpot = findRandomAirSpot(radius);
            //make sure spot is in the biome that has fish and not too far from initial spot
            if (newSpot != null && biomeHasFish(newSpot) && newSpot.getSquaredDistance(startingPos) < 4096) {
                huntSpot = newSpot;
                spotFound = true;
                break;
            }
        }

        if (!spotFound) stopHunt();
    }

    //adjusting to water height so the spot is always above water, but not too high
    private BlockPos adjustToWater(BlockPos blockPos) {
        if (!aboveWater(blockPos)) return blockPos;
        BlockPos pos = new BlockPos(blockPos);
        World world = entity.getWorld();

        while (!world.getBlockState(pos).isOf(Blocks.WATER) && pos.getY() > -64) pos = pos.down();
        return pos.up(3);
    }

    //check if findFishyPlace() was called too many times
    private boolean tooManyCalls() {
        return calls > 100;
    }

    private void stopHunt() {
        startingPos = null;
        entity.stopHunt();
    }

    private boolean isGoingToDrown() {
        return entity.getAir() < entity.getMaxAir() / 10;
    }
}
