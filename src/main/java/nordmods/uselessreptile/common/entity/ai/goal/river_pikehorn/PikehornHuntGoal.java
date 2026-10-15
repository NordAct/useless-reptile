package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.core.BlockPos;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class PikehornHuntGoal extends Goal {

    private final RiverPikehorn mob;
    private BlockPos huntSpot;
    private boolean closeToSpot = false;
    private int calls;
    private BlockPos startingPos;

    public PikehornHuntGoal(RiverPikehorn entity) {
        this.mob = entity;
    }

    @Override
    public boolean canUse() {
        //locating the hunt spot
        if (!mob.isHunting()) return false;
        if (mob.isTame() || startingPos == null) startingPos = mob.getOwner() != null ? mob.getOwner().blockPosition() : mob.getHomePoint();
        if (huntSpot == null) findFishyPlace(50);
        return huntSpot != null && startingPos != null;
    }

    @Override
    public void start() {
        calls = 0;
        mob.forceTargetInWater = true;
    }

    @Override
    public void stop() {
        mob.forceTargetInWater = false;
    }

    @Override
    public void tick() {
        if (isGoingToDrown() || hasFish()) mob.forceTargetInWater = false;

        if (!hasFish() && !tooManyCalls() && mob.forceTargetInWater) {
            if (mob.getPickupTarget() != null) {
                ItemEntity target = mob.getPickupTarget();
                if (target.isRemoved()) mob.setPickupTarget(null);
            }

            if (mob.getPickupTarget() == null) {
                //lookup for dropped fish first
                AABB box = mob.getBoundingBox().inflate(20);
                List<ItemEntity> drops = mob.level().getEntitiesOfClass(ItemEntity.class, box.setMinY(box.minY - 20), (item) -> {
                    ItemStack itemStack = item.getItem();
                    return mob.getFoodItem(itemStack) != null && item.isAlive() && !item.hasPickUpDelay();
                });

                if (drops.isEmpty()) {
                    if (mob.getTarget() != null) return;

                    //checking if it's above water
                    if (!aboveWater(huntSpot)) findFishyPlace(30);
                    else {
                        huntSpot = adjustToWater(huntSpot);
                        //checking if dragon is close to spot
                        if (closeToSpot) {
                            findFishyPlace(30);
                            closeToSpot = false;
                        } else {
                            //if not close to the spot, move to it closer
                            double distance = huntSpot.distToCenterSqr(mob.position());
                            if (distance < 32) closeToSpot = true;
                            else mob.getNavigation().moveTo(huntSpot.getX(), huntSpot.getY(), huntSpot.getZ(), 1);
                        }
                    }
                } else mob.setPickupTarget(drops.getFirst());
            } else mob.getNavigation().moveTo(mob.getPickupTarget(), 1);
        } else {
            if (mob.getAirSupply() >= mob.getMaxAirSupply() * 0.9 && !mob.forceTargetInWater) mob.forceTargetInWater = true;
            //else go back to starting position
            mob.setTarget(null);
            if (hasFish()) mob.setInAirTimer(mob.getMaxInAirTimer());
            mob.getNavigation().moveTo(startingPos.getX(), startingPos.getY(), startingPos.getZ(), 1);
            if (hasFish()) {
                double distance = mob.blockPosition().distSqr(startingPos.above());
                if (distance < mob.getBbWidth() * 2.0f * (mob.getBbWidth() * 2.0f)) stopHunt();
            }
        }
    }

    private boolean hasFish() {
        return !mob.getOffhandItem().isEmpty();
    }

    @Nullable
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = mob.getBbHeight() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = mob.level().getBlockState(destination.above(y));
            if (!blockState.getFluidState().isEmpty()) {
                adjustment = y;
                break;
            }
        }
        return destination.above(adjustment);
    }

    @Nullable
    protected BlockPos findRandomAirSpot(int radius) {
        BlockPos div = null;
        for (int i = 0; i < 5; i++) {
            BlockPos fuzz = RandomPos.generateRandomDirection(mob.getRandom(), radius, 5);
            BlockPos result = mob.blockPosition().offset(fuzz);
            if (mob.level().getBlockState(result).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

    private boolean biomeHasFish(BlockPos blockPos) {
        Level world = mob.level();
        Biome biome = world.getBiome(blockPos).value();
        List<Weighted<MobSpawnSettings.SpawnerData>> entries = biome.getMobSettings().getMobs(MobCategory.WATER_AMBIENT).unwrap();
        return !entries.isEmpty();
    }

    //check if spot is above water
    private boolean aboveWater(BlockPos blockPos) {
        BlockPos pos = new BlockPos(blockPos);
        Level world = mob.level();

        while (world.getBlockState(pos).is(Blocks.AIR) && pos.getY() > -64) pos = pos.below();
        return world.getBlockState(pos).is(Blocks.WATER);
    }

    private void findFishyPlace(int radius) {
        calls++;
        boolean spotFound = false;
        for (int i = 0; i < 20; i++) {
            BlockPos newSpot = findRandomAirSpot(radius);
            //make sure spot is in the biome that has fish and not too far from initial spot
            if (newSpot != null && biomeHasFish(newSpot) && newSpot.distSqr(startingPos) < 4096) {
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
        Level world = mob.level();

        while (!world.getBlockState(pos).is(Blocks.WATER) && pos.getY() > -64) pos = pos.below();
        return pos.above(3);
    }

    //check if findFishyPlace() was called too many times
    private boolean tooManyCalls() {
        return calls > 100;
    }

    private void stopHunt() {
        startingPos = null;

        mob.stopGathering();
    }

    private boolean isGoingToDrown() {
        return mob.getAirSupply() < mob.getMaxAirSupply() * 0.2;
    }
}
