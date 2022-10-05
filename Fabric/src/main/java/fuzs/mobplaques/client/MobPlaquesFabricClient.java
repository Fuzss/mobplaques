package fuzs.mobplaques.client;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.api.client.event.RenderNameTagCallback;
import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MobPlaquesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCoreServices.FACTORIES.clientModConstructor(MobPlaques.MOD_ID).accept(new MobPlaquesClient());
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.START_CLIENT_TICK.register(KeyBindingHandler::onClientTick$Start);
        RenderNameTagCallback.RENDER.register(MobPlaqueHandler::onRenderNameTag);
    }
}
