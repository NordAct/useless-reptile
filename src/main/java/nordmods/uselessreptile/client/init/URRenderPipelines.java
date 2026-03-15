package nordmods.uselessreptile.client.init;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import nordmods.uselessreptile.UselessReptile;

public class URRenderPipelines {
    public static final RenderPipeline GUI_SHOCK_OVERLAY = RenderPipelines.register(
            RenderPipeline
                    .builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                    .withLocation(UselessReptile.id("pipeline/gui_shock_overlay"))
                    .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
                    .build()
    );

    public static void init() {}
}
