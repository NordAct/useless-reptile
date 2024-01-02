package nordmods.uselessreptile.mixin.client;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.text.TranslatableTextContent;
import nordmods.uselessreptile.client.init.URClientConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//I really shouldn't have done this... But idk for a better way to split configs and keep Mod Menu compat
@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin {
    @Shadow private boolean pendingChanges;
    @Shadow @Final public TabManager tabManager;

    @Inject(method = "finishOrSave", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", shift = At.Shift.AFTER), remap = false)
    private void saveClientConfig(CallbackInfo ci) {
        //a weird way to filter out specifically UR configs
        if (tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab
                && categoryTab.getTitle().getContent() instanceof TranslatableTextContent content
                && content.getKey().contains("config.uselessreptile.category")) {
            URClientConfig.save();
            pendingChanges = false;
            categoryTab.updateButtons();
        }
    }
}
