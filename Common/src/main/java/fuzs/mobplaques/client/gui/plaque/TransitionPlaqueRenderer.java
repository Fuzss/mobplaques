package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.config.ValueCallback;
import fuzs.puzzleslib.config.core.AbstractConfigBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class TransitionPlaqueRenderer extends MobPlaqueRenderer {
    private boolean shiftColors;
    private boolean relativeValue;

    protected abstract int getHighColor();

    protected abstract int getLowColor();

    public abstract int getMaxValue(LivingEntity entity);

    protected boolean belowMaxValue(LivingEntity entity) {
        return this.getValue(entity) < this.getMaxValue(entity);
    }

    @Override
    protected Component getComponent(LivingEntity entity) {
        if (!this.relativeValue) {
            return super.getComponent(entity);
        }
        return Component.literal((int) (this.getValuePercentage(entity) * 100.0F) + "%");
    }

    @Override
    protected int getColor(LivingEntity entity) {
        if (!this.shiftColors) {
            return super.getColor(entity);
        }
        return getTransitionedColor(this.getHighColor(), this.getLowColor(), this.getValuePercentage(entity));
    }

    private float getValuePercentage(LivingEntity entity) {
        return Mth.clamp(this.getValue(entity) / (float) this.getMaxValue(entity), 0.0F, 1.0F);
    }

    @Override
    public void setupConfig(AbstractConfigBuilder builder, ValueCallback callback) {
        super.setupConfig(builder, callback);
        callback.accept(builder.comment("Transition text colors depending on current percentage.").define("shift_colors", true), v -> this.shiftColors = v);
        callback.accept(builder.comment("Show current amount as percentage instead of as absolute value.").define("relative_value", false), v -> this.relativeValue = v);
    }

    private static int getTransitionedColor(int startColor, int endColor, float transition) {
        int startR = startColor >> 16 & 0xFF;
        int startG = startColor >> 8 & 0xFF;
        int startB = startColor >> 0 & 0xFF;
        int endR = endColor >> 16 & 0xFF;
        int endG = endColor >> 8 & 0xFF;
        int endB = endColor >> 0 & 0xFF;
        int colorR = endR + (int) ((startR - endR) * transition);
        int colorG = endG + (int) ((startG - endG) * transition);
        int colorB = endB + (int) ((startB - endB) * transition);
        int color = 0;
        color |= colorR << 16;
        color |= colorG << 8;
        color |= colorB << 0;
        return color;
    }

    private static int getTransitionedColor2(int startColor, int endColor, float transition) {
        transition = 1.0F - transition;
        int startR = startColor >> 16 & 0xFF;
        int startG = startColor >> 8 & 0xFF;
        int startB = startColor >> 0 & 0xFF;
        int endR = endColor >> 16 & 0xFF;
        int endG = endColor >> 8 & 0xFF;
        int endB = endColor >> 0 & 0xFF;
        int colorR = startR + (startR > endR ? -1 : 1) * (int) (Math.abs(startR - endR) * transition);
        int colorG = startG + (startG > endG ? -1 : 1) * (int) (Math.abs(startG - endG) * transition);
        int colorB = startB + (startB > endB ? -1 : 1) * (int) (Math.abs(startB - endB) * transition);
        int color = 0;
        color |= colorR << 16;
        color |= colorG << 8;
        color |= colorB << 0;
        return color;
    }
}
