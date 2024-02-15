package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;

public class HealthPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = new ResourceLocation("hud/heart/vehicle_container");
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = new ResourceLocation("hud/heart/vehicle_full");

    public HealthPlaqueRenderer() {
        super(0x1EB100, 0xED230D);
    }

    @Override
    public boolean wantsToRender(LivingEntity entity) {
        return this.allowRendering && (!this.hideAtFullHealth(entity) || this.belowMaxValue(entity));
    }

    @Override
    public int getValue(LivingEntity entity) {
        return (int) Math.ceil(Math.min(entity.getHealth(), entity.getMaxHealth())) + this.getAbsorptionValue(entity);
    }

    private int getAbsorptionValue(LivingEntity entity) {
        return Mth.ceil(entity.getAbsorptionAmount());
    }

    @Override
    public int getMaxValue(LivingEntity entity) {
        return (int) Math.ceil(entity.getMaxHealth()) + this.getAbsorptionValue(entity);
    }

    @Override
    protected void renderIconBackground(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, LivingEntity entity) {
        this.innerRenderIcon(poseStack, bufferSource, packedLight, posX, posY, 0.01F, getContainerSprite(entity));
    }

    @Override
    protected ResourceLocation getSprite(LivingEntity entity) {
        return getFullSprite(entity);
    }

    public static ResourceLocation getContainerSprite(LivingEntity entity) {
        return isMount(entity) ? HEART_VEHICLE_CONTAINER_SPRITE : Gui.HeartType.CONTAINER.getSprite(entity instanceof Player player && player.level().getLevelData().isHardcore(), false, false);
    }

    public static ResourceLocation getFullSprite(LivingEntity entity) {
        return isMount(entity) ? HEART_VEHICLE_FULL_SPRITE : forEntity(entity).getSprite(false, false, false);
    }

    private static boolean isMount(LivingEntity entity) {
        return entity instanceof Saddleable saddleable && saddleable.isSaddled();
    }

    public static Gui.HeartType forEntity(LivingEntity entity) {
        if (entity.hasEffect(MobEffects.POISON)) {
            return Gui.HeartType.POISIONED;
        } else if (entity.hasEffect(MobEffects.WITHER)) {
            return Gui.HeartType.WITHERED;
        } else if (entity.isFullyFrozen()) {
            return Gui.HeartType.FROZEN;
        } else if (entity.getAbsorptionAmount() > 0.0F) {
            return Gui.HeartType.ABSORBING;
        }
        return Gui.HeartType.NORMAL;
    }
}
