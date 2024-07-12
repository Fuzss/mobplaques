package fuzs.mobplaques.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public class KeyBindingHandler {
    public static final String KEY_CATEGORY = "key.categories." + MobPlaques.MOD_ID;
    public static final KeyMapping TOGGLE_PLAQUES_KEY_MAPPING = new KeyMapping("key.togglePlaques", InputConstants.KEY_J,
            KEY_CATEGORY
    );
    private static final Component ON_COMPONENT = Component.empty().append(CommonComponents.OPTION_ON).withStyle(ChatFormatting.GREEN);
    private static final Component OFF_COMPONENT = Component.empty().append(CommonComponents.OPTION_OFF).withStyle(ChatFormatting.RED);
    public static final String KEY_MOB_PLAQUES_STATUS = "key.togglePlaques.message";

    public static void onClientTick$Start(Minecraft minecraft) {
        while (TOGGLE_PLAQUES_KEY_MAPPING.consumeClick()) {
            ModConfigSpec.ConfigValue<Boolean> enableRendering = MobPlaques.CONFIG.get(ClientConfig.class).allowRendering;
            enableRendering.set(!enableRendering.get());
            Component component = Component.translatable(KEY_MOB_PLAQUES_STATUS, enableRendering.get() ? ON_COMPONENT : OFF_COMPONENT);
            minecraft.gui.setOverlayMessage(component, false);
        }
    }
}
