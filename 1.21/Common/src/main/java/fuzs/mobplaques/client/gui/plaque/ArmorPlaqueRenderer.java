package fuzs.mobplaques.client.gui.plaque;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ArmorPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation ARMOR_FULL_SPRITE = new ResourceLocation("hud/armor_full");

    @Override
    public int getValue(LivingEntity entity) {
        return entity.getArmorValue();
    }

    @Override
    protected ResourceLocation getSprite(LivingEntity entity) {
        return ARMOR_FULL_SPRITE;
    }
}
