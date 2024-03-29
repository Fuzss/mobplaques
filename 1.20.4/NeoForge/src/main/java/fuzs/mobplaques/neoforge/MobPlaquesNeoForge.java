package fuzs.mobplaques.neoforge;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(MobPlaques.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobPlaquesNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(MobPlaques.MOD_ID, MobPlaques::new);
        DataProviderHelper.registerDataProviders(MobPlaques.MOD_ID, ModLanguageProvider::new);
    }
}
