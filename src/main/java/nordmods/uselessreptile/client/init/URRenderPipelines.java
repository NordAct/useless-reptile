package nordmods.uselessreptile.client.init;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import nordmods.uselessreptile.UselessReptile;

public class URRenderPipelines {
    public static final RenderPipeline GUI_SHOCK_OVERLAY = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET).withLocation(UselessReptile.id("pipeline/gui_shock_overlay")).withBlend(BlendFunction.ADDITIVE).build()
    );
}
