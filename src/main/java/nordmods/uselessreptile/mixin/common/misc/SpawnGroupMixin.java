package nordmods.uselessreptile.mixin.common.misc;

import nordmods.uselessreptile.common.util.URSpawnGroup;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import net.minecraft.world.entity.MobCategory;

//credits to Hybrid Aquatic code for whatever this cursed thing is because I was too lazy to learn how to use Fabric ASM
@Mixin(MobCategory.class)
public class SpawnGroupMixin {
    @SuppressWarnings("unused")
    SpawnGroupMixin(String enumname, int ordinal, String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        throw new AssertionError();
    }

    // Vanilla Spawn Groups array
    @Shadow
    @Mutable
    @Final
    private static MobCategory[] $VALUES;

    @Unique
    private static MobCategory createSpawnGroup(String enumname, int ordinal, URSpawnGroup spawnGroup) {
        return ((MobCategory)(Object) new SpawnGroupMixin(enumname, ordinal, spawnGroup.name, spawnGroup.spawnCap, spawnGroup.peaceful, spawnGroup.rare, spawnGroup.immediateDespawnRange));
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/MobCategory;$VALUES:[Lnet/minecraft/world/entity/MobCategory;", shift = At.Shift.AFTER))
    private static void addGroups(CallbackInfo ci) {
        int vanillaSpawnGroupsLength = $VALUES.length;
        URSpawnGroup[] groups = URSpawnGroup.values();
        $VALUES = Arrays.copyOf($VALUES, vanillaSpawnGroupsLength + groups.length);

        for (int i = 0; i < groups.length; i++) {
            int pos = vanillaSpawnGroupsLength + i;
            URSpawnGroup spawnGroup = groups[i];
            spawnGroup.spawnGroup = $VALUES[pos] = createSpawnGroup(spawnGroup.name(), pos, spawnGroup);
        }
    }
}
