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
    public static final TagKey<Block> MOLECLAW_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "moleclaw_spawnable_on");
    public static final TagKey<Block> WYVERN_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "wyvern_spawnable_on");
    public static final TagKey<Block> PIKEHORN_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "pikehorn_spawnable_on");
    public static final TagKey<Block> LIGHTNING_CHASER_SPAWNABLE_ON = register(RegistryKeys.BLOCK, "lightning_chaser_spawnable_on");
    public static final TagKey<Biome> SWAMP_WYVERN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"swamp_wyvern_spawn_whitelist");
    public static final TagKey<Biome> SWAMP_WYVERN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"swamp_wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_whitelist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"moleclaw_spawn_whitelist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_WHITELIST = register(RegistryKeys.BIOME,"lightning_chaser_spawn_whitelist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"lightning_chaser_spawn_blacklist");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(RegistryKeys.ITEM, "moleclaw_helmets");
    public static final TagKey<Item> MOLECLAW_CAN_EQUIP = register(RegistryKeys.ITEM, "moleclaw_can_equip");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR = register(RegistryKeys.ITEM, "common_dragon_armor");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_IRON = register(RegistryKeys.ITEM, "common_dragon_armor_iron");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_GOLD = register(RegistryKeys.ITEM, "common_dragon_armor_gold");
    public static final TagKey<Item> COMMON_DRAGON_ARMOR_DIAMOND = register(RegistryKeys.ITEM, "common_dragon_armor_diamond");

    private static<T> TagKey<T> register(RegistryKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, new Identifier(UselessReptile.MODID ,id));
    }
}
