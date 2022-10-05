package fuzs.mobplaques.client.handler;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.init.ClientModRegistry;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.config.core.AbstractConfigValue;
import net.minecraft.client.Minecraft;

public class KeyBindingHandler {

    public static void onClientTick$Start(Minecraft minecraft) {
        while (ClientModRegistry.TOGGLE_PLAQUES_KEY_MAPPING.consumeClick()) {
            AbstractConfigValue<Boolean> enableRendering = MobPlaques.CONFIG.get(ClientConfig.class).enableRendering;
            enableRendering.set(!enableRendering.get());
        }
    }
}
