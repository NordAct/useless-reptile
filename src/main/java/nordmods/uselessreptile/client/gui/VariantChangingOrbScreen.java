package nordmods.uselessreptile.client.gui;

import com.mojang.math.Axis;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.util.FakeDragon;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
public class VariantChangingOrbScreen extends Screen {
    private final FakeDragon fakeDragon;
    private static final int DRAGON_WIDTH = 200;
    private static final int DRAGON_HEIGHT = 60;

    public VariantChangingOrbScreen(DragonVariantType<?> variantType, String variantName) {
        super(GameNarrator.NO_TITLE);
        this.fakeDragon = new FakeDragon(variantType, variantName);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int centerX = width/2;
        int centerY = height/2;

        extractFakeDragon(
                context,
                centerX - DRAGON_WIDTH/2,
                centerY - width/2,
                centerX + DRAGON_WIDTH/2,
                centerY + width/2,
                mouseX,
                mouseY
        );
        super.extractBackground(context, mouseX, mouseY, delta);
    }

    private void extractFakeDragon(GuiGraphicsExtractor context, int x0, int y0, int x1, int y1, float mouseX, float mouseY) {
        if (!ResourceUtil.isResourceReloadFinished) return;

        float centerX = (x0 + x1) / 2f;
        float centerY = (y0 + y1) / 2f;
        float dx = (float)Math.atan((centerX - mouseX) / 40f);
        float dy = (float) Math.atan((centerY - mouseY) / 40f);

        context.enableScissor(x0, y0, x1, y1);

        LivingEntityRenderState state = fakeDragon.createDragonRenderState();
        state.setStateData(ClientStateDataTypes.LIGHT, LightCoordsUtil.FULL_BRIGHT);

        Quaternionf rot = new Quaternionf();
        Quaternionf cam = Axis.XP.rotationDegrees(-dy * 20 + 180).mul(Axis.YP.rotationDegrees(-dx * 40));
        rot.mul(cam);

        float scale = state.getStateData(StateDataTypes.SCALE, 1f);
        scale = 400/(scale * scale);
        context.entity(
                state,
                scale,
                new Vector3f(0, height/3f/scale, 0),
                rot,
                cam,
                x0, y0,
                x1, y1
        );

        context.disableScissor();
    }
}
