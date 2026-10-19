package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class URDragonBaseAttributeValues extends FabricDynamicRegistryProvider {
    public static final ResourceKey<Map<Holder<Attribute>, Float>> WYVERN = ResourceKey.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, UselessReptile.id("wyvern/default"));
    public static final ResourceKey<Map<Holder<Attribute>, Float>> RIVER_PIKEHORN = ResourceKey.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, UselessReptile.id("river_pikehorn/default"));
    public static final ResourceKey<Map<Holder<Attribute>, Float>> LIGHTNING_CHASER = ResourceKey.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, UselessReptile.id("lightning_chaser/default"));
    public static final ResourceKey<Map<Holder<Attribute>, Float>> MOLECLAW = ResourceKey.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, UselessReptile.id("moleclaw/default"));
    public static final ResourceKey<Map<Holder<Attribute>, Float>> MAGMAMUNCHER = ResourceKey.create(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES, UselessReptile.id("magmamuncher/default"));

    public URDragonBaseAttributeValues(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Base Attribute Values";
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_BASE_ATTRIBUTE_VALUES));
    }

    public static void register(BootstrapContext<Map<Holder<Attribute>, Float>> bootstrapContext) {
        bootstrapContext.register(
                WYVERN,
                Util.make(() -> {
                    Map<Holder<Attribute>, Float> map = new HashMap<>();
                    map.put(Attributes.JUMP_STRENGTH, 0.63f);
                    map.put(Attributes.ATTACK_DAMAGE, 9f);
                    map.put(Attributes.ATTACK_KNOCKBACK, 0.3f);
                    map.put(Attributes.MAX_HEALTH, 50f);
                    map.put(Attributes.ARMOR, 4f);
                    map.put(Attributes.ARMOR_TOUGHNESS, 2f);
                    map.put(Attributes.MOVEMENT_SPEED, 0.2f);
                    map.put(Attributes.FLYING_SPEED, 0.7f);
                    map.put(URAttributes.DRAGON_ACCELERATION_DURATION, 400f);
                    map.put(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 8f);
                    map.put(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 4f);
                    map.put(URAttributes.DRAGON_VERTICAL_SPEED, 0.4f);
                    return map;
                })
        );
        bootstrapContext.register(
                LIGHTNING_CHASER,
                Util.make(() -> {
                    Map<Holder<Attribute>, Float> map = new HashMap<>();
                    map.put(Attributes.ATTACK_DAMAGE, 12f);
                    map.put(Attributes.ATTACK_KNOCKBACK, 0.3f);
                    map.put(Attributes.MAX_HEALTH, 70f);
                    map.put(Attributes.ARMOR, 6f);
                    map.put(Attributes.ARMOR_TOUGHNESS, 6f);
                    map.put(Attributes.MOVEMENT_SPEED, 0.25f);
                    map.put(Attributes.FLYING_SPEED, 0.9f);
                    map.put(URAttributes.DRAGON_ACCELERATION_DURATION, 800f);
                    map.put(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 98f);
                    map.put(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 7f);
                    map.put(URAttributes.DRAGON_VERTICAL_SPEED, 0.3f);
                    return map;
                })
        );
        bootstrapContext.register(
                RIVER_PIKEHORN,
                Util.make(() -> {
                    Map<Holder<Attribute>, Float> map = new HashMap<>();
                    map.put(Attributes.ATTACK_DAMAGE, 3f);
                    map.put(Attributes.ATTACK_KNOCKBACK, 0f);
                    map.put(Attributes.MAX_HEALTH, 20f);
                    map.put(Attributes.ARMOR, 0f);
                    map.put(Attributes.ARMOR_TOUGHNESS, 0f);
                    map.put(Attributes.MOVEMENT_SPEED, 0.2f);
                    map.put(Attributes.FLYING_SPEED, 0.8f);
                    map.put(URAttributes.DRAGON_ACCELERATION_DURATION, 200f);
                    map.put(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 10f);
                    map.put(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 16f);
                    map.put(URAttributes.DRAGON_VERTICAL_SPEED, 0.2f);
                    return map;
                })
        );
        bootstrapContext.register(
                MOLECLAW,
                Util.make(() -> {
                    Map<Holder<Attribute>, Float> map = new HashMap<>();
                    map.put(Attributes.ATTACK_DAMAGE, 8f);
                    map.put(Attributes.ATTACK_KNOCKBACK, 0.5f);
                    map.put(Attributes.MAX_HEALTH, 80f);
                    map.put(Attributes.ARMOR, 8f);
                    map.put(Attributes.ARMOR_TOUGHNESS, 4f);
                    map.put(Attributes.MOVEMENT_SPEED, 0.25f);
                    map.put(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 6f);
                    map.put(URAttributes.DRAGON_MINING_LEVEL, 0f);
                    return map;
                })
        );
        bootstrapContext.register(
                MAGMAMUNCHER,
                Util.make(() -> {
                    Map<Holder<Attribute>, Float> map = new HashMap<>();
                    map.put(Attributes.ATTACK_DAMAGE, 4f);
                    map.put(Attributes.ATTACK_KNOCKBACK, 0.2f);
                    map.put(Attributes.MAX_HEALTH, 16f);
                    map.put(Attributes.ARMOR, 2f);
                    map.put(Attributes.ARMOR_TOUGHNESS, 0f);
                    map.put(Attributes.MOVEMENT_SPEED, 0.2f);
                    map.put(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 20f);
                    return map;
                })
        );
    }
}
