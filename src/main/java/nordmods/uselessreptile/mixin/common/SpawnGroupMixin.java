package nordmods.uselessreptile.mixin.common;

import net.minecraft.entity.SpawnGroup;
import nordmods.uselessreptile.common.util.URSpawnGroup;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

//credits to Hybrid Aquatic code for whatever this cursed thing is because I was too lazy to learn how to use Fabric ASM
@Mixin(SpawnGroup.class)
public class SpawnGroupMixin {
    @SuppressWarnings("unused")
    SpawnGroupMixin(String enumname, int ordinal, String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        throw new AssertionError();
    }

    // Vanilla Spawn Groups array
    @Shadow
    @Mutable
    @Final
    private static SpawnGroup[] field_6301;

    @Unique
    private static SpawnGroup createSpawnGroup(String enumname, int ordinal, URSpawnGroup spawnGroup) {
        return ((SpawnGroup)(Object) new SpawnGroupMixin(enumname, ordinal, spawnGroup.name, spawnGroup.spawnCap, spawnGroup.peaceful, spawnGroup.rare, spawnGroup.immediateDespawnRange));
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/SpawnGroup;field_6301:[Lnet/minecraft/entity/SpawnGroup;", shift = At.Shift.AFTER))
    private static void addGroups(CallbackInfo ci) {
        int vanillaSpawnGroupsLength = field_6301.length;
        URSpawnGroup[] groups = URSpawnGroup.values();
        field_6301 = Arrays.copyOf(field_6301, vanillaSpawnGroupsLength + groups.length);

        for (int i = 0; i < groups.length; i++) {
            int pos = vanillaSpawnGroupsLength + i;
            URSpawnGroup spawnGroup = groups[i];
            spawnGroup.spawnGroup = field_6301[pos] = createSpawnGroup(spawnGroup.name(), pos, spawnGroup);
        }
    }
}
