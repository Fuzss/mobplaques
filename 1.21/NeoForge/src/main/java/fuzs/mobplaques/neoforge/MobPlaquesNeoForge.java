package fuzs.mobplaques.neoforge;

import fuzs.mobplaques.MobPlaques;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(MobPlaques.MOD_ID)
public class MobPlaquesNeoForge {

    public MobPlaquesNeoForge() {
        ModConstructor.construct(MobPlaques.MOD_ID, MobPlaques::new);
    }
}
