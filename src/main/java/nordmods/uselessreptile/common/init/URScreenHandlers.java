package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;

public class URScreenHandlers{
    public final static MenuType<URDragonScreenHandler> WYVERN_INVENTORY = registerDragonInventory(UREntities.WYVERN_ENTITY, WyvernEntity.createInventory(null));
    public final static MenuType<URDragonScreenHandler> MOLECLAW_INVENTORY = registerDragonInventory(UREntities.MOLECLAW_ENTITY, MoleclawEntity.createInventory(null));
    public final static MenuType<URDragonScreenHandler> LIGHTNING_CHASER_INVENTORY = registerDragonInventory(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaserEntity.createInventory(null));
    public final static MenuType<URDragonScreenHandler> MAGMAMUNCHER_INVENTORY = registerDragonInventory(UREntities.MAGMAMUNCHER_ENTITY, MagmamuncherEntity.createInventory(null));

    public static void init() {}

    private static MenuType<URDragonScreenHandler> registerDragonInventory(EntityType<? extends URDragonEntity> type, DragonInventory inventory) {
        return register(
                EntityType.getKey(type).withSuffix("_inventory").getPath(),
                new MenuType<>(
                        (syncId, playerInventory) -> new URDragonScreenHandler(
                                        null,
                                        syncId,
                                        playerInventory,
                                        inventory
                        ),
                        FeatureFlags.VANILLA_SET
                )
        );
    }

    private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType<T> screenHandlerType) {
        return Registry.register(BuiltInRegistries.MENU, UselessReptile.id(id), screenHandlerType);
    }
}
