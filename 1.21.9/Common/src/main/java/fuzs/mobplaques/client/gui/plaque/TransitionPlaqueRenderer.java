package fuzs.mobplaques.client.gui.plaque;

import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class TransitionPlaqueRenderer extends MobPlaqueRenderer {
    private final int defaultHighColor;
    private final int defaultLowColor;
    private boolean shiftColors;
    private PlaqueValue plaqueValue;

    protected TransitionPlaqueRenderer(int defaultHighColor, int defaultLowColor) {
        this.defaultHighColor = defaultHighColor;
        this.defaultLowColor = defaultLowColor;
    }

    public abstract int getMaxValue(MobPlaquesRenderState renderState);

    protected boolean isBelowMaxValue(MobPlaquesRenderState renderState) {
        return this.getValue(renderState) < this.getMaxValue(renderState);
    }

    @Override
    protected MutableComponent getTextComponent(MobPlaquesRenderState renderState) {
        return switch (this.plaqueValue) {
            case ABSOLUTE -> super.getTextComponent(renderState);
            case ABSOLUTE_WITH_MAX -> Component.literal(
                    this.getValue(renderState) + "/" + this.getMaxValue(renderState));
            case RELATIVE -> Component.literal((int) (this.getValuePercentage(renderState) * 100.0F) + "%");
        };
    }

    @Override
    protected int getColor(MobPlaquesRenderState renderState) {
        if (!this.shiftColors) {
            return super.getColor(renderState);
        } else {
            float transition = this.getValuePercentage(renderState);
            return packTransitionedColor(this.defaultHighColor, this.defaultLowColor, transition);
        }
    }

    private float getValuePercentage(MobPlaquesRenderState renderState) {
        return Mth.clamp(this.getValue(renderState) / (float) this.getMaxValue(renderState), 0.0F, 1.0F);
    }

    @Override
    public void setupConfig(ModConfigSpec.Builder builder, ValueCallback callback) {
        super.setupConfig(builder, callback);
        callback.accept(builder.comment("Transition text colors depending on current percentage.")
                .define("shift_colors", false), v -> this.shiftColors = v);
        callback.accept(builder.comment("Show current amount either as percentage or as absolute value.")
                .defineEnum("plaque_value", PlaqueValue.RELATIVE), v -> this.plaqueValue = v);
    }

    public static int packTransitionedColor(int startColor, int endColor, float transition) {
        int startR = ARGB.red(startColor);
        int startG = ARGB.green(startColor);
        int startB = ARGB.blue(startColor);
        int endR = ARGB.red(endColor);
        int endG = ARGB.green(endColor);
        int endB = ARGB.blue(endColor);
        int colorR = endR + (int) ((startR - endR) * transition);
        int colorG = endG + (int) ((startG - endG) * transition);
        int colorB = endB + (int) ((startB - endB) * transition);
        return ARGB.color(0, colorR, colorG, colorB);
    }

    private enum PlaqueValue {
        ABSOLUTE,
        ABSOLUTE_WITH_MAX,
        RELATIVE
    }
}
