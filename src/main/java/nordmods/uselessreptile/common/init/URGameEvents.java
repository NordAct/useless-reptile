package nordmods.uselessreptile.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonCallBackGoal;

public class URGameEvents {
    public final static Holder.Reference<GameEvent> LIGHTNING_STRIKE_FAR = register("lightning_strike_far", 256);
    public final static Holder.Reference<GameEvent> FLUTE_USED = register("flute_used", 256);
    public final static Holder.Reference<GameEvent> INSTRUMENT_USED = register("instrument_used", DragonCallBackGoal.MAX_CALL_DISTANCE);

    private static Holder.Reference<GameEvent> register(String id, int range) {
        return Registry.registerForHolder(BuiltInRegistries.GAME_EVENT, UselessReptile.id(id), new GameEvent(range));
    }

    public static void init() {}
}
