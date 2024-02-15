package fuzs.mobplaques.fabric.client;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.MobPlaquesClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class MobPlaquesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(MobPlaques.MOD_ID, MobPlaquesClient::new);
    }
}
