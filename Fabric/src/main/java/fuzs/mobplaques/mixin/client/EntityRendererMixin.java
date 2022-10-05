package fuzs.mobplaques.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mobplaques.api.client.event.RenderNameTagCallback;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void mobplaques$render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo callback) {
        RenderNameTagCallback.RENDER.invoker().onRenderNameTag(entity, entity.getDisplayName(), (EntityRenderer<?>) (Object) this, poseStack, buffer, packedLight, partialTick).ifPresent(result -> {
            boolean shouldShowName = this.shouldShowName(entity);
            if (shouldShowName && !result) {
                callback.cancel();
            } else if (!shouldShowName && result) {
                this.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight);
            }
        });
    }

    @Shadow
    protected abstract boolean shouldShowName(T entity);

    @Shadow
    protected abstract void renderNameTag(T entity, Component displayName, PoseStack matrixStack, MultiBufferSource buffer, int packedLight);
}
