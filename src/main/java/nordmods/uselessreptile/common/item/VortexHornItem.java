package nordmods.uselessreptile.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.init.URItemComponents;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.item.component.URDragonDataStorageComponent;
import nordmods.uselessreptile.common.item.component.VortexHornCapacityComponent;
import com.mojang.blaze3d.platform.InputConstants;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class VortexHornItem extends InstrumentItem {
    public VortexHornItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (URDragonPart.getPartParent(user) instanceof URDragonEntity dragon) entity = dragon;
        if (entity instanceof URDragonEntity dragon && dragon.getOwner() == user && !user.isShiftKeyDown()) {
            if (tryCollectDragon(stack, user, dragon, hand, true)) {
                user.releaseUsingItem();
                user.makeSound(URSoundEvent.VORTEX_HORN_SUCK_IN);
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (user.isShiftKeyDown()) {
            if (tryMassCatchOrRelease(stack, user, world, hand)) return InteractionResult.SUCCESS;
        }
        if (URDragonPart.getPartParent(user) instanceof URDragonEntity dragon) {
            interactLivingEntity(stack, user, dragon, hand);
            user.releaseUsingItem();
            return InteractionResult.SUCCESS;
        }
        InteractionResult result = super.use(world, user, hand);
        user.getCooldowns().addCooldown(stack, 0);
        return result;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (context.getPlayer() instanceof Player user) {
            Level world = context.getLevel();
            InteractionHand hand = context.getHand();
            if (user.isShiftKeyDown()) {
                if (tryMassCatchOrRelease(stack, user, world, hand)) {
                    user.releaseUsingItem();
                    return InteractionResult.SUCCESS;
                }
            }
            BlockPos pos = context.getClickedPos();
            BlockState blockState = world.getBlockState(pos);
            Direction direction = context.getClickedFace();
            if (!blockState.getCollisionShape(world, pos).isEmpty()) pos = pos.relative(direction);

            if (tryCreateDragon(stack, user, world, hand, pos)) {
                user.releaseUsingItem();
                user.makeSound(URSoundEvent.VORTEX_HORN_SPIT_OUT);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type
    )  {
        if (stack.getComponents().has(URItemComponents.DRAGON_STORAGE)) {
            URDragonDataStorageComponent dataComponent = stack.get(URItemComponents.DRAGON_STORAGE);
            if (dataComponent != null) {
                boolean full = getCurrentCapacity(stack) >= getMaxCapacity(stack);
                textConsumer.accept(Component.translatable("tooltip.uselessreptile.vortex_horn.capacity",getCurrentCapacity(stack) , getMaxCapacity(stack)).withStyle(full ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
                if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)) textConsumer.accept(Component.translatable("tooltip.uselessreptile.hidden").withStyle(ChatFormatting.DARK_GRAY));
                else {
                    textConsumer.accept(Component.translatable("tooltip.uselessreptile.vortex_horn.contained_dragons"));
                    for (CustomData nbtComponent : dataComponent.entityData()) {
                        CompoundTag nbt = nbtComponent.copyTag();
                        if (nbt.contains("CustomName")) {
                            Component customName = nbt.read("CustomName", ComponentSerialization.CODEC).orElse(Component.empty());
                            textConsumer.accept(customName);
                        } else {
                            String string = nbt.getString("id").orElse("");
                            Optional<EntityType<?>> entityType = EntityType.byString(string);
                            textConsumer.accept(entityType.map(value -> Component.translatable(value.getDescriptionId())).orElseGet(() -> Component.literal("ERROR").withStyle(ChatFormatting.RED)));
                        }
                    }
                }
            }
            textConsumer.accept(Component.empty());
        }
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
    }

    protected boolean tryMassCatchOrRelease(ItemStack stack, Player user, Level world, InteractionHand hand) {
        AABB box = new AABB(user.blockPosition()).inflate(2);
        List<URDragonEntity> dragons = world.getEntitiesOfClass(URDragonEntity.class, box, entity -> entity.getOwner() == user && !entity.isOrderedToSit());
        int leastCapacity = 0;
        if (!dragons.isEmpty()) {
            leastCapacity = dragons.getFirst().vortexHornCapacity();
            for (URDragonEntity dragon : dragons) leastCapacity = Math.min(leastCapacity, dragon.vortexHornCapacity());
        }
        if (leastCapacity <= 0 || getCurrentCapacity(stack) + leastCapacity > getMaxCapacity(stack)) {
            URDragonDataStorageComponent dataComponent = stack.get(URItemComponents.DRAGON_STORAGE);
            if (dataComponent != null && getCurrentCapacity(stack) > 0) {
                for (int i = 0; i < dataComponent.entityData().size(); i++) tryCreateDragon(stack, user, world, hand, user.blockPosition());
                user.makeSound(URSoundEvent.VORTEX_HORN_SPIT_OUT);
                return true;
            }
        } else {
            dragons.sort(Comparator.comparingDouble((dragon) -> dragon.distanceToSqr(dragon.getOwner())));
            for (URDragonEntity dragon : dragons) {
                if (dragon.isOrderedToSit()) continue;
                if (!tryCollectDragon(stack, user, dragon, hand, false)) break;
            }
            user.makeSound(URSoundEvent.VORTEX_HORN_SUCK_IN);
            return true;
        }
        return false;
    }

    protected boolean tryCollectDragon(ItemStack stack, Player user, URDragonEntity dragon, InteractionHand hand, boolean capacityWarning) {
        int dragonCapacity = dragon.vortexHornCapacity();
        if (getCurrentCapacity(stack) + dragonCapacity > getMaxCapacity(stack)) {
            if (capacityWarning && !user.level().isClientSide()) user.displayClientMessage(Component.translatable("other.uselessreptile.not_enough_capacity"), true);
            return false;
        }

        if (user.level().isClientSide()) return true;

        dragon.stopRiding();
        dragon.ejectPassengers();

        URDragonDataStorageComponent dataComponent = stack.get(URItemComponents.DRAGON_STORAGE);
        URDragonDataStorageComponent appliedComponent;
        if (dataComponent != null) {
            List<CustomData> dragons = new ArrayList<>(dataComponent.entityData());
            CustomData data = URDragonDataStorageComponent.createData(dragon);
            dragons.add(data);
            appliedComponent = new URDragonDataStorageComponent(dragons);
        } else appliedComponent = URDragonDataStorageComponent.DEFAULT;
        stack.set(URItemComponents.DRAGON_STORAGE, appliedComponent);
        VortexHornCapacityComponent capacityComponent = new VortexHornCapacityComponent(getCurrentCapacityDragonStorage(stack, user.level()), getMaxCapacity(stack));
        stack.set(URItemComponents.VORTEX_HORN_CAPACITY, capacityComponent);
        user.setItemInHand(hand, stack);

        spawnCloud(dragon);

        dragon.discard();
        return true;
    }

    protected boolean tryCreateDragon(ItemStack stack, Player user, Level world, InteractionHand hand, BlockPos pos) {
        URDragonDataStorageComponent dataComponent = stack.get(URItemComponents.DRAGON_STORAGE);
        if (dataComponent != null) {
            List<CustomData> dragons = new ArrayList<>(dataComponent.entityData());
            if (dragons.isEmpty()) return false;
            CustomData last = dragons.getLast();
            if (!world.isClientSide()) {
                Entity dragon = URDragonDataStorageComponent.createEntity(last , world);
                if (dragon == null) return false;
                dragon.setPosRaw(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (dragon instanceof URDragonEntity urDragon) {
                    urDragon.setHomePoint(pos);
                    urDragon.setBoundedInstrumentSound(urDragon.getInstrument(stack));
                    urDragon.updateEquipment();
                    spawnCloud(urDragon);
                }
                world.addFreshEntity(dragon);
                if (dragon instanceof URDragonEntity urDragon && urDragon.getOwner() != user) urDragon.setTarget(user);
            }
            dragons.removeLast();
            stack.set(URItemComponents.DRAGON_STORAGE, new URDragonDataStorageComponent(dragons));
            VortexHornCapacityComponent capacityComponent = new VortexHornCapacityComponent(getCurrentCapacityDragonStorage(stack, world), getMaxCapacity(stack));
            stack.set(URItemComponents.VORTEX_HORN_CAPACITY, capacityComponent);
            user.setItemInHand(hand, stack);
            return true;
        }
        return false;
    }

    protected void spawnCloud(Entity dragon) {
        MinecraftServer server = dragon.level().getServer();
        if (server != null) {
            double x = dragon.getX();
            double y = dragon.getY();
            double z = dragon.getZ();
            float offsetY = dragon.getBbHeight() / 2f;
            float offsetXZ = dragon.getBbWidth() / 2f;
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(ParticleTypes.CLOUD, false, false,
                    x, y, z, offsetXZ , offsetY, offsetXZ, 0, 20);
            server.getPlayerList().broadcast(null, x, y + offsetY, z, 128, dragon.level().dimension(), packet);
        }
    }

    protected int getCurrentCapacity(ItemStack stack) {
        return stack.getOrDefault(URItemComponents.VORTEX_HORN_CAPACITY, VortexHornCapacityComponent.DEFAULT).currentCapacity();
    }

    public int getMaxCapacity(ItemStack stack) {
        if (stack.getComponents().has(URItemComponents.VORTEX_HORN_CAPACITY)) {
            VortexHornCapacityComponent dataComponent = stack.get(URItemComponents.VORTEX_HORN_CAPACITY);
            if (dataComponent != null) return dataComponent.maxCapacity();
        }
        return VortexHornCapacityComponent.DEFAULT.maxCapacity();
    }

    protected int getCurrentCapacityDragonStorage(ItemStack stack, Level world) {
        return stack.getOrDefault(URItemComponents.DRAGON_STORAGE, URDragonDataStorageComponent.DEFAULT)
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
