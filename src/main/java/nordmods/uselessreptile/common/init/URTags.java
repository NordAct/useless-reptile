package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import nordmods.uselessreptile.UselessReptile;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(Registries.BLOCK, "dragon_unbreakable");
    public static final TagKey<Block> LIGHTNING_BREATH_ALWAYS_BREAKS = register(Registries.BLOCK, "lightning_breath_always_breaks");

    public static final TagKey<Biome> WYVERN_SPAWN_BLACKLIST = register(Registries.BIOME,"wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(Registries.BIOME,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(Registries.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_BLACKLIST = register(Registries.BIOME,"lightning_chaser_spawn_blacklist");
    public static final TagKey<Biome> MAGMAMUNCHER_SPAWN_BLACKLIST = register(Registries.BIOME,"magmamuncher_spawn_blacklist");

    public static final TagKey<Item> MOLECLAW_CHESTPLATES = register(Registries.ITEM, "moleclaw_chestplates");
    public static final TagKey<Item> MOLECLAW_TAIL_ARMOR = register(Registries.ITEM, "moleclaw_tail_armor");
    public static final TagKey<Item> MOLECLAW_HELMETS = register(Registries.ITEM, "moleclaw_helmets");
    public static final TagKey<Item> PROTECTS_MOLECLAW_FROM_LIGHT = register(Registries.ITEM, "protects_moleclaw_from_light");
    public static final TagKey<Item> MOLECLAW_SADDLES = register(Registries.ITEM, "moleclaw_saddles");

    public static final TagKey<Item> LIGHTNING_CHASER_CHESTPLATES = register(Registries.ITEM, "lightning_chaser_chestplates");
    public static final TagKey<Item> LIGHTNING_CHASER_TAIL_ARMOR = register(Registries.ITEM, "lightning_chaser_tail_armor");
    public static final TagKey<Item> LIGHTNING_CHASER_HELMETS = register(Registries.ITEM, "lightning_chaser_helmets");
    public static final TagKey<Item> LIGHTNING_CHASER_SADDLES = register(Registries.ITEM, "lightning_chaser_saddles");

    public static final TagKey<Item> WYVERN_SADDLES = register(Registries.ITEM, "wyvern_saddles");

    public static final TagKey<Item> VORTEX_HORNS = register(Registries.ITEM, "vortex_horns");

    public static final TagKey<DimensionType> DEPLETED_MAGMA_REGENERATES = register(Registries.DIMENSION_TYPE, "depleted_magma_regenerates");

    public static final TagKey<EntityType<?>> DRAGON_IMMUNE = register(Registries.ENTITY_TYPE, "dragon_immune");

    private static<T> TagKey<T> register(ResourceKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.create(registryKey, UselessReptile.id(id));
    }
}
