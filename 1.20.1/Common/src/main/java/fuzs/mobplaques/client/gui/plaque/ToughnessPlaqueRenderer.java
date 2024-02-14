package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.MobPlaques;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ToughnessPlaqueRenderer extends MobPlaqueRenderer {
    public static final ResourceLocation TOUGHNESS_ICONS_LOCATION = MobPlaques.id("textures/gui/icons.png");

    @Override
    public int getValue(LivingEntity entity) {
        return Mth.floor(entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }

    @Override
    protected int getIconX(LivingEntity entity) {
        return 18;
    }

    @Override
    protected int getIconY(LivingEntity entity) {
        return 0;
    }

    @Override
    protected ResourceLocation getTextureSheet() {
        return TOUGHNESS_ICONS_LOCATION;
    }
}
