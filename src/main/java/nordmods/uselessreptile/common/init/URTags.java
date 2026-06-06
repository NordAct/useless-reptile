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
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;

public class URTags {
    public static final TagKey<Block> DRAGON_UNBREAKABLE = register(Registries.BLOCK, "dragon_unbreakable");
    public static final TagKey<Block> LIGHTNING_BREATH_ALWAYS_BREAKS = register(Registries.BLOCK, "lightning_breath_always_breaks");

    public static final TagKey<Biome> WYVERN_SPAWN_BLACKLIST = register(Registries.BIOME,"wyvern_spawn_blacklist");
    public static final TagKey<Biome> RIVER_PIKEHORN_SPAWN_BLACKLIST = register(Registries.BIOME,"river_pikehorn_spawn_blacklist");
    public static final TagKey<Biome> MOLECLAW_SPAWN_BLACKLIST = register(Registries.BIOME,"moleclaw_spawn_blacklist");
    public static final TagKey<Biome> LIGHTNING_CHASER_SPAWN_BLACKLIST = register(Registries.BIOME,"lightning_chaser_spawn_blacklist");
    public static final TagKey<Biome> MAGMAMUNCHER_SPAWN_BLACKLIST = register(Registries.BIOME,"magmamuncher_spawn_blacklist");
    public static final TagKey<Biome> HAS_LIGHTNING_CHASER_NEST_DESERT = register(Registries.BIOME,"has_structure/lightning_chaser_nest_desert");

    public static final TagKey<Item> PROTECTS_MOLECLAW_FROM_LIGHT = register(Registries.ITEM, "protects_moleclaw_from_light");
    public static final TagKey<Item> VORTEX_HORNS = register(Registries.ITEM, "vortex_horns");

    public static final TagKey<DimensionType> DEPLETED_MAGMA_REGENERATES = register(Registries.DIMENSION_TYPE, "depleted_magma_regenerates");

    public static final TagKey<EntityType<?>> DRAGON_IMMUNE = register(Registries.ENTITY_TYPE, "dragon_immune");
    public static final TagKey<EntityType<?>> DRAGON = register(Registries.ENTITY_TYPE, "dragon");

    public static final TagKey<DragonAbilityType<?>> ATTACK = register(URResourceKeys.DRAGON_ABILITY_TYPE, "attack");
    public static final TagKey<DragonAbilityType<?>> MELEE_ATTACK = register(URResourceKeys.DRAGON_ABILITY_TYPE, "melee_attack");
    public static final TagKey<DragonAbilityType<?>> RANGED_ATTACK = register(URResourceKeys.DRAGON_ABILITY_TYPE, "ranged_attack");
    public static final TagKey<DragonAbilityType<?>> SUPPORT_ABILITY = register(URResourceKeys.DRAGON_ABILITY_TYPE, "support_ability");
    public static final TagKey<DragonAbilityType<?>> DEFENSIVE_ABILITY = register(URResourceKeys.DRAGON_ABILITY_TYPE, "support_ability");

    private static<T> TagKey<T> register(ResourceKey<? extends Registry<T>> registryKey, String id) {
        return TagKey.create(registryKey, UselessReptile.id(id));
    }
}
