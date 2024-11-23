package fuzs.mobplaques.neoforge.client;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.MobPlaquesClient;
import fuzs.mobplaques.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = MobPlaques.MOD_ID, dist = Dist.CLIENT)
public class MobPlaquesNeoForgeClient {

    public MobPlaquesNeoForgeClient() {
        ClientModConstructor.construct(MobPlaques.MOD_ID, MobPlaquesClient::new);
        DataProviderHelper.registerDataProviders(MobPlaques.MOD_ID, ModLanguageProvider::new);
    }
}
