package nordmods.uselessreptile.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(RegistryKeys.BLOCK, "dragon_unbreakable");
    public static final TagKey<Block> MOLECLAW_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "moleclaw_spawnable_on");
    public static final TagKey<Block> WYVERN_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "wyvern_spawnable_on");
    public static final TagKey<Block> RIVER_PIKEHORN_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "river_pikehorn_spawnable_on");
    public static final TagKey<Block> LIGHTNING_CHASER_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "lightning_chaser_spawnable_on");

    public static final TagKey<Biome> WYVERN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"wyvern_spawn_whitelist");
    public static final TagKey<Biome> WYVERN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_whitelist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"moleclaw_spawn_whitelist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"lightning_chaser_spawn_whitelist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"lightning_chaser_spawn_blacklist");

    public static final TagKey<Item> MOLECLAW_CHESTPLATES = register(RegistryKeys.ITEM, "moleclaw_chestplates");
    public static final TagKey<Item> MOLECLAW_TAIL_ARMOR = register(RegistryKeys.ITEM, "moleclaw_tail_armor");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(RegistryKeys.ITEM, "moleclaw_helmets");
    public static final TagKey<Item> MOLECLAW_PROTECTS_FROM_LIGHT = register(RegistryKeys.ITEM, "moleclaw_protects_from_light");

    public static final TagKey<Item> LIGHTNING_CHASER_CHESTPLATES = register(RegistryKeys.ITEM, "lightning_chaser_chestplates");
    public static final TagKey<Item> LIGHTNING_CHASER_TAIL_ARMOR = register(RegistryKeys.ITEM, "lightning_chaser_tail_armor");
    public static final TagKey<Item> LIGHTNING_CHASER_HELMETS = register(RegistryKeys.ITEM, "lightning_chaser_helmets");

    private static<T> TagKey<T> register(RegistryKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, UselessReptile.id(id));
    }
}
