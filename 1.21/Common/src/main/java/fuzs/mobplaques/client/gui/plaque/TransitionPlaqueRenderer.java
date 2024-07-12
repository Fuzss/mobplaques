package fuzs.mobplaques.client.gui.plaque;

import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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

    public abstract int getMaxValue(LivingEntity entity);

    protected boolean belowMaxValue(LivingEntity entity) {
        return this.getValue(entity) < this.getMaxValue(entity);
    }

    @Override
    protected Component getComponent(LivingEntity entity) {
        return switch (this.plaqueValue) {
            case ABSOLUTE -> super.getComponent(entity);
            case ABSOLUTE_WITH_MAX -> Component.literal(this.getValue(entity) + "/" + this.getMaxValue(entity));
            case RELATIVE -> Component.literal((int) (this.getValuePercentage(entity) * 100.0F) + "%");
        };
    }

    @Override
    protected int getColor(LivingEntity entity) {
        if (!this.shiftColors) {
            return super.getColor(entity);
        } else {
            float transition = this.getValuePercentage(entity);
            return packTransitionedColor(this.defaultHighColor, this.defaultLowColor, transition);
        }
    }

    private float getValuePercentage(LivingEntity entity) {
        return Mth.clamp(this.getValue(entity) / (float) this.getMaxValue(entity), 0.0F, 1.0F);
    }

    @Override
    public void setupConfig(ModConfigSpec.Builder builder, ValueCallback callback) {
        super.setupConfig(builder, callback);
        callback.accept(builder.comment("Transition text colors depending on current percentage.").define("shift_colors", false), v -> this.shiftColors = v);
        callback.accept(builder.comment("Show current amount either as percentage or as absolute value.").defineEnum("plaque_value", PlaqueValue.RELATIVE), v -> this.plaqueValue = v);
    }

    public static int packTransitionedColor(int startColor, int endColor, float transition) {
        int startR = FastColor.ARGB32.red(startColor);
        int startG = FastColor.ARGB32.green(startColor);
        int startB = FastColor.ARGB32.blue(startColor);
        int endR = FastColor.ARGB32.red(endColor);
        int endG = FastColor.ARGB32.green(endColor);
        int endB = FastColor.ARGB32.blue(endColor);
        int colorR = endR + (int) ((startR - endR) * transition);
        int colorG = endG + (int) ((startG - endG) * transition);
        int colorB = endB + (int) ((startB - endB) * transition);
        return FastColor.ARGB32.color(0, colorR, colorG, colorB);
    }

    private enum PlaqueValue {
        ABSOLUTE, ABSOLUTE_WITH_MAX, RELATIVE
    }
}
