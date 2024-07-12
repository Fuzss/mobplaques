package fuzs.mobplaques.data.client;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.handler.KeyBindingHandler;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addKeyCategory(MobPlaques.MOD_ID, MobPlaques.MOD_NAME);
        builder.add(KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING, "Toggle Mob Plaques");
        builder.add(KeyBindingHandler.KEY_MOB_PLAQUES_STATUS, "Set Mob Plaques to: %s");
    }
}
