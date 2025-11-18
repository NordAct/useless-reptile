package nordmods.uselessreptile.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.init.URModEvents;
import org.jetbrains.annotations.Nullable;

/**
 * Fired whenever {@link nordmods.uselessreptile.common.entity.base.URDragonEntity} attempts to consume an item.
 * user - usually either {@link net.minecraft.world.entity.player.Player} (when interacting with mob) or {@link nordmods.uselessreptile.common.entity.base.URDragonEntity}
 * original - {@link ItemStack} that is attempted to be consumed, copy of the stack before consumption
 * remainder - original {@link ItemStack} that already got consumed
 * hand - player hand, always provided when user is Player and never when dragon
 * For usage example refer to {@link URModEvents#onItemConsumedEvents()}
 */
public interface DragonOnItemConsumedEvent {
    Event<DragonOnItemConsumedEvent> EVENT = EventFactory.createArrayBacked(
            DragonOnItemConsumedEvent.class,
            callbacks -> ((user, original, remainder, hand) -> {
                for (DragonOnItemConsumedEvent event : callbacks) event.onItemConsumed(user, original, remainder,  hand);
            })
    );
    void onItemConsumed(@Nullable LivingEntity user, ItemStack original, ItemStack remainder, @Nullable InteractionHand hand);
}
