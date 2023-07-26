package nordmods.uselessreptile.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(RegistryKeys.BLOCK, "dragon_unbreakable");
    public static final TagKey<Biome> SWAMP_WYVERN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"swamp_wyvern_spawn_whitelist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_whitelist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Block> ALLOWS_MOLECLAW_SPAWN = register(RegistryKeys.BLOCK, "allows_moleclaw_spawn");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(RegistryKeys.ITEM, "moleclaw_helmets");

    private static<T> TagKey<T> register(RegistryKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, new Identifier(UselessReptile.MODID ,id));
    }
}
