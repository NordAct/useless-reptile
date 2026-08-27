package nordmods.uselessreptile.common.entity.animation_processor;

import com.mojang.math.Axis;
import libs.gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MultipartDragonAnimationProcessor<T extends URDragonEntity & MultipartEntity> extends DragonAnimationProcessor<T> {
    public MultipartDragonAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public void tick() {
        super.tick();
        BRModel model = getModel();
        if (model == null) return;
        for (EntityPart part : animatable.getParts()) {
            URDragonPart dragonPart = (URDragonPart) part;
            LocatorTransformation transformation = model.getLocatorTransformation(dragonPart.name);
            if (transformation != null) {
                Vector4f vec = transformation.matrix().transform(new Vector4f(0, 0, 0, 1));
                Vector3f pos = new Vector3f(vec.x(), vec.y() - dragonPart.getBbHeight() / 2, vec.z()).rotate(Axis.YP.rotationDegrees(180));
                dragonPart.setRelativePos(pos);
            }
        }
    }
}
