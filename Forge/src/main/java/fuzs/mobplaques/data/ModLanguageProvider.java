package fuzs.mobplaques.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator gen, String modId) {
        super(gen, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("key.togglePlaques.message", "Mob Plaques are set to: %s");
        this.add("key.togglePlaques", "Toggle Mob Plaques");
    }
}
