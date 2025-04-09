package nordmods.uselessreptile.client.init;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.UUID;

public class URDataTickets {
    public static final DataTicket<String> DRAGON_VARIANT = DataTicket.create("ur_dragon_variant", String.class);
    public static final DataTicket<Identifier> DRAGON_ID = DataTicket.create("ur_dragon_id", Identifier.class);
    public static final DataTicket<Text> DRAGON_NAME = DataTicket.create("ur_dragon_name", Text.class);
    public static final DataTicket<AssetCache> ASSET_CACHE = DataTicket.create("ur_asset_cache", AssetCache.class);
    public static final DataTicket<DragonAssetCache> DRAGON_ASSET_CACHE = DataTicket.create("ur_dragon_asset_cache", DragonAssetCache.class);
    public static final DataTicket<Identifier> ITEM_ID = DataTicket.create("ur_item_id", Identifier.class);
    public static final DataTicket<EntityEquipment> DRAGON_EQIPMENT = DataTicket.create("ur_dragon_equipment", EntityEquipment.class);
    public static final DataTicket<GeoRenderState> DRAGON_RENDER_STATE = DataTicket.create("ur_dragon_render_state", GeoRenderState.class); //don't question this, I was lazy
    public static final DataTicket<Boolean> HAS_SADDLE = DataTicket.create("ur_has_saddle", Boolean.class);
    public static final DataTicket<UUID> UUID = DataTicket.create("ur_uuid", UUID.class);
    public static final DataTicket<Boolean> IS_RIDING_PLAYER = DataTicket.create("ur_is_riding_player", Boolean.class);
    public static final DataTicket<Boolean> HAS_RIDER = DataTicket.create("ur_has_rider", Boolean.class);
    public static final DataTicket<Boolean> SHOULD_RENDER_TO_CLIENT = DataTicket.create("ur_should_render_to_client", Boolean.class);
    public static final DataTicket<ItemStack> MAINHAND_ITEM = DataTicket.create("ur_mainhand_item", ItemStack.class);
    public static final DataTicket<ItemStack> OFFHAND_ITEM = DataTicket.create("ur_offhand_item", ItemStack.class);
}
