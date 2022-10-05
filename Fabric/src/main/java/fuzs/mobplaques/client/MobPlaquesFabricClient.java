package fuzs.mobplaques.client;

import fuzs.mobplaques.api.client.event.RenderNameTagCallback;
import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MobPlaquesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.START_CLIENT_TICK.register(KeyBindingHandler::onClientTick$Start);
        RenderNameTagCallback.RENDER.register(MobPlaqueHandler::onRenderNameTag);
    }
}
