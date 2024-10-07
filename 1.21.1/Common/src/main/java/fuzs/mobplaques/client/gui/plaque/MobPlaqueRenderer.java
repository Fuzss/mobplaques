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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Matrix4f;

public abstract class MobPlaqueRenderer {
    protected static final int PLAQUE_HEIGHT = 11;
    protected static final int BACKGROUND_BORDER_SIZE = 1;
    protected static final int ICON_SIZE = 9;
    protected static final int TEXT_ICON_GAP = 2;

    protected boolean allowRendering;

    public boolean wantsToRender(LivingEntity entity) {
        return this.allowRendering && this.getValue(entity) > 0;
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
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBackground) {
            int totalWidth = this.getWidth(font, entity);
            int backgroundColor = Minecraft.getInstance().options.getBackgroundColor(0.25F);
            GuiBlitHelper.fill(poseStack, bufferSource,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight,
                    posX - totalWidth / 2, posY, posX + totalWidth / 2, posY + this.getHeight(), 0.03F, backgroundColor
            );
        }
    }

    private void renderComponent(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, LivingEntity entity) {
        Component component = this.getComponent(entity);
        int totalWidth = this.getWidth(font, entity);
        Matrix4f matrix4f = poseStack.last().pose();
        if (MobPlaques.CONFIG.get(ClientConfig.class).behindWalls) {
            font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE,
                    posY + BACKGROUND_BORDER_SIZE + 1, FastColor.ARGB32.color(0x20, this.getColor(entity)), false,
                    matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, 0,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight
            );
        }
        font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE,
                posY + BACKGROUND_BORDER_SIZE + 1, FastColor.ARGB32.color(0xFF, this.getColor(entity)), false, matrix4f,
                bufferSource, Font.DisplayMode.NORMAL, 0,
                MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight
        );
    }

    private void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, Font font, LivingEntity entity) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        posX += this.getWidth(font, entity) / 2 - BACKGROUND_BORDER_SIZE - ICON_SIZE;
        posY += BACKGROUND_BORDER_SIZE;
        this.renderIconBackground(poseStack, bufferSource, packedLight, posX, posY, entity);
        this.innerRenderIcon(poseStack, bufferSource, packedLight, posX, posY, 0.0F, this.getSprite(entity));
    }

    protected void innerRenderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, float zOffset, ResourceLocation resourceLocation) {
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(resourceLocation);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                ModRenderType.ICON_SEE_THROUGH.apply(textureAtlasSprite.atlasLocation()));
        if (MobPlaques.CONFIG.get(ClientConfig.class).behindWalls) {
            GuiBlitHelper.blitSprite(poseStack, vertexConsumer,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight, posX, posY,
                    zOffset, textureAtlasSprite, ICON_SIZE, ICON_SIZE, FastColor.ARGB32.color(0x20, -1)
            );
        }
        vertexConsumer = bufferSource.getBuffer(ModRenderType.ICON.apply(textureAtlasSprite.atlasLocation()));
        GuiBlitHelper.blitSprite(poseStack, vertexConsumer,
                MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight, posX, posY, zOffset,
                textureAtlasSprite, ICON_SIZE, ICON_SIZE, -1
        );
    }

    protected void renderIconBackground(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, LivingEntity entity) {
        // NO-OP
    }

    protected abstract ResourceLocation getSprite(LivingEntity entity);

    public void setupConfig(ModConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Allow for rendering this type of plaque.").define("allow_rendering", true),
                v -> this.allowRendering = v
        );
    }
}
