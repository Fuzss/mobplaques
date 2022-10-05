package fuzs.mobplaques.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.OptionalBox;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public interface RenderNameTagCallback {
    Event<RenderNameTagCallback> RENDER = EventFactory.createArrayBacked(RenderNameTagCallback.class, listeners -> (Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick) -> {
        for (RenderNameTagCallback event : listeners) {
            Optional<Boolean> result = event.onRenderNameTag(entity, content, entityRenderer, poseStack, multiBufferSource, packedLight, partialTick);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    });

    Optional<Boolean> onRenderNameTag(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick);
}
