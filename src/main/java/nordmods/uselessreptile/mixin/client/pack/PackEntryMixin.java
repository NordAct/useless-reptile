package nordmods.uselessreptile.mixin.client.pack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.repository.PackCompatibility;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.duck.PackSelectionScreenAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class PackEntryMixin {
    @Shadow
    @Final
    private TransferableSelectionList parent;

    @Shadow
    @Final
    private PackSelectionModel.Entry pack;

    @WrapOperation(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;isCompatible()Z"))
    private boolean markIncompatibleForVersionMismatch(PackCompatibility instance, Operation<Boolean> original) {
        if (validateIncompat(pack)) return false;
        return original.call(instance);
    }

    @WrapOperation(method = "handlePackSelection", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;isCompatible()Z"))
    private boolean verifyVersionMismatch(PackCompatibility instance, Operation<Boolean> original) {
        if (validateIncompat(pack)) return false;
        return original.call(instance);
    }

    @WrapOperation(method = "handlePackSelection", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;getConfirmation()Lnet/minecraft/network/chat/Component;"))
    private Component replaceMessage(PackCompatibility instance, Operation<Component> original) {
        if (validateIncompat(pack)) {
            VersionInterval interval = ((PackSelectionScreenAccessor) parent.screen).getPacksWithVersion().get(((PackSelectionModel.EntryBase)pack).pack);
            return Component.translatable(
                    "gui.uselessreptile.pack_mod_version_mismatch",
                    Component.empty().append(pack.getTitle()).withStyle(ChatFormatting.GOLD),
                    getRequiredVersionRangeComponent(interval).withStyle(ChatFormatting.RED),
                    Component.literal(UselessReptile.MOD_VERSION.toString()).withStyle(ChatFormatting.RED)
            );
        }
        return original.call(instance);
    }

    private MutableComponent getRequiredVersionRangeComponent(VersionInterval interval) {
        boolean minPresent = interval.getMin() != null;
        boolean maxPresent = interval.getMax() != null;
        if (minPresent && maxPresent) {
            return Component.translatable("gui.uselessreptile.version_range",
                    interval.isMinInclusive() ? Component.translatable("gui.uselessreptile.version_inclusive", interval.getMin()) : Component.literal(interval.getMin() + ""),
                    interval.isMaxInclusive() ? Component.translatable("gui.uselessreptile.version_inclusive", interval.getMax()) : Component.literal(interval.getMax() + "")
            );
        }
        else if (!minPresent) {
            return Component.translatable("gui.uselessreptile.version_range_below",
                    interval.isMaxInclusive() ? Component.translatable("gui.uselessreptile.version_inclusive", interval.getMax()) : Component.literal(interval.getMax() + "")
            );
        }
        return Component.translatable("gui.uselessreptile.version_range_above",
                interval.isMinInclusive() ? Component.translatable("gui.uselessreptile.version_inclusive", interval.getMin()) : Component.literal(interval.getMin() + "")
        );
    }

    private boolean validateIncompat(PackSelectionModel.Entry pack) {
        return pack instanceof PackSelectionModel.EntryBase entryBase && ((PackSelectionScreenAccessor)parent.screen).getInvalidPacks().contains(entryBase.pack);
    }
}
