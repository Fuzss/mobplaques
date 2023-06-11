package fuzs.mobplaques.client.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class ClientModRegistry extends RenderType {
    public static final Function<ResourceLocation, RenderType> ICON = Util.memoize((resourceLocation) -> {
        return RenderType.create("icon", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
    });
    public static final Function<ResourceLocation, RenderType> ICON_SEE_THROUGH = Util.memoize((resourceLocation) -> {
        return RenderType.create("icon_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });

    private ClientModRegistry(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }
}
