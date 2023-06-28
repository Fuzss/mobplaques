package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class TransitionPlaqueRenderer extends MobPlaqueRenderer {
    private final int defaultHighColor;
    private final int defaultLowColor;
    private boolean shiftColors;
    private PlaqueValue plaqueValue;

    protected TransitionPlaqueRenderer(int defaultHighColor, int defaultLowColor) {
        this.defaultHighColor = defaultHighColor;
        this.defaultLowColor = defaultLowColor;
    }

    public abstract int getMaxValue(LivingEntity entity);

    protected boolean belowMaxValue(LivingEntity entity) {
        return this.getValue(entity) < this.getMaxValue(entity);
    }

    @Override
    protected Component getComponent(LivingEntity entity) {
        return switch (this.plaqueValue) {
            case DEFAULT -> super.getComponent(entity);
            case INCLUDE_MAX -> Component.literal(this.getValue(entity) + "/" + this.getMaxValue(entity));
            case RELATIVE_PERCENTAGE -> Component.literal((int) (this.getValuePercentage(entity) * 100.0F) + "%");
        };
    }

    @Override
    protected int getColor(LivingEntity entity) {
        if (!this.shiftColors) {
            return super.getColor(entity);
        }
        return getTransitionedColor(this.defaultHighColor, this.defaultLowColor, this.getValuePercentage(entity));
    }

    private float getValuePercentage(LivingEntity entity) {
        return Mth.clamp(this.getValue(entity) / (float) this.getMaxValue(entity), 0.0F, 1.0F);
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        super.setupConfig(builder, callback);
        callback.accept(builder.comment("Transition text colors depending on current percentage.").define("shift_colors", false), v -> this.shiftColors = v);
        callback.accept(builder.comment("Show current amount either as percentage or as absolute value.").defineEnum("plaque_value", PlaqueValue.RELATIVE_PERCENTAGE), v -> this.plaqueValue = v);
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

    private enum PlaqueValue {
        DEFAULT, INCLUDE_MAX, RELATIVE_PERCENTAGE
    }
}
