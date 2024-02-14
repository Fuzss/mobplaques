package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.helper.GuiBlitHelper;
import fuzs.mobplaques.client.renderer.ModRenderType;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import org.joml.Matrix4f;

public abstract class MobPlaqueRenderer {
    protected static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    protected static final int PLAQUE_HEIGHT = 11;
    protected static final int BACKGROUND_BORDER_SIZE = 1;
    protected static final int ICON_SIZE = 9;
    protected static final int TEXT_ICON_GAP = 2;
    /**
     * Light value stolen from Neat mod by Vazkii, probably shows up elsewhere in vanilla though.
     */
    protected static final int FULL_BRIGHT_PACKED_LIGHT = 0xF000F0;

    protected boolean allowRendering;

    public boolean wantsToRender(LivingEntity entity) {
        return this.allowRendering && !this.hideAtFullHealth(entity) && this.getValue(entity) > 0;
    }

    protected boolean hideAtFullHealth(LivingEntity entity) {
        return MobPlaques.CONFIG.get(ClientConfig.class).hideAtFullHealth && !this.belowMaxHealth(entity);
    }

    private boolean belowMaxHealth(LivingEntity entity) {
        double value = Math.ceil(entity.getHealth());
        double maxValue = Math.ceil(entity.getMaxHealth());
        return value < maxValue;
    }

    public int getWidth(Font font, LivingEntity entity) {
        return font.width(this.getComponent(entity)) + TEXT_ICON_GAP + ICON_SIZE + BACKGROUND_BORDER_SIZE * 2;
    }

    public int getHeight() {
        return PLAQUE_HEIGHT;
    }

    public abstract int getValue(LivingEntity entity);

    protected Component getComponent(LivingEntity entity) {
        return Component.literal(this.getValue(entity) + "x");
    }

    protected int getColor(LivingEntity entity) {
        return 0xFFFFFF;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, Font font, LivingEntity entity) {
        poseStack.pushPose();
        this.renderBackground(poseStack, posX, posY, bufferSource, packedLight, font, entity);
        this.renderComponent(poseStack, posX, posY, bufferSource, packedLight, font, entity);
        this.renderIcon(poseStack, bufferSource, packedLight, posX, posY, font, entity);
        poseStack.popPose();
    }

    private void renderBackground(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, LivingEntity entity) {
        if (MobPlaques.CONFIG.get(ClientConfig.class).plaqueBackground) {
            ClientConfig.FullBrightnessRendering fullBrightness = MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness;
            if (fullBrightness != ClientConfig.FullBrightnessRendering.NEVER) packedLight = FULL_BRIGHT_PACKED_LIGHT;
            int totalWidth = this.getWidth(font, entity);
            int backgroundColor = Minecraft.getInstance().options.getBackgroundColor(0.25F);
            GuiBlitHelper.fill(poseStack, bufferSource, packedLight, posX - totalWidth / 2, posY, posX + totalWidth / 2, posY + this.getHeight(), 0.03F, backgroundColor);
        }
    }

    private void renderComponent(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, LivingEntity entity) {
        Component component = this.getComponent(entity);
        int totalWidth = this.getWidth(font, entity);
        Matrix4f matrix4f = poseStack.last().pose();
        boolean behindWalls = MobPlaques.CONFIG.get(ClientConfig.class).behindWalls;
        ClientConfig.FullBrightnessRendering fullBrightness = MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness;
        if (fullBrightness != ClientConfig.FullBrightnessRendering.NEVER) packedLight = FULL_BRIGHT_PACKED_LIGHT;
        font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE, posY + BACKGROUND_BORDER_SIZE + 1, 0x20 << 24 | this.getColor(entity), false, matrix4f, bufferSource, behindWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE, posY + BACKGROUND_BORDER_SIZE + 1, 0xFF << 24 | this.getColor(entity), false, matrix4f, bufferSource, behindWalls && fullBrightness == ClientConfig.FullBrightnessRendering.ALWAYS ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, Font font, LivingEntity entity) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        posX += this.getWidth(font, entity) / 2 - BACKGROUND_BORDER_SIZE - ICON_SIZE;
        posY += BACKGROUND_BORDER_SIZE;
        this.renderIconBackground(poseStack, bufferSource, packedLight, posX, posY, entity);
        this.innerRenderIcon(poseStack, bufferSource, packedLight, posX, posY, 0.0F, this.getIconX(entity), this.getIconY(entity));
    }

    protected void innerRenderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, float zOffset, int textureX, int textureY) {
        boolean behindWalls = MobPlaques.CONFIG.get(ClientConfig.class).behindWalls;
        VertexConsumer vertexConsumer = bufferSource.getBuffer((behindWalls ? ModRenderType.ICON_SEE_THROUGH : ModRenderType.ICON).apply(this.getTextureSheet()));
        GuiBlitHelper.blit(poseStack, vertexConsumer, FULL_BRIGHT_PACKED_LIGHT, posX, posY, zOffset, textureX, textureY, ICON_SIZE, ICON_SIZE, 256, 256, 0x20FFFFFF);
        ClientConfig.FullBrightnessRendering fullBrightness = MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness;
        vertexConsumer = bufferSource.getBuffer((behindWalls && fullBrightness == ClientConfig.FullBrightnessRendering.ALWAYS ? ModRenderType.ICON_SEE_THROUGH : ModRenderType.ICON).apply(this.getTextureSheet()));
        GuiBlitHelper.blit(poseStack, vertexConsumer, fullBrightness != ClientConfig.FullBrightnessRendering.NEVER ? FULL_BRIGHT_PACKED_LIGHT : packedLight, posX, posY, zOffset, textureX, textureY, ICON_SIZE, ICON_SIZE, 256, 256, -1);
    }

    protected void renderIconBackground(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, LivingEntity entity) {
        // NO-OP
    }
    
    protected abstract int getIconX(LivingEntity entity);

    protected abstract int getIconY(LivingEntity entity);

    protected ResourceLocation getTextureSheet() {
        return GUI_ICONS_LOCATION;
    }

    public void setupConfig(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Allow for rendering this type of plaque.").define("allow_rendering", true), v -> this.allowRendering = v);
    }
}
