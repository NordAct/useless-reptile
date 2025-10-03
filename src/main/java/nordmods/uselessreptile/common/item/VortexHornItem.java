package nordmods.uselessreptile.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class VortexHornItem extends GoatHornItem {
    public VortexHornItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (getPartParent(user) instanceof URDragonEntity dragon) entity = dragon;
        if (entity instanceof URDragonEntity dragon && dragon.getOwner() == user && !user.isSneaking()) {
            if (tryCollectDragon(stack, user, dragon, hand, true)) {
                user.stopUsingItem();
                user.playSound(URSounds.VORTEX_HORN_SUCK_IN);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (tryMassCatchOrRelease(stack, user, world, hand)) return ActionResult.SUCCESS;
        }
        if (getPartParent(user) instanceof URDragonEntity dragon) {
            useOnEntity(stack, user, dragon, hand);
            user.stopUsingItem();
            return ActionResult.SUCCESS;
        }
        ActionResult result = super.use(world, user, hand);
        user.getItemCooldownManager().set(stack, 0);
        return result;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        if (context.getPlayer() instanceof PlayerEntity user) {
            World world = context.getWorld();
            Hand hand = context.getHand();
            if (user.isSneaking()) {
                if (tryMassCatchOrRelease(stack, user, world, hand)) {
                    user.stopUsingItem();
                    return ActionResult.SUCCESS;
                }
            }
            BlockPos pos = context.getBlockPos();
            BlockState blockState = world.getBlockState(pos);
            Direction direction = context.getSide();
            if (!blockState.getCollisionShape(world, pos).isEmpty()) pos = pos.offset(direction);

            if (tryCreateDragon(stack, user, world, hand, pos)) {
                user.stopUsingItem();
                user.playSound(URSounds.VORTEX_HORN_SPIT_OUT);
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(
            ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type
    )  {
        if (stack.getComponents().contains(URItems.DRAGON_STORAGE_COMPONENT)) {
            URDragonDataStorageComponent dataComponent = stack.get(URItems.DRAGON_STORAGE_COMPONENT);
            if (dataComponent != null) {
                boolean full = getCurrentCapacity(stack) >= getMaxCapacity(stack);
                textConsumer.accept(Text.translatable("tooltip.uselessreptile.vortex_horn.capacity",getCurrentCapacity(stack) , getMaxCapacity(stack)).formatted(full ? Formatting.YELLOW : Formatting.GRAY));
                if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), InputUtil.GLFW_KEY_LEFT_SHIFT)) textConsumer.accept(Text.translatable("tooltip.uselessreptile.hidden").formatted(Formatting.DARK_GRAY));
                else {
                    textConsumer.accept(Text.translatable("tooltip.uselessreptile.vortex_horn.contained_dragons"));
                    for (NbtComponent nbtComponent : dataComponent.entityData()) {
                        NbtCompound nbt = nbtComponent.copyNbt();
                        if (nbt.contains("CustomName")) {
                            Text customName = nbt.get("CustomName", TextCodecs.CODEC).orElse(Text.empty());
                            textConsumer.accept(customName);
                        } else {
                            String string = nbt.getString("id").orElse("");
                            Optional<EntityType<?>> entityType = EntityType.get(string);
                            textConsumer.accept(entityType.map(value -> Text.translatable(value.getTranslationKey())).orElseGet(() -> Text.literal("ERROR").formatted(Formatting.RED)));
                        }
                    }
                }
            }
            textConsumer.accept(Text.empty());
        }
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }

    protected boolean tryMassCatchOrRelease(ItemStack stack, PlayerEntity user, World world, Hand hand) {
        Box box = new Box(user.getBlockPos()).expand(2);
        List<URDragonEntity> dragons = world.getEntitiesByClass(URDragonEntity.class, box, entity -> entity.getOwner() == user && !entity.isSitting());
        int leastCapacity = 0;
        if (!dragons.isEmpty()) {
            leastCapacity = dragons.getFirst().vortexHornCapacity();
            for (URDragonEntity dragon : dragons) leastCapacity = Math.min(leastCapacity, dragon.vortexHornCapacity());
        }
        if (leastCapacity <= 0 || getCurrentCapacity(stack) + leastCapacity > getMaxCapacity(stack)) {
            URDragonDataStorageComponent dataComponent = stack.get(URItems.DRAGON_STORAGE_COMPONENT);
            if (dataComponent != null && getCurrentCapacity(stack) > 0) {
                for (int i = 0; i < dataComponent.entityData().size(); i++) tryCreateDragon(stack, user, world, hand, user.getBlockPos());
                user.playSound(URSounds.VORTEX_HORN_SPIT_OUT);
                return true;
            }
        } else {
            dragons.sort(Comparator.comparingDouble((dragon) -> dragon.squaredDistanceTo(dragon.getOwner())));
            for (URDragonEntity dragon : dragons) {
                if (dragon.isSitting()) continue;
                if (!tryCollectDragon(stack, user, dragon, hand, false)) break;
            }
            user.playSound(URSounds.VORTEX_HORN_SUCK_IN);
            return true;
        }
        return false;
    }

    protected boolean tryCollectDragon(ItemStack stack, PlayerEntity user, URDragonEntity dragon, Hand hand, boolean capacityWarning) {
        int dragonCapacity = dragon.vortexHornCapacity();
        if (getCurrentCapacity(stack) + dragonCapacity > getMaxCapacity(stack)) {
            if (capacityWarning && !user.getEntityWorld().isClient()) user.sendMessage(Text.translatable("other.uselessreptile.not_enough_capacity"), true);
            return false;
        }

        if (user.getEntityWorld().isClient()) return true;

        dragon.stopRiding();
        dragon.removeAllPassengers();

        URDragonDataStorageComponent dataComponent = stack.get(URItems.DRAGON_STORAGE_COMPONENT);
        URDragonDataStorageComponent appliedComponent;
        if (dataComponent != null) {
            List<NbtComponent> dragons = new ArrayList<>(dataComponent.entityData());
            NbtComponent data = URDragonDataStorageComponent.createData(dragon);
            dragons.add(data);
            appliedComponent = new URDragonDataStorageComponent(dragons);
        } else appliedComponent = URDragonDataStorageComponent.DEFAULT;
        stack.set(URItems.DRAGON_STORAGE_COMPONENT, appliedComponent);
        VortexHornCapacityComponent capacityComponent = new VortexHornCapacityComponent(getCurrentCapacityDragonStorage(stack, user.getEntityWorld()), getMaxCapacity(stack));
        stack.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, capacityComponent);
        user.setStackInHand(hand, stack);

        spawnCloud(dragon);

        dragon.discard();
        return true;
    }

    protected boolean tryCreateDragon(ItemStack stack, PlayerEntity user, World world, Hand hand, BlockPos pos) {
        URDragonDataStorageComponent dataComponent = stack.get(URItems.DRAGON_STORAGE_COMPONENT);
        if (dataComponent != null) {
            List<NbtComponent> dragons = new ArrayList<>(dataComponent.entityData());
            if (dragons.isEmpty()) return false;
            NbtComponent last = dragons.getLast();
            if (!world.isClient()) {
                Entity dragon = URDragonDataStorageComponent.createEntity(last , world);
                if (dragon == null) return false;
                dragon.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (dragon instanceof URDragonEntity urDragon) {
                    urDragon.setHomePoint(pos);
                    urDragon.setBoundedInstrumentSound(urDragon.getInstrument(stack));
                    urDragon.updateEquipment();
                    spawnCloud(urDragon);
                }
                world.spawnEntity(dragon);
                if (dragon instanceof URDragonEntity urDragon && urDragon.getOwner() != user) urDragon.setTarget(user);
            }
            dragons.removeLast();
            stack.set(URItems.DRAGON_STORAGE_COMPONENT, new URDragonDataStorageComponent(dragons));
            VortexHornCapacityComponent capacityComponent = new VortexHornCapacityComponent(getCurrentCapacityDragonStorage(stack, world), getMaxCapacity(stack));
            stack.set(URItems.VORTEX_HORN_CAPACITY_COMPONENT, capacityComponent);
            user.setStackInHand(hand, stack);
            return true;
        }
        return false;
    }

    protected void spawnCloud(Entity dragon) {
        MinecraftServer server = dragon.getEntityWorld().getServer();
        if (server != null) {
            double x = dragon.getX();
            double y = dragon.getY();
            double z = dragon.getZ();
            float offsetY = dragon.getHeight() / 2f;
            float offsetXZ = dragon.getWidth() / 2f;
            ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.CLOUD, false, false,
                    x, y, z, offsetXZ , offsetY, offsetXZ, 0, 20);
            server.getPlayerManager().sendToAround(null, x, y + offsetY, z, 128, dragon.getEntityWorld().getRegistryKey(), packet);
        }
    }

    protected int getCurrentCapacity(ItemStack stack) {
        return stack.getOrDefault(URItems.VORTEX_HORN_CAPACITY_COMPONENT, VortexHornCapacityComponent.DEFAULT).currentCapacity();
    }

    @Nullable
    protected URDragonEntity getPartParent(PlayerEntity user) {
        HitResult hitResult = ProjectileUtil.getCollision(user, entity -> entity instanceof URDragonPart, user.getEntityInteractionRange());
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof URDragonPart part) return part.owner;
        return null;
    }

    public int getMaxCapacity(ItemStack stack) {
        if (stack.getComponents().contains(URItems.VORTEX_HORN_CAPACITY_COMPONENT)) {
            VortexHornCapacityComponent dataComponent = stack.get(URItems.VORTEX_HORN_CAPACITY_COMPONENT);
            if (dataComponent != null) return dataComponent.maxCapacity();
        }
        return VortexHornCapacityComponent.DEFAULT.maxCapacity();
    }

    protected int getCurrentCapacityDragonStorage(ItemStack stack, World world) {
        return stack.getOrDefault(URItems.DRAGON_STORAGE_COMPONENT, URDragonDataStorageComponent.DEFAULT)
                .entityData()
                .stream()
                .mapToInt(value -> {
                    Entity entity = URDragonDataStorageComponent.createEntity(value, world);
                    if (entity instanceof URDragonEntity dragon) {
                        return dragon.vortexHornCapacity();
                    }
                    else return 0;
                })
                .sum();
    }
}
