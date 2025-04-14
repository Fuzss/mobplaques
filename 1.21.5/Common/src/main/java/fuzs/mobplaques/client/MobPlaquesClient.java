package fuzs.mobplaques.client;

import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.mobplaques.client.handler.PickEntityHandler;
import fuzs.mobplaques.client.renderer.ModRenderType;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderPipelinesContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.GameRenderEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderNameTagCallback;

public class MobPlaquesClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ExtractRenderStateCallback.EVENT.register(MobPlaqueHandler::onExtractRenderState);
        RenderNameTagCallback.EVENT.register(MobPlaqueHandler::onRenderNameTag);
        GameRenderEvents.BEFORE.register(PickEntityHandler::onBeforeGameRender);
        ClientTickEvents.START.register(PickEntityHandler::onStartClientTick);
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        KeyBindingHandler.onRegisterKeyMappings(context);
    }

    @Override
    public void onRegisterRenderPipelines(RenderPipelinesContext context) {
        context.registerRenderPipeline(ModRenderType.TEXT_BACKGROUND_PIPELINE);
    }
}
