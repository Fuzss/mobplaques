package fuzs.mobplaques.common.client.renderer.rendertype;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import fuzs.mobplaques.common.MobPlaques;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class ModRenderType {
    /**
     * Disable depth write as it prevents water behind the text background from rendering.
     *
     * @see RenderPipelines#TEXT_BACKGROUND
     */
    public static final RenderPipeline TEXT_BACKGROUND_PIPELINE = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.FOG)
            .withLocation("pipeline/text_background")
            .withVertexShader("core/text_background")
            .withFragmentShader("core/text_background")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build();
    /**
     * @see RenderType#TEXT_BACKGROUND
     */
    private static final RenderType TEXT_BACKGROUND = RenderType.create(MobPlaques.id("text_background").toString(),
            RenderSetup.builder(TEXT_BACKGROUND_PIPELINE).useLightmap().sortOnUpload().createRenderSetup());

    private ModRenderType() {
        // NO-OP
    }

    public static RenderType textBackground() {
        return TEXT_BACKGROUND;
    }
}
