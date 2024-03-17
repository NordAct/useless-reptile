package nordmods.uselessreptile.common.util;

import net.minecraft.entity.SpawnGroup;
import nordmods.uselessreptile.common.config.URConfig;

//credits to Hybrid Aquatic code
public enum URSpawnGroup {
    DRAGON("ur_dragon", URConfig.getConfig().dragonSpawnGroupCapacity, true, false, 128),
    UNDERGROUND_DRAGON("ur_underground_dragon", URConfig.getConfig().undergroundDragonSpawnGroupCapacity, true, false, 128),
    SMALL_DRAGON("ur_small_sragon", URConfig.getConfig().smallDragonSpawnGroupCapacity, true, false, 128);

    public SpawnGroup spawnGroup;
    public final String name;
    public final int spawnCap;
    public final boolean peaceful;
    public final boolean rare;
    public final int immediateDespawnRange;

    URSpawnGroup(String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        this.name = name;
        this.spawnCap = spawnCap;
        this.peaceful = peaceful;
        this.rare = rare;
        this.immediateDespawnRange = immediateDespawnRange;
    }
}
