package fuzs.mobplaques.client.handler;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.init.ClientModRegistry;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.config.core.AbstractConfigValue;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class KeyBindingHandler {
    private static final Component ON_COMPONENT = Component.empty().append(CommonComponents.OPTION_ON).withStyle(ChatFormatting.GREEN);
    private static final Component OFF_COMPONENT = Component.empty().append(CommonComponents.OPTION_OFF).withStyle(ChatFormatting.RED);

    public static void onClientTick$Start(Minecraft minecraft) {
        while (ClientModRegistry.TOGGLE_PLAQUES_KEY_MAPPING.consumeClick()) {
            AbstractConfigValue<Boolean> enableRendering = MobPlaques.CONFIG.get(ClientConfig.class).allowRendering;
            enableRendering.set(!enableRendering.get());
            Component component = Component.translatable("key.togglePlaques.message", enableRendering.get() ? ON_COMPONENT : OFF_COMPONENT);
            minecraft.gui.setOverlayMessage(component, false);
        }
    }
}
