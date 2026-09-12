package nordmods.uselessreptile.client.init;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
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
