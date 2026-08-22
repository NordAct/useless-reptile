package nordmods.uselessreptile.common.init;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.asset_cache.AssetCache;
import nordmods.uselessreptile.common.entity.animation_processor.BoneTransform;
import nordmods.uselessreptile.common.entity.animation_processor.SyncronizedAnimationProcessor;

import java.util.Map;
import java.util.UUID;

public class URStateDataTypes {
    //common dragon data - used for all dragons
    public static final StateDataType<Identifier> DRAGON_ID = new StateDataType<>(UselessReptile.id("dragon_id"));
    public static final StateDataType<AssetCache> ASSET_CACHE = new StateDataType<>(UselessReptile.id("asset_cache"));
    public static final StateDataType<SyncronizedAnimationProcessor<?>> ANIMATION_PROCESSOR = new StateDataType<>(UselessReptile.id("animation_processor"));
    public static final StateDataType<Float> BODY_X_ROTATION = new StateDataType<>(UselessReptile.id("body_x_rotation"));
    public static final StateDataType<Float> BODY_Y_ROTATION = new StateDataType<>(UselessReptile.id("body_y_rotation"));
    public static final StateDataType<Float> HEAD_X_ROTATION = new StateDataType<>(UselessReptile.id("head_x_rotation"));
    public static final StateDataType<Float> HEAD_Y_ROTATION = new StateDataType<>(UselessReptile.id("head_y_rotation"));
    public static final StateDataType<Float> YAW_SPEED = new StateDataType<>(UselessReptile.id("yaw_speed"));
    public static final StateDataType<Map<String, BoneTransform>> BONE_TRANSFORMS = new StateDataType<>(UselessReptile.id("bone_transforms"));
    //specific dragon data - used for some dragons
    public static final StateDataType<UUID> DRAGON_UUID = new StateDataType<>(UselessReptile.id("dragon_uuid"));
    public static final StateDataType<Boolean> DRAGON_IS_RIDING_PLAYER = new StateDataType<>(UselessReptile.id("dragon_is_riding_player"));
}
