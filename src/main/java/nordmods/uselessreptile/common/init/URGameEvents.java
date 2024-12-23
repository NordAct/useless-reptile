package nordmods.uselessreptile.common.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonCallBackGoal;

public class URGameEvents {
    public final static RegistryEntry.Reference<GameEvent> LIGHTNING_STRIKE_FAR = register("lightning_strike_far", 256);
    public final static RegistryEntry.Reference<GameEvent> FLUTE_USED = register("flute_used", 256);
    public final static RegistryEntry.Reference<GameEvent> INSTRUMENT_USED = register("instrument_used", DragonCallBackGoal.MAX_CALL_DISTANCE);

    private static RegistryEntry.Reference<GameEvent> register(String id, int range) {
        return Registry.registerReference(Registries.GAME_EVENT, UselessReptile.id(id), new GameEvent(range));
    }

    public static void init() {}
}
