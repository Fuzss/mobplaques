package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.renderer.ModRenderType;
import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Matrix4f;

public abstract class MobPlaqueRenderer {
    protected static final int FULL_BRIGHTNESS_PACKED_LIGHT = 0XF000F0;
    protected static final int BACKGROUND_BORDER_SIZE = 1;

    protected boolean allowRendering;

    public boolean isRenderingAllowed(MobPlaquesRenderState renderState) {
        return this.allowRendering && this.getValue(renderState) > 0;
    }

    public int getWidth(MobPlaquesRenderState renderState) {
        return Minecraft.getInstance().font.width(this.getComponent(renderState)) + 2;
    }

    public int getHeight(MobPlaquesRenderState renderState) {
        return Minecraft.getInstance().font.lineHeight + 2;
    }

    public abstract int getValue(MobPlaquesRenderState renderState);

    protected MutableComponent getTextComponent(MobPlaquesRenderState renderState) {
        return Component.literal(this.getValue(renderState) + "x");
    }

    public final Component getComponent(MobPlaquesRenderState renderState) {
        return Component.empty()
                .append(this.getTextComponent(renderState).withColor(this.getColor(renderState)))
                .append(Component.object(new AtlasSprite(AtlasIds.GUI, this.getSprite(renderState))));
    }

    protected int getColor(MobPlaquesRenderState renderState) {
        return ARGB.transparent(-1);
    }

    public void submit(int posX, int posY, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, EntityRenderState entityRenderState, MobPlaquesRenderState renderState) {
        this.submitTextBackground(poseStack, posX, posY, submitNodeCollector, entityRenderState, renderState);
        this.submitTextComponent(poseStack, posX, posY, submitNodeCollector, entityRenderState, renderState);
    }

    private void submitTextBackground(PoseStack poseStack, int posX, int posY, SubmitNodeCollector submitNodeCollector, EntityRenderState entityRenderState, MobPlaquesRenderState renderState) {
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBackground) {
            int backgroundColor = Minecraft.getInstance().options.getBackgroundColor(0.25F);
            RenderType renderType =
                    MobPlaques.CONFIG.get(ClientConfig.class).behindWalls ? RenderType.textBackgroundSeeThrough() :
                            ModRenderType.textBackground();
            submitNodeCollector.submitCustomGeometry(poseStack,
                    renderType,
                    (PoseStack.Pose pose, VertexConsumer vertexConsumer) -> {
                        Matrix4f matrix4f = pose.pose();
                        int minX = posX - this.getWidth(renderState) / 2;
                        int minY = posY;
                        int maxX = posX + this.getWidth(renderState) / 2;
                        int maxY = posY + this.getHeight(renderState);
                        int packedLight = MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ?
                                FULL_BRIGHTNESS_PACKED_LIGHT : entityRenderState.lightCoords;
                        vertexConsumer.addVertex(matrix4f, minX, minY, 0.0F)
                                .setColor(backgroundColor)
                                .setLight(packedLight);
                        vertexConsumer.addVertex(matrix4f, minX, maxY, 0.0F)
                                .setColor(backgroundColor)
                                .setLight(packedLight);
                        vertexConsumer.addVertex(matrix4f, maxX, maxY, 0.0F)
                                .setColor(backgroundColor)
                                .setLight(packedLight);
                        vertexConsumer.addVertex(matrix4f, maxX, minY, 0.0F)
                                .setColor(backgroundColor)
                                .setLight(packedLight);
                    });
        }
    }

    private void submitTextComponent(PoseStack poseStack, int posX, int posY, SubmitNodeCollector submitNodeCollector, EntityRenderState entityRenderState, MobPlaquesRenderState renderState) {
        FormattedCharSequence formattedCharSequence = this.getComponent(renderState).getVisualOrderText();
        int x = posX - this.getWidth(renderState) / 2 + BACKGROUND_BORDER_SIZE;
        int y = posY + BACKGROUND_BORDER_SIZE + 1;
        int packedLight = MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? FULL_BRIGHTNESS_PACKED_LIGHT :
                entityRenderState.lightCoords;
        if (MobPlaques.CONFIG.get(ClientConfig.class).behindWalls) {
            // this does not respect the light level, use some very low alpha so it does not appear too bright
            submitNodeCollector.order(1)
                    .submitText(poseStack,
                            x,
                            y,
                            formattedCharSequence,
                            MobPlaques.CONFIG.get(ClientConfig.class).renderTextShadow,
                            Font.DisplayMode.SEE_THROUGH,
                            packedLight,
                            ARGB.color(MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 0x80 : 0x20, -1),
                            0,
                            entityRenderState.outlineColor);
        }

        submitNodeCollector.order(1)
                .submitText(poseStack,
                        x,
                        y,
                        formattedCharSequence,
                        MobPlaques.CONFIG.get(ClientConfig.class).renderTextShadow,
                        Font.DisplayMode.NORMAL,
                        packedLight,
                        -1,
                        0,
                        entityRenderState.outlineColor);
    }

    protected abstract ResourceLocation getSprite(MobPlaquesRenderState renderState);

    public void setupConfig(ModConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Allow for rendering this type of plaque.").define("allow_rendering", true),
                v -> this.allowRendering = v);
    }

    public void extractRenderState(LivingEntity livingEntity, MobPlaquesRenderState renderState, float partialTick) {
        // NO-OP
    }

    public abstract String getName();
}
