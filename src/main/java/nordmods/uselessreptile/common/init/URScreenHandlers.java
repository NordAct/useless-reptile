package nordmods.uselessreptile.common.init;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public class URScreenHandlers{
    public final static ScreenHandlerType<URDragonScreenHandler> WYVERN_INVENTORY = registerDragonInventory(UREntities.WYVERN_ENTITY, WyvernEntity.createInventory(null));
    public final static ScreenHandlerType<URDragonScreenHandler> MOLECLAW_INVENTORY = registerDragonInventory(UREntities.MOLECLAW_ENTITY, MoleclawEntity.createInventory(null));
    public final static ScreenHandlerType<URDragonScreenHandler> LIGHTNING_CHASER_INVENTORY = registerDragonInventory(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaserEntity.createInventory(null));
    public final static ScreenHandlerType<URDragonScreenHandler> MAGMAMUNCHER_INVENTORY = registerDragonInventory(UREntities.MAGMAMUNCHER_ENTITY, MagmamuncherEntity.createInventory(null));

    public static void init() {}

    private static ScreenHandlerType<URDragonScreenHandler> registerDragonInventory(EntityType<? extends URDragonEntity> type, DragonInventory inventory) {
        return register(
                EntityType.getId(type).withSuffixedPath("_inventory").getPath(),
                new ScreenHandlerType<>(
                        (syncId, playerInventory) -> new URDragonScreenHandler(
                                        null,
                                        syncId,
                                        playerInventory,
                                        inventory
                        ),
                        FeatureFlags.VANILLA_FEATURES
                )
        );
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, UselessReptile.id(id), screenHandlerType);
    }
}
