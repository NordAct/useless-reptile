package nordmods.uselessreptile.client.renderer.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.projectile.AcidBlast;


public class AcidBlastRenderer extends BREntityRenderer<AcidBlast, EntityRenderState> {
    private static final Identifier TEXTURE = UselessReptile.id("textures/entity/acid_blast/acid_blast.png");
    public AcidBlastRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BRModelProvider() {
            private static final Identifier MODEL = UselessReptile.id("biscuit_roll/models/entity/acid_blast/acid_blast.geo.json");
            private static final Identifier ANIMATION = UselessReptile.id("biscuit_roll/animations/entity/acid_blast/acid_blast.animation.json");

            @Override
            public Identifier getModelId(BRState state) {
                return MODEL;
            }

            @Override
            public Identifier getAnimationId(BRState state) {
                return ANIMATION;
            }
        });
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }
}
