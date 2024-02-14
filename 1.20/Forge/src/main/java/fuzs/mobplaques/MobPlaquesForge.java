package fuzs.mobplaques;

import fuzs.mobplaques.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.core.DataProviderHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(MobPlaques.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobPlaquesForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(MobPlaques.MOD_ID, MobPlaques::new);
        DataProviderHelper.registerDataProviders(MobPlaques.MOD_ID, ModLanguageProvider::new);
    }
}
