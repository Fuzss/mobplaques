package fuzs.mobplaques.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record RenderPropertyKey<T>(ResourceLocation resourceLocation) {

    @NotNull
    public static <T> T getRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        T renderProperty = ((EntityRenderStateExtension) renderState).mobplaques$getRenderProperty(key);
        Objects.requireNonNull(renderProperty, "render property " + key + " is null");
        return renderProperty;
    }

    public static <T> boolean containsRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        T renderProperty = ((EntityRenderStateExtension) renderState).mobplaques$getRenderProperty(key);
        return renderProperty != null;
    }

    public static <T> void setRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key, @Nullable T t) {
        ((EntityRenderStateExtension) renderState).mobplaques$setRenderProperty(key, t);
    }

    public static void clearRenderProperties(EntityRenderState renderState) {
        ((EntityRenderStateExtension) renderState).mobplaques$clearRenderProperties();
    }
}
