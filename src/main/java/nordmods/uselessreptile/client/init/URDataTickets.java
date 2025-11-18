package nordmods.uselessreptile.client.init;

import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.phys.Vec3;

public class URDataTickets {
    //common dragon data - used for all dragons
    public static final DataTicket<String> DRAGON_VARIANT = DataTicket.create("ur_dragon_variant", String.class);
    public static final DataTicket<ResourceLocation> DRAGON_ID = DataTicket.create("ur_dragon_id", ResourceLocation.class);
    public static final DataTicket<Component> DRAGON_NAME = DataTicket.create("ur_dragon_name", Component.class);
    //specific dragon data - used for some dragons
    public static final DataTicket<Boolean> DRAGON_HAS_SADDLE = DataTicket.create("ur_dragon_has_saddle", Boolean.class);
    public static final DataTicket<UUID> DRAGON_UUID = DataTicket.create("ur_dragon_uuid", UUID.class);
    public static final DataTicket<Boolean> DRAGON_IS_RIDING_PLAYER = DataTicket.create("ur_dragon_is_riding_player", Boolean.class);
    public static final DataTicket<Boolean> DRAGON_SHOULD_RENDER_TO_CLIENT = DataTicket.create("ur_dragon_should_render_to_client", Boolean.class);
    public static final DataTicket<ShootingPoint> DRAGON_SHOOTING_POINT = DataTicket.create("ur_dragon_shooting_point", ShootingPoint.class);
    //equipment data for rendering
    public static final DataTicket<AssetCache> EQUIPMENT_ASSET_CACHE = DataTicket.create("ur_equipment_asset_cache", AssetCache.class);
    public static final DataTicket<DragonAssetCache> DRAGON_ASSET_CACHE = DataTicket.create("ur_dragon_asset_cache", DragonAssetCache.class); //also utilized for passenger rendering
    public static final DataTicket<ResourceLocation> EQUIPMENT_ITEM_ID = DataTicket.create("ur_equipment_item_id", ResourceLocation.class);
    public static final DataTicket<EntityEquipment> DRAGON_EQIPMENT = DataTicket.create("ur_dragon_equipment", EntityEquipment.class);
    public static final DataTicket<GeoRenderState> DRAGON_RENDER_STATE = DataTicket.create("ur_dragon_render_state", GeoRenderState.class);
    public static final DataTicket<HashMap> DRAGON_BONES = DataTicket.create("ur_dragon_bones", HashMap.class); //string and OwnerBoneTransforms
    //passenger(rider) data for rendering
    public static final DataTicket<Boolean> PASSENGER_SHOULD_RENDER_TO_CLIENT = DataTicket.create("ur_passenger_should_render_to_client", Boolean.class);
    public static final DataTicket<EntityRenderState> PASSENGER_RENDER_STATE = DataTicket.create("ur_passenger_render_state", EntityRenderState.class); //required for passenger render layer
    public static final DataTicket<EntityRenderer> PASSENGER_RENDER = DataTicket.create("ur_passenger_render_state", EntityRenderer.class); //ditto
    public static final DataTicket<UUID> PASSENGER_UUID = DataTicket.create("ur_passenger_uuid", UUID.class); //also ditto
    public static final DataTicket<Vec3> PASSENGER_ATTACHMENT_POS = DataTicket.create("ur_passenger_vehicle_attachment_pos", Vec3.class); //you got me
}
