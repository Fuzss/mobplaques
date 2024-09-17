package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ArmorPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/armor_full");

    @Override
    public int getValue(LivingEntity entity) {
        return entity.getArmorValue();
    }

    @Override
    protected ResourceLocation getSprite(LivingEntity entity) {
        return ARMOR_FULL_SPRITE;
    }
}
