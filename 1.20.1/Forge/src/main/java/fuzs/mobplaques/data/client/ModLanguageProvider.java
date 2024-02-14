package fuzs.mobplaques.data.client;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add(KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING, "Toggle Mob Plaques");
        this.add(KeyBindingHandler.KEY_MOB_PLAQUES_STATUS, "Set Mob Plaques to: %s");
        this.add(KeyBindingHandler.KEY_CATEGORY, MobPlaques.MOD_NAME);
    }
}
