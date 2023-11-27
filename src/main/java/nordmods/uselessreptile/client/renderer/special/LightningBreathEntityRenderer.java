package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;

public class LightningBreathEntityRenderer extends EntityRenderer<LightningBreathEntity> {
    public LightningBreathEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(LightningBreathEntity entity) {
        return new Identifier(UselessReptile.MODID, "textures/misc/shockwave.png");
    }
}
