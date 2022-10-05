package fuzs.mobplaques.client.core;

import net.minecraft.world.entity.LivingEntity;

public class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isBossMob(LivingEntity entity) {
        return !entity.canChangeDimensions();
    }
}
