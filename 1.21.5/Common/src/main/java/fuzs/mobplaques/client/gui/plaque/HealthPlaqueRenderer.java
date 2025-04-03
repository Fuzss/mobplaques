package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;

public class HealthPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/heart/vehicle_container");
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/heart/vehicle_full");
    private static final RenderPropertyKey<Float> HEALTH_PROPERTY = createKey("health");
    private static final RenderPropertyKey<Float> MAX_HEALTH_PROPERTY = createKey("max_health");
    private static final RenderPropertyKey<Float> ABSORPTION_PROPERTY = createKey("absorption");
    private static final RenderPropertyKey<ResourceLocation> SPRITE_PROPERTY = createKey("sprite");
    private static final RenderPropertyKey<ResourceLocation> CONTAINER_SPRITE_PROPERTY = createKey("container_sprite");

    public HealthPlaqueRenderer() {
        super(0x1EB100, 0xED230D);
    }

    @Override
    public boolean isRenderingAllowed(EntityRenderState renderState) {
        return this.allowRendering && RenderPropertyKey.containsRenderProperty(renderState, HEALTH_PROPERTY);
    }

    @Override
    public int getValue(EntityRenderState renderState) {
        float health = RenderPropertyKey.getRenderProperty(renderState, HEALTH_PROPERTY);
        float maxHealth = RenderPropertyKey.getRenderProperty(renderState, MAX_HEALTH_PROPERTY);
        return (int) Math.ceil(Math.min(health, maxHealth)) + this.getAbsorptionValue(renderState);
    }

    private int getAbsorptionValue(EntityRenderState renderState) {
        return Mth.ceil(RenderPropertyKey.getRenderProperty(renderState, ABSORPTION_PROPERTY));
    }

    @Override
    public int getMaxValue(EntityRenderState renderState) {
        return (int) Math.ceil(RenderPropertyKey.getRenderProperty(renderState, MAX_HEALTH_PROPERTY)) +
                this.getAbsorptionValue(renderState);
    }

    @Override
    protected void renderIconBackground(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, EntityRenderState renderState) {
        ResourceLocation resourceLocation = RenderPropertyKey.getRenderProperty(renderState, CONTAINER_SPRITE_PROPERTY);
        this.innerRenderIcon(poseStack, bufferSource, packedLight, posX, posY, 0.01F, resourceLocation);
    }

    @Override
    protected ResourceLocation getSprite(EntityRenderState renderState) {
        return RenderPropertyKey.getRenderProperty(renderState, SPRITE_PROPERTY);
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, EntityRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        RenderPropertyKey.setRenderProperty(renderState, HEALTH_PROPERTY, livingEntity.getHealth());
        RenderPropertyKey.setRenderProperty(renderState, MAX_HEALTH_PROPERTY, livingEntity.getMaxHealth());
        RenderPropertyKey.setRenderProperty(renderState, ABSORPTION_PROPERTY, livingEntity.getAbsorptionAmount());
        RenderPropertyKey.setRenderProperty(renderState, SPRITE_PROPERTY, getSprite(livingEntity));
        RenderPropertyKey.setRenderProperty(renderState, CONTAINER_SPRITE_PROPERTY, getContainerSprite(livingEntity));
    }

    public static ResourceLocation getContainerSprite(LivingEntity livingEntity) {
        return isMount(livingEntity) ? HEART_VEHICLE_CONTAINER_SPRITE : Gui.HeartType.CONTAINER.getSprite(
                livingEntity instanceof Player player && player.level().getLevelData().isHardcore(), false, false);
    }

    public static ResourceLocation getSprite(LivingEntity livingEntity) {
        return isMount(livingEntity) ? HEART_VEHICLE_FULL_SPRITE :
                forEntity(livingEntity).getSprite(false, false, false);
    }

    private static boolean isMount(LivingEntity livingEntity) {
        return livingEntity instanceof Saddleable saddleable && saddleable.isSaddled();
    }

    private static Gui.HeartType forEntity(LivingEntity livingEntity) {
        if (livingEntity.hasEffect(MobEffects.POISON)) {
            return Gui.HeartType.POISIONED;
        } else if (livingEntity.hasEffect(MobEffects.WITHER)) {
            return Gui.HeartType.WITHERED;
        } else if (livingEntity.isFullyFrozen()) {
            return Gui.HeartType.FROZEN;
        } else if (livingEntity.getAbsorptionAmount() > 0.0F) {
            return Gui.HeartType.ABSORBING;
        }
        return Gui.HeartType.NORMAL;
    }
}
