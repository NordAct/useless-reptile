package nordmods.uselessreptile.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import nordmods.uselessreptile.UselessReptile;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(RegistryKeys.BLOCK, "dragon_unbreakable");
    public static final TagKey<Block> LIGHTNING_BREATH_ALWAYS_BREAKS = register(RegistryKeys.BLOCK, "lightning_breath_always_breaks");

    public static final TagKey<Biome> WYVERN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"lightning_chaser_spawn_blacklist");
    public static final TagKey<Biome> MAGMAMUNCHER_SPAWN_BLACKLIST = register(RegistryKeys.BIOME,"magmamuncher_spawn_blacklist");

    public static final TagKey<Item> DRAGON_SADDLES = register(RegistryKeys.ITEM, "dragon_saddles");

    public static final TagKey<Item> MOLECLAW_CHESTPLATES = register(RegistryKeys.ITEM, "moleclaw_chestplates");
    public static final TagKey<Item> MOLECLAW_TAIL_ARMOR = register(RegistryKeys.ITEM, "moleclaw_tail_armor");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(RegistryKeys.ITEM, "moleclaw_helmets");
    public static final TagKey<Item> PROTECTS_MOLECLAW_FROM_LIGHT = register(RegistryKeys.ITEM, "protects_moleclaw_from_light");
    public static final TagKey<Item> MOLECLAW_SADDLES = register(RegistryKeys.ITEM, "moleclaw_saddles");

    public static final TagKey<Item> LIGHTNING_CHASER_CHESTPLATES = register(RegistryKeys.ITEM, "lightning_chaser_chestplates");
    public static final TagKey<Item> LIGHTNING_CHASER_TAIL_ARMOR = register(RegistryKeys.ITEM, "lightning_chaser_tail_armor");
    public static final TagKey<Item> LIGHTNING_CHASER_HELMETS = register(RegistryKeys.ITEM, "lightning_chaser_helmets");
    public static final TagKey<Item> LIGHTNING_CHASER_SADDLES = register(RegistryKeys.ITEM, "lightning_chaser_saddles");

    public static final TagKey<Item> WYVERN_SADDLES = register(RegistryKeys.ITEM, "wyvern_saddles");

    public static final TagKey<Item> WYVERN_TAMING_ITEM = register(RegistryKeys.ITEM, "wyvern_taming_item");
    public static final TagKey<Item> MOLECLAW_TAMING_ITEM = register(RegistryKeys.ITEM, "moleclaw_taming_item");
    public static final TagKey<Item> RIVER_PIKEHORN_TAMING_ITEM = register(RegistryKeys.ITEM, "river_pikehorn_taming_item");
    public static final TagKey<Item> MAGMAMUNCHER_TAMING_ITEM = register(RegistryKeys.ITEM, "magmamuncher_taming_item");

    public static final TagKey<Item> WYVERN_FOOD = register(RegistryKeys.ITEM, "wyvern_food");
    public static final TagKey<Item> MOLECLAW_FOOD = register(RegistryKeys.ITEM, "moleclaw_food");
    public static final TagKey<Item> RIVER_PIKEHORN_FOOD = register(RegistryKeys.ITEM, "river_pikehorn_food");
    public static final TagKey<Item> LIGHTNING_CHASER_FOOD = register(RegistryKeys.ITEM, "lightning_chaser_food");
    public static final TagKey<Item> MAGMAMUNCHER_FOOD = register(RegistryKeys.ITEM, "magmamuncher_food");

    public static final TagKey<DimensionType> DEPLETED_MAGMA_REGENERATES = register(RegistryKeys.DIMENSION_TYPE, "depleted_magma_regenerates");

    private static<T> TagKey<T> register(RegistryKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, UselessReptile.id(id));
    }
}
