package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class AirPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation AIR_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/air");
    private static final RenderPropertyKey<Integer> AIR_SUPPLY_PROPERTY = createKey("air_supply");
    private static final RenderPropertyKey<Integer> MAX_AIR_SUPPLY_PROPERTY = createKey("max_air_supply");

    public AirPlaqueRenderer() {
        super(0x0376BB, 0xED230D);
    }

    @Override
    public boolean isRenderingAllowed(EntityRenderState renderState) {
        return this.allowRendering && this.isBelowMaxValue(renderState) && RenderPropertyKey.containsRenderProperty(renderState, AIR_SUPPLY_PROPERTY);
    }

    @Override
    public int getValue(EntityRenderState renderState) {
        return Math.max(0, RenderPropertyKey.getRenderProperty(renderState, AIR_SUPPLY_PROPERTY) / 20);
    }

    @Override
    protected ResourceLocation getSprite(EntityRenderState renderState) {
        return AIR_SPRITE;
    }

    @Override
    public int getMaxValue(EntityRenderState renderState) {
        return RenderPropertyKey.getRenderProperty(renderState, MAX_AIR_SUPPLY_PROPERTY) / 20;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        RenderPropertyKey.setRenderProperty(renderState, AIR_SUPPLY_PROPERTY, livingEntity.getAirSupply());
        RenderPropertyKey.setRenderProperty(renderState, MAX_AIR_SUPPLY_PROPERTY, livingEntity.getMaxAirSupply());
    }
}