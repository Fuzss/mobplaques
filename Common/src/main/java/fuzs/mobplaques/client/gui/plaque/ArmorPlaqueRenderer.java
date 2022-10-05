package fuzs.mobplaques.client.gui.plaque;

import net.minecraft.world.entity.LivingEntity;

public class ArmorPlaqueRenderer extends MobPlaqueRenderer {

    @Override
    public int getValue(LivingEntity entity) {
        return entity.getArmorValue();
    }

    @Override
    protected int getIconX(LivingEntity entity) {
        return 34;
    }

    @Override
    protected int getIconY(LivingEntity entity) {
        return 9;
    }
}
