package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.*;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.URDragonMenu;

public class URMenus {
    public final static MenuType<URDragonMenu> WYVERN_INVENTORY = registerDragonInventory(UREntities.WYVERN_ENTITY, Wyvern.createInventory(null));
    public final static MenuType<URDragonMenu> MOLECLAW_INVENTORY = registerDragonInventory(UREntities.MOLECLAW_ENTITY, Moleclaw.createInventory(null));
    public final static MenuType<URDragonMenu> LIGHTNING_CHASER_INVENTORY = registerDragonInventory(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaser.createInventory(null));
    public final static MenuType<URDragonMenu> RIVER_PIKEHORN_INVENTORY = registerDragonInventory(UREntities.RIVER_PIKEHORN_ENTITY, RiverPikehorn.createInventory(null));
    public final static MenuType<URDragonMenu> MAGMAMUNCHER_INVENTORY = registerDragonInventory(UREntities.MAGMAMUNCHER_ENTITY, Magmamuncher.createInventory(null));

    public static void init() {}

    private static MenuType<URDragonMenu> registerDragonInventory(EntityType<? extends URDragonEntity> type, DragonInventory inventory) {
        return register(
                EntityType.getKey(type).withSuffix("_inventory").getPath(),
                new MenuType<>(
                        (syncId, playerInventory) -> new URDragonMenu(
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
