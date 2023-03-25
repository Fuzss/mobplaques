package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;

public class HealthPlaqueRenderer extends TransitionPlaqueRenderer {

    @Override
    public boolean wantsToRender(LivingEntity entity) {
        return this.allowRendering && (!this.hideAtFullHealth(entity) || this.belowMaxValue(entity));
    }

    @Override
    protected int getHighColor() {
        return ChatFormatting.GREEN.getColor();
    }

    @Override
    protected int getLowColor() {
        return ChatFormatting.RED.getColor();
    }

    @Override
    public int getValue(LivingEntity entity) {
        return (int) Math.ceil(entity.getHealth()) + this.getAbsorptionValue(entity);
    }

    private int getAbsorptionValue(LivingEntity entity) {
        return Mth.ceil(entity.getAbsorptionAmount());
    }

    @Override
    public int getMaxValue(LivingEntity entity) {
        return (int) Math.ceil(entity.getMaxHealth()) + this.getAbsorptionValue(entity);
    }

    @Override
    protected void renderIconBackground(PoseStack poseStack, int posX, int posY, LivingEntity entity) {
        GuiComponent.blit(poseStack, posX, posY, HeartType.CONTAINER.getTextureX(), HeartType.CONTAINER.getTextureY(), ICON_SIZE, ICON_SIZE, 256, 256);
        poseStack.translate(0.0F, 0.0F, -0.03F);
    }

    @Override
    protected int getIconX(LivingEntity entity) {
        return HeartType.selectHeartType(entity).getTextureX();
    }

    @Override
    protected int getIconY(LivingEntity entity) {
        return HeartType.selectHeartType(entity).getTextureY();
    }

    private enum HeartType {
        CONTAINER(0),
        NORMAL(2),
        POISONED(4),
        WITHERED(6),
        ABSORBING(8),
        FROZEN(9),
        MOUNT(4, 1, false);

        private final int indexX;
        private final int indexY;
        private final boolean hardcoreVariant;

        HeartType(int indexX) {
            this(indexX, 0, true);
        }

        HeartType(int indexX, int indexY, boolean hardcoreVariant) {
            this.indexX = indexX;
            this.indexY = indexY;
            this.hardcoreVariant = hardcoreVariant;
        }

        public int getTextureX() {
            return 16 + this.indexX * 18;
        }

        public int getTextureY() {
            return this.getIndexY(false) * 9;
        }

        public int getIndexY(boolean hardcore) {
            if (hardcore && this.hardcoreVariant) {
                return 5;
            }
            return this.indexY;
        }

        public static HeartType selectHeartType(LivingEntity entity) {
            if (entity.hasEffect(MobEffects.POISON)) {
                return POISONED;
            } else if (entity.hasEffect(MobEffects.WITHER)) {
                return WITHERED;
            } else if (entity.isFullyFrozen()) {
                return FROZEN;
            } else if (entity.getAbsorptionAmount() > 0.0F) {
                return ABSORBING;
            }
            return entity instanceof Saddleable saddleable && saddleable.isSaddled() ? MOUNT : NORMAL;
        }
    }
}
