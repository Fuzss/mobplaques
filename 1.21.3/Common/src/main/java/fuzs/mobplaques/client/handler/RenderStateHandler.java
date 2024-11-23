package fuzs.mobplaques.client.handler;

import fuzs.mobplaques.client.gui.plaque.MobPlaqueRenderer;
import fuzs.mobplaques.client.renderer.entity.state.RenderPropertyKey;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;

public class RenderStateHandler {
    private static final RenderPropertyKey<EventResult> RENDER_PLAQUE_PROPERTY = MobPlaqueRenderer.createKey("render_plaque");

    public static void onExtractRenderState(Entity entity, EntityRenderer<?, ?> entityRenderer, EntityRenderState renderState, float partialTick, RenderPropertyKey.RenderPropertySetter renderPropertySetter) {
        if (entity instanceof LivingEntity livingEntity) {
            boolean canPlaqueRender = MobPlaqueHandler.canPlaqueRender(entity, entityRenderer, partialTick);
            if (canPlaqueRender && renderState.nameTag == null) {
                renderState.nameTag = CommonComponents.EMPTY;
                renderState.nameTagAttachment = entity.getAttachments()
                        .getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));
            }
            RenderPropertyKey.setRenderProperty(renderState, RENDER_PLAQUE_PROPERTY, EventResult.PASS);
        }
    }
}
