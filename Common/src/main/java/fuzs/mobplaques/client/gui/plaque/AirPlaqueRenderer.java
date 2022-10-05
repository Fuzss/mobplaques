package fuzs.mobplaques.client.gui.plaque;

import net.minecraft.ChatFormatting;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;

public class AirPlaqueRenderer extends TransitionPlaqueRenderer {

    @Override
    public boolean wantsToRender(LivingEntity entity) {
        return entity.isEyeInFluid(FluidTags.WATER) || this.belowMaxValue(entity);
    }

    @Override
    public int getValue(LivingEntity entity) {
        return Math.max(0, entity.getAirSupply() / 20);
    }

    @Override
    protected int getHighColor() {
        return ChatFormatting.AQUA.getColor();
    }

    @Override
    protected int getLowColor() {
        return ChatFormatting.RED.getColor();
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
