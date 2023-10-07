package nordmods.uselessreptile.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(Registry.BLOCK_KEY, "dragon_unbreakable");
    public static final TagKey<Biome> SWAMP_WYVERN_SPAWN_WHITELIST = register(Registry.BIOME_KEY,"swamp_wyvern_spawn_whitelist");
    public static final TagKey<Biome> SWAMP_WYVERN_SPAWN_BLACKLIST = register(Registry.BIOME_KEY,"swamp_wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_WHITELIST = register(Registry.BIOME_KEY,"river_pikehorn_spawn_whitelist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(Registry.BIOME_KEY,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_WHITELIST = register(Registry.BIOME_KEY,"moleclaw_spawn_whitelist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(Registry.BIOME_KEY,"moleclaw_spawn_blacklist");
    public static final TagKey<Block> ALLOWS_MOLECLAW_SPAWN = register(Registry.BLOCK_KEY, "allows_moleclaw_spawn");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(Registry.ITEM_KEY, "moleclaw_helmets");
    public static final TagKey<Item> MOLECLAW_CAN_EQUIP = register(Registry.ITEM_KEY, "moleclaw_can_equip");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR = register(Registry.ITEM_KEY, "common_dragon_armor");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_IRON = register(Registry.ITEM_KEY, "common_dragon_armor_iron");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_GOLD = register(Registry.ITEM_KEY, "common_dragon_armor_gold");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_DIAMOND = register(Registry.ITEM_KEY, "common_dragon_armor_diamond");

    private static<T> TagKey<T> register(RegistryKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, new Identifier(UselessReptile.MODID ,id));
    }
}
