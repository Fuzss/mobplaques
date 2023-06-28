package fuzs.mobplaques.client;

import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderNameTagCallback;

public class MobPlaquesClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        ClientTickEvents.START.register(KeyBindingHandler::onClientTick$Start);
        RenderNameTagCallback.EVENT.register(MobPlaqueHandler::onRenderNameTag);
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING);
    }
}
