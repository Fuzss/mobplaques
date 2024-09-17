package fuzs.mobplaques.fabric;

import fuzs.mobplaques.MobPlaques;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class MobPlaquesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(MobPlaques.MOD_ID, MobPlaques::new);
    }
}
