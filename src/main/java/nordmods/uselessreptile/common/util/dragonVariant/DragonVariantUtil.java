package nordmods.uselessreptile.common.util.dragonVariant;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragonVariantUtil {
    private static final Map<String, List<DragonVariant>> dragonVariants = new HashMap<>();

    public static List<DragonVariant> getVariants(String name) {
        return dragonVariants.get(name);
    }

    public static void reset() {
        dragonVariants.clear();
    }

    public static void add(String name, List<DragonVariant> variants) {
        List<DragonVariant> content = dragonVariants.get(name);
        if (content != null) {
            content.addAll(variants);
            dragonVariants.put(name, content);
        } else dragonVariants.put(name, variants);
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<DragonVariant>> entry : dragonVariants.entrySet()) {
            for (DragonVariant variant : entry.getValue()) {
                UselessReptile.LOGGER.debug("{}: variant {} was loaded", entry.getKey(), variant);
            }
        }
    }

    public static boolean isVariantIn(DragonVariant.BiomeRestrictions restrictions, ServerWorldAccess world, BlockPos blockPos) {
        RegistryEntry<Biome> biome = world.getBiome(blockPos);
        List<String> id = restrictions.hasBiomesByIdList() ? restrictions.biomesById() : List.of();
        List<String> tags = restrictions.hasBiomesByTagList() ?restrictions.biomesByTag() : List.of();

        boolean isIn = false;
        for (String s : id) {
            Identifier name = new Identifier(s);
            if (biome.matchesId(name)) {
                isIn = true;
                break;
            }
        }

        if (!isIn) for (String tag : tags) {
            Identifier name = new Identifier(tag);
            if (biome.isIn(TagKey.of(Registry.BIOME_KEY, name))) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }

    public static void assignVariant(ServerWorldAccess world, URDragonEntity entity) {
        List<DragonVariant> variants = DragonVariantUtil.getVariants(entity);
        if (variants == null) throw new RuntimeException("Failed to get variants for " + entity);

        List<DragonVariant> allowedVariants = new ArrayList<>(variants.size());
        for (DragonVariant variant : variants) {
            //banned biomes check (blacklist)
            if (variant.hasBannedBiomes() && DragonVariantUtil.isVariantIn(variant.bannedBiomes(), world, entity.getBlockPos())) continue;
            if (variant.altitudeRestriction().min() > entity.getBlockPos().getY() || entity.getBlockPos().getY() > variant.altitudeRestriction().max()) continue;
            //allowed biomes check (whitelist)
            if (!variant.hasAllowedBiomes()) allowedVariants.add(variant);
            else if (DragonVariantUtil.isVariantIn(variant.allowedBiomes(), world, entity.getBlockPos())) allowedVariants.add(variant);
        }

        assignVariantFromList(entity, allowedVariants);
    }

    public static List<DragonVariant> getVariants(URDragonEntity entity) {
        Identifier id = EntityType.getId(entity.getType());
        return DragonVariantUtil.getVariants(id.getPath());
    }

    public static void assignVariantFromList(URDragonEntity entity, List<DragonVariant> variants) {
        int totalWeight = 0;
        for (DragonVariant variant : variants) totalWeight += variant.weight();
        if (totalWeight <= 0)
            throw new RuntimeException("Failed to assign dragon variant due impossible total weight of all variants for " + entity);

        int roll = entity.getRandom().nextInt(totalWeight);
        int previousBound = 0;

        for (DragonVariant variant : variants) {
            if (roll >= previousBound && roll < previousBound + variant.weight()) {
                entity.setVariant(variant.name());
                break;
            }
            previousBound += variant.weight();
        }
    }
}
