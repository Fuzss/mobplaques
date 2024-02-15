package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.MobPlaques;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ToughnessPlaqueRenderer extends MobPlaqueRenderer {
    private static final ResourceLocation TOUGHNESS_FULL_SPRITE = MobPlaques.id("hud/toughness_full");

    @Override
    public int getValue(LivingEntity entity) {
        return Mth.floor(entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }

    @Override
    protected ResourceLocation getSprite(LivingEntity entity) {
        return TOUGHNESS_FULL_SPRITE;
    }
}
