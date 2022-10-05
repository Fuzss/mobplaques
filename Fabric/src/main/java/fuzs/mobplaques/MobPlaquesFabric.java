package fuzs.mobplaques;

import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;

public class MobPlaquesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(MobPlaques.MOD_ID).accept(new MobPlaques());
    }
}
