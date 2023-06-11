package fuzs.mobplaques.client.gui.plaque;

import net.minecraft.world.entity.LivingEntity;

public class AirPlaqueRenderer extends TransitionPlaqueRenderer {

    public AirPlaqueRenderer() {
        super(0x0376BB, 0xED230D);
    }

    @Override
    public boolean wantsToRender(LivingEntity entity) {
        return this.allowRendering && !this.hideAtFullHealth(entity) && this.belowMaxValue(entity);
    }

    @Override
    public int getValue(LivingEntity entity) {
        return Math.max(0, entity.getAirSupply() / 20);
    }

    @Override
    public int getMaxValue(LivingEntity entity) {
        return entity.getMaxAirSupply() / 20;
    }

    @Override
    protected int getIconX(LivingEntity entity) {
        return 16;
    }

    @Override
    protected int getIconY(LivingEntity entity) {
        return 18;
    }
}
