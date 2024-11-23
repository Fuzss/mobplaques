package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.client.renderer.entity.state.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ArmorPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/armor_full");
    private static final RenderPropertyKey<Integer> ARMOR_PROPERTY = createKey("armor");

    @Override
    public boolean isRenderingAllowed(EntityRenderState renderState) {
        return super.isRenderingAllowed(renderState) &&
                RenderPropertyKey.containsRenderProperty(renderState, ARMOR_PROPERTY);
    }

    @Override
    public int getValue(EntityRenderState renderState) {
        return RenderPropertyKey.getRenderProperty(renderState, ARMOR_PROPERTY);
    }

    @Override
    protected ResourceLocation getSprite(EntityRenderState renderState) {
        return ARMOR_FULL_SPRITE;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        RenderPropertyKey.setRenderProperty(renderState, ARMOR_PROPERTY, livingEntity.getArmorValue());
    }
}
