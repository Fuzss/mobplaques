package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ArmorPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/armor_full");

    @Override
    public int getValue(MobPlaquesRenderState renderState) {
        return renderState.armor;
    }

    @Override
    protected ResourceLocation getSprite(MobPlaquesRenderState renderState) {
        return ARMOR_FULL_SPRITE;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, MobPlaquesRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        renderState.armor = livingEntity.getArmorValue();
    }
}
