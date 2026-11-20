package nordmods.uselessreptile.client.init;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.uselessreptile.UselessReptile;

import java.util.List;
import java.util.UUID;

public class URClientStateDataTypes {
    //equipment data for rendering
    public static final StateDataType<ItemStackRenderState> FISH = new StateDataType<>(UselessReptile.id("fish"));
    public static final StateDataType<ItemStackRenderState> BANNER = new StateDataType<>(UselessReptile.id("banner"));
    //passengers data for rendering
    public static final StateDataType<List<Boolean>> PASSENGERS_SHOULD_RENDER_TO_CLIENT = new StateDataType<>(UselessReptile.id("passengers_should_render_to_client"));
    public static final StateDataType<List<? super EntityRenderState>> PASSENGERS_RENDER_STATE = new StateDataType<>(UselessReptile.id("passengers_render_state")); //required for passenger render layer
    public static final StateDataType<List<EntityRenderer<? super Entity, ? super EntityRenderState>>> PASSENGERS_RENDERERS = new StateDataType<>(UselessReptile.id("passengers_renderers")); //ditto
    public static final StateDataType<List<UUID>> PASSENGERS_UUID = new StateDataType<>(UselessReptile.id("passengers_uuid")); //also ditto
    public static final StateDataType<List<Vec3>> PASSENGERS_ATTACHMENT_POS = new StateDataType<>(UselessReptile.id("passengers_vehicle_attachment_pos")); //you got me
}
