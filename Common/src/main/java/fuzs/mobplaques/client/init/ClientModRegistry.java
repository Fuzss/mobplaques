package fuzs.mobplaques.client.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ClientModRegistry {
    public static final KeyMapping TOGGLE_PLAQUES_KEY_MAPPING = new KeyMapping("key.togglePlaques", InputConstants.KEY_P, "key.categories.misc");
}
