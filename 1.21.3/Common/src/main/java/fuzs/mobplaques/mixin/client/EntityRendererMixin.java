package fuzs.mobplaques.mixin.client;

import fuzs.mobplaques.client.handler.RenderStateHandler;
import fuzs.mobplaques.client.renderer.entity.state.EntityRenderStateExtension;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @Shadow
    @Final
    private S reusedState;

    @Inject(
            method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
            at = @At("TAIL")
    )
    public void createRenderState(T entity, float partialTick, CallbackInfoReturnable<S> callback) {
        S renderState = this.reusedState;
        ((EntityRenderStateExtension) renderState).mobplaques$clearRenderProperties();
        RenderStateHandler.onExtractRenderState(entity, EntityRenderer.class.cast(this), renderState, partialTick, ((EntityRenderStateExtension) renderState)::mobplaques$setRenderProperty);
    }
}
