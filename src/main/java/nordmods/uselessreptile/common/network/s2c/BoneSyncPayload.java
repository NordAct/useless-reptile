package nordmods.uselessreptile.common.network.s2c;

import nordmods.uselessreptile.common.entity.animation_processor.BoneTransform;

import java.util.Map;

public interface BoneSyncPayload {
    Map<String, BoneTransform> boneTransforms();
}
