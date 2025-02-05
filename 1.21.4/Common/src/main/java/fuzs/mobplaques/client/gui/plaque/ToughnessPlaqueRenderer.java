package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.MobPlaques;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ToughnessPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation TOUGHNESS_FULL_SPRITE = MobPlaques.id("hud/toughness_full");
    private static final RenderPropertyKey<Double> ARMOR_TOUGHNESS_PROPERTY = createKey("armor_toughness");

    @Override
    public boolean isRenderingAllowed(EntityRenderState renderState) {
        return super.isRenderingAllowed(renderState) &&
                RenderPropertyKey.containsRenderProperty(renderState, ARMOR_TOUGHNESS_PROPERTY);
    }

    @Override
    public int getValue(EntityRenderState renderState) {
        return Mth.floor(RenderPropertyKey.getRenderProperty(renderState, ARMOR_TOUGHNESS_PROPERTY));
    }

    @Override
    protected ResourceLocation getSprite(EntityRenderState renderState) {
        return TOUGHNESS_FULL_SPRITE;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        RenderPropertyKey.setRenderProperty(renderState, ARMOR_TOUGHNESS_PROPERTY, livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }
}
