package nordmods.uselessreptile.mixin.common.misc;

import net.minecraft.world.entity.MobCategory;
import nordmods.uselessreptile.common.util.URMobCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/// Adds custom mob categories
//credits to Hybrid Aquatic code for whatever this cursed thing is because I was too lazy to learn how to use Fabric ASM
@Mixin(MobCategory.class)
public class MobCategoryMixin {
    @SuppressWarnings("unused")
    MobCategoryMixin(String enumname, int ordinal, String name, int spawnCap, boolean peaceful, boolean rare, int immediateDespawnRange) {
        throw new AssertionError();
    }

    // Vanilla Spawn Groups array
    @Shadow
    @Mutable
    @Final
    private static MobCategory[] $VALUES;

    @Unique
    private static MobCategory createSpawnGroup(String enumname, int ordinal, URMobCategory spawnGroup) {
        return ((MobCategory)(Object) new MobCategoryMixin(enumname, ordinal, spawnGroup.name, spawnGroup.spawnCap, spawnGroup.peaceful, spawnGroup.rare, spawnGroup.immediateDespawnRange));
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/MobCategory;$VALUES:[Lnet/minecraft/world/entity/MobCategory;", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
    private static void addGroups(CallbackInfo ci) {
        int vanillaSpawnGroupsLength = $VALUES.length;
        URMobCategory[] groups = URMobCategory.values();
        $VALUES = Arrays.copyOf($VALUES, vanillaSpawnGroupsLength + groups.length);

        for (int i = 0; i < groups.length; i++) {
            int pos = vanillaSpawnGroupsLength + i;
            URMobCategory spawnGroup = groups[i];
            spawnGroup.mobCategory = $VALUES[pos] = createSpawnGroup(spawnGroup.name(), pos, spawnGroup);
        }
    }
}
