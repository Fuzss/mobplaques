package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ToughnessPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation TOUGHNESS_FULL_SPRITE = MobPlaques.id("hud/toughness_full");

    @Override
    public int getValue(MobPlaquesRenderState renderState) {
        return renderState.toughness;
    }

    @Override
    protected ResourceLocation getSprite(MobPlaquesRenderState renderState) {
        return TOUGHNESS_FULL_SPRITE;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, MobPlaquesRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        renderState.toughness = Mth.floor(livingEntity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }
}
