package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class HealthPlaqueRenderer extends TransitionPlaqueRenderer {
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = MobPlaques.id("hud/heart/vehicle_full");

    public HealthPlaqueRenderer() {
        super(0x1EB100, 0xED230D);
    }

    @Override
    public int getValue(MobPlaquesRenderState renderState) {
        return Math.min(renderState.health, renderState.maxHealth) + renderState.absorption;
    }

    @Override
    public int getMaxValue(MobPlaquesRenderState renderState) {
        return renderState.maxHealth + renderState.absorption;
    }

    @Override
    protected ResourceLocation getSprite(MobPlaquesRenderState renderState) {
        return renderState.sprite;
    }

    @Override
    public void extractRenderState(LivingEntity livingEntity, MobPlaquesRenderState renderState, float partialTick) {
        super.extractRenderState(livingEntity, renderState, partialTick);
        renderState.health = Mth.ceil(livingEntity.getHealth());
        renderState.maxHealth = Mth.ceil(livingEntity.getMaxHealth());
        renderState.absorption = Mth.ceil(livingEntity.getAbsorptionAmount());
        renderState.sprite = this.getSprite(livingEntity);
    }

    @Override
    public String getName() {
        return "Health";
    }

    private ResourceLocation getSprite(LivingEntity livingEntity) {
        return isMount(livingEntity) ? HEART_VEHICLE_FULL_SPRITE : getSprite(forEntity(livingEntity), livingEntity);
    }

    public static ResourceLocation getSprite(Gui.HeartType heartType) {
        return getSprite(heartType, null);
    }

    private static ResourceLocation getSprite(Gui.HeartType heartType, @Nullable LivingEntity livingEntity) {
        ResourceLocation resourceLocation = heartType.getSprite(isHardcore(livingEntity), false, false);
        return MobPlaques.id(resourceLocation.getPath());
    }

    private static boolean isHardcore(@Nullable LivingEntity livingEntity) {
        return livingEntity instanceof Player player && player.level().getLevelData().isHardcore();
    }

    private static boolean isMount(LivingEntity livingEntity) {
        return livingEntity instanceof Mob mob && mob.isSaddled();
    }

    /**
     * @see net.minecraft.client.gui.Gui.HeartType#forPlayer(Player)
     */
    private static Gui.HeartType forEntity(LivingEntity livingEntity) {
        if (livingEntity.hasEffect(MobEffects.POISON)) {
            return Gui.HeartType.POISIONED;
        } else if (livingEntity.hasEffect(MobEffects.WITHER)) {
            return Gui.HeartType.WITHERED;
        } else if (livingEntity.isFullyFrozen()) {
            return Gui.HeartType.FROZEN;
        } else if (livingEntity.getAbsorptionAmount() > 0.0F) {
            return Gui.HeartType.ABSORBING;
        } else {
            return Gui.HeartType.NORMAL;
        }
    }
}
