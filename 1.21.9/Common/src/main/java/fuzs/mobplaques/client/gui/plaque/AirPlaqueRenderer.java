package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class AirPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation AIR_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/air");

    public AirPlaqueRenderer() {
        super(0x0376BB, 0xED230D);
    }

    @Override
    public boolean isRenderingAllowed(MobPlaquesRenderState renderState) {
        return this.allowRendering && this.isBelowMaxValue(renderState);
    }

    @Override
    public int getValue(MobPlaquesRenderState renderState) {
        return renderState.airSupply;
    }

    @Override
    protected ResourceLocation getSprite(MobPlaquesRenderState renderState) {
        return AIR_SPRITE;
    }

    @Override
    public int getMaxValue(MobPlaquesRenderState renderState) {
        return renderState.maxAirSupply;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, MobPlaquesRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        renderState.airSupply = Math.max(0, livingEntity.getAirSupply() / 20);
        renderState.maxAirSupply = livingEntity.getMaxAirSupply() / 20;
    }

    @Override
    public String getName() {
        return "Air";
    }
}
