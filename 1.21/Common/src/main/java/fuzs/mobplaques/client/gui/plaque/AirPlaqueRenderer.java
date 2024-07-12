package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class AirPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation AIR_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/air");

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
    protected ResourceLocation getSprite(LivingEntity entity) {
        return AIR_SPRITE;
    }

    @Override
    public int getMaxValue(LivingEntity entity) {
        return entity.getMaxAirSupply() / 20;
    }
}
