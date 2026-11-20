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
import nordmods.uselessreptile.common.util.duck.PackModVersionPredicateStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/// Injections to GUI to display warnings if pack is not compatible with current mod version
@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class PackEntryMixin {
    @Shadow
    @Final
    private PackSelectionModel.Entry pack;

    @Unique
    private static final Component MOD_VERSION_INCOMPATIBLE = Component.translatable("gui.uselessreptile.mod_version_incompatible").withStyle(ChatFormatting.GRAY);

    @WrapOperation(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;isCompatible()Z"))
    private boolean markIncompatibleForVersionMismatch(PackCompatibility instance, Operation<Boolean> original) {
        if (validateIncompat(pack)) return false;
        return original.call(instance);
    }


    @WrapOperation(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;getDescription()Lnet/minecraft/network/chat/Component;"))
    private Component getVersionMismatchDescription(PackCompatibility instance, Operation<Component> original) {
        if (((PackModVersionPredicateStorage)((PackSelectionModel.EntryBase)pack).pack).useless_reptile$modVersionMismatch()) return MOD_VERSION_INCOMPATIBLE;
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
            VersionInterval interval = ((PackModVersionPredicateStorage)((PackSelectionModel.EntryBase)pack).pack).useless_reptile$getModVersionPredicate().getInterval();
            return Component.translatable(
                    "gui.uselessreptile.pack_mod_version_mismatch",
                    Component.empty().append(pack.getTitle()).withStyle(ChatFormatting.GOLD),
                    getRequiredVersionRangeComponent(interval).withStyle(ChatFormatting.RED),
                    Component.literal(UselessReptile.MOD_VERSION.toString()).withStyle(ChatFormatting.RED)
            );
        }
        return original.call(instance);
    }

    @Unique
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

    @Unique
    private boolean validateIncompat(PackSelectionModel.Entry pack) {
        return pack instanceof PackSelectionModel.EntryBase entryBase && ((PackModVersionPredicateStorage)entryBase.pack).useless_reptile$modVersionMismatch();
    }
}
