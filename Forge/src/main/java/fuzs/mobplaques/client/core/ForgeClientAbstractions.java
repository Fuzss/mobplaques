package fuzs.mobplaques.client.core;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.Tags;

public class ForgeClientAbstractions implements ClientAbstractions {

    @Override
    public boolean isBossMob(LivingEntity entity) {
        return entity.getType().is(Tags.EntityTypes.BOSSES);
    }
}
