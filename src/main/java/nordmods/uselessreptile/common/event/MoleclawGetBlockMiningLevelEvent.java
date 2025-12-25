package nordmods.uselessreptile.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.block.state.BlockState;
import nordmods.uselessreptile.common.entity.Moleclaw;

//javadoc
import nordmods.uselessreptile.common.init.URModEvents;

/**
 * Gets mining level for a block to check if {@link Moleclaw} is able to break it.
 * If several events propose different levels, highest value will be returned.
 * For usage example refer to {@link URModEvents#getDefaultBlockMiningLevelForMoleclaw()}
 */
public interface MoleclawGetBlockMiningLevelEvent {
    Event<MoleclawGetBlockMiningLevelEvent> EVENT = EventFactory.createArrayBacked(
            MoleclawGetBlockMiningLevelEvent.class,
            callbacks -> ((blockState) -> {
                int miningLevel = 0;
                for (MoleclawGetBlockMiningLevelEvent event : callbacks) {
                    int proposedMiningLevel = event.getMiningLevel(blockState);
                    if (proposedMiningLevel > miningLevel) miningLevel = proposedMiningLevel;
                }
                return miningLevel;
            })
    );
    int getMiningLevel(BlockState blockState);
}
