package fuzs.mobplaques.client;

import fuzs.mobplaques.client.init.ClientModRegistry;
import fuzs.puzzleslib.client.core.ClientModConstructor;

public class MobPlaquesClient implements ClientModConstructor {

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMappings(ClientModRegistry.TOGGLE_PLAQUES_KEY_MAPPING);
    }
}
