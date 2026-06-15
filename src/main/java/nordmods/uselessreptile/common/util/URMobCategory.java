package nordmods.uselessreptile.common.util;

import net.minecraft.world.entity.MobCategory;
import nordmods.uselessreptile.common.config.URConfig;

//credits to Hybrid Aquatic code
public enum URMobCategory {
    DRAGON("ur_dragon", "UR_D", URConfig.getConfig().dragonCategoryCapacity, true, false, 128),
    UNDERGROUND_DRAGON("ur_underground_dragon", "UR_UD", URConfig.getConfig().undergroundDragonCategoryCapacity, true, false, 128),
    SMALL_DRAGON("ur_small_dragon", "UR_SD", URConfig.getConfig().smallDragonCategoryCapacity, true, false, 128);

    public MobCategory mobCategory;
    public final String name;
    public final String debugAbbreviation;
    public final int spawnCap;
    public final boolean peaceful;
    public final boolean rare;
    public final int immediateDespawnRange;

    URMobCategory(String name, String debugAbbreviation, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        this.name = name;
        this.debugAbbreviation = debugAbbreviation;
        this.spawnCap = spawnCap;
        this.peaceful = peaceful;
        this.rare = rare;
        this.immediateDespawnRange = immediateDespawnRange;
    }
}
