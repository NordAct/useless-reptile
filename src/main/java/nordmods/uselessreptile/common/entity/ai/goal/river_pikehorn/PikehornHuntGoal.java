package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PikehornHuntGoal extends Goal {

    private final RiverPikehornEntity entity;
    private AbstractFish fish;
    private BlockPos huntSpot;
    private boolean closeToSpot = false;
    private int calls;
    private BlockPos startingPos;

    public PikehornHuntGoal(RiverPikehornEntity entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        //locating the hunt spot
        if (!entity.isHunting() || entity.getTarget() != null) return false;
        if (entity.isTame() || startingPos == null) startingPos = entity.getOwner() != null ? entity.getOwner().blockPosition() : entity.getHomePoint();
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
            AABB box = entity.getBoundingBox().inflate(20);
            List<ItemEntity> drops = entity.level().getEntitiesOfClass(ItemEntity.class, box.setMinY(box.minY - 20), (item) -> {
                ItemStack itemStack = item.getItem();
                return entity.getFoodItem(itemStack) != null && item.isAlive() && !item.hasPickUpDelay();
            });

            if (!drops.isEmpty()) entity.getNavigation().moveTo(drops.getFirst(), 1);
            else {
                //check if fish is valid
                if (fish != null && (fish.isDeadOrDying() || fish.isRemoved())) fish = null;

                if (fish == null) {
                    //checking if it's above water
                    if (aboveWater(huntSpot)) {
                        huntSpot = adjustToWater(huntSpot);
                        //checking if dragon is close to it or found target
                        List<AbstractFish> list = entity.level().getEntitiesOfClass(AbstractFish.class,  box.setMinY(box.minY - 30), entity::canAttack);
                        AbstractFish target = null;
                        if (!list.isEmpty()) {
                            target = list.getFirst();
                            for (AbstractFish entry : list) {
                                if (entity.distanceToSqr(entry) < entity.distanceToSqr(target)) target = entry;
                            }
                        }
                        if (target != null) fish = target;
                        if (closeToSpot && target == null) {
                            findFishyPlace(30);
                            closeToSpot = false;
                        } else {
                            //if not close to the spot, move to it closer
                            double distance = huntSpot.distToCenterSqr(entity.position());
                            if (distance < 32) closeToSpot = true;
                            else entity.getNavigation().moveTo(huntSpot.getX(), huntSpot.getY(), huntSpot.getZ(), 1);
                        }
                    } else findFishyPlace(30);

                } else {
                    //kill the fish
                    entity.getLookControl().setLookAt(fish);
                    entity.getNavigation().moveTo(fish, 1);
                    if (entity.getPrimaryAttackCooldown() > 0) return;
                    if (entity.getAttackBoundingBox().intersects(fish.getBoundingBox())) entity.attackMelee(fish);
                }
            }
        } else {
            if (entity.getAirSupply() >= entity.getMaxAirSupply() * 0.9 && !entity.forceTargetInWater) entity.forceTargetInWater = true;
            //else go back to starting pos
            entity.getNavigation().moveTo(startingPos.getX(), startingPos.getY(), startingPos.getZ(), 1);
            if (entity.forceTargetInWater) {
                double distance = entity.blockPosition().distSqr(startingPos.above());
                if (distance < entity.getBbWidth() * 2.0f * (entity.getBbWidth() * 2.0f)) stopHunt();
            }
        }
    }

    private boolean hasFish() {
        return !entity.getMainHandItem().isEmpty();
    }

    @Nullable
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = entity.getHeightMod() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = entity.level().getBlockState(destination.above(y));
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
            BlockPos fuzz = RandomPos.generateRandomDirection(entity.getRandom(), radius, 5);
            BlockPos result = entity.blockPosition().offset(fuzz);
            if (entity.level().getBlockState(result).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

    private boolean biomeHasFish(BlockPos blockPos) {
        Level world = entity.level();
        Biome biome = world.getBiome(blockPos).value();
        List<Weighted<MobSpawnSettings.SpawnerData>> entries = biome.getMobSettings().getMobs(MobCategory.WATER_AMBIENT).unwrap();
        return !entries.isEmpty();
    }

    //check if spot is above water
    private boolean aboveWater(BlockPos blockPos) {
        BlockPos pos = new BlockPos(blockPos);
        Level world = entity.level();

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
        Level world = entity.level();

        while (!world.getBlockState(pos).is(Blocks.WATER) && pos.getY() > -64) pos = pos.below();
        return pos.above(3);
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
        return entity.getAirSupply() < entity.getMaxAirSupply() / 10;
    }
}
