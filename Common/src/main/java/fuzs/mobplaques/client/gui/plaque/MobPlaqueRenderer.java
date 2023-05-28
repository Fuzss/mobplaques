package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import org.joml.Matrix4f;

public abstract class MobPlaqueRenderer {
    protected static final int PLAQUE_HEIGHT = 11;
    protected static final int BACKGROUND_BORDER_SIZE = 1;
    protected static final int ICON_SIZE = 9;
    protected static final int TEXT_ICON_GAP = 2;

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
        return -1;
    }

    public void render(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, boolean withBackground, Font font, LivingEntity entity) {
        poseStack.pushPose();
        this.tryRenderBackground(poseStack, posX, posY, withBackground, font, entity);
        this.renderComponent(poseStack, posX, posY, bufferSource, packedLight, font, entity);
        this.renderIcon(poseStack, posX, posY, font, entity);
        poseStack.popPose();
    }

    private void tryRenderBackground(PoseStack poseStack, int posX, int posY, boolean withBackground, Font font, LivingEntity entity) {
        if (withBackground) {
            int totalWidth = this.getWidth(font, entity);
            GuiComponent.fill(poseStack, posX - totalWidth / 2, posY, posX + totalWidth / 2, posY + this.getHeight(), Minecraft.getInstance().options.getBackgroundColor(0.25F));
            poseStack.translate(0.0F, 0.0F, 0.03F);
        }
    }

    private void renderComponent(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, LivingEntity entity) {
        Component component = this.getComponent(entity);
        int totalWidth = this.getWidth(font, entity);
        Matrix4f matrix4f = poseStack.last().pose();
        // light value stolen from Neat mod by Vazkii, probably shows up elsewhere in vanilla though
        // also kinda there to hide the fact that icons always render with full brightness, not sure what to do about that otherwise lol
        font.drawInBatch(component, posX - totalWidth / 2 + BACKGROUND_BORDER_SIZE, posY + BACKGROUND_BORDER_SIZE + 1, this.getColor(entity), false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
    }

    private void renderIcon(PoseStack poseStack, int posX, int posY, Font font, LivingEntity entity) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.getTextureSheet());
        RenderSystem.enableDepthTest();
        posX += this.getWidth(font, entity) / 2 - BACKGROUND_BORDER_SIZE - ICON_SIZE;
        posY += BACKGROUND_BORDER_SIZE;
        this.renderIconBackground(poseStack, posX, posY, entity);
        GuiComponent.blit(poseStack, posX, posY, this.getIconX(entity), this.getIconY(entity), ICON_SIZE, ICON_SIZE, 256, 256);
        RenderSystem.disableDepthTest();
    }

    protected void renderIconBackground(PoseStack poseStack, int posX, int posY, LivingEntity entity) {

    }

    protected abstract int getIconX(LivingEntity entity);

    protected abstract int getIconY(LivingEntity entity);

    protected ResourceLocation getTextureSheet() {
        return GuiComponent.GUI_ICONS_LOCATION;
    }

    public void setupConfig(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Allow for rendering this type of plaque.").define("allow_rendering", true), v -> this.allowRendering = v);
    }
}
