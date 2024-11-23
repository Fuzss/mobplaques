package fuzs.mobplaques.client.gui.plaque;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.helper.GuiBlitHelper;
import fuzs.mobplaques.client.renderer.ModRenderType;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Matrix4f;

public abstract class MobPlaqueRenderer {
    protected static final int PLAQUE_HEIGHT = 11;
    protected static final int BACKGROUND_BORDER_SIZE = 1;
    protected static final int ICON_SIZE = 9;
    protected static final int TEXT_ICON_GAP = 2;

    protected boolean allowRendering;

    public boolean isRenderingAllowed(EntityRenderState renderState) {
        return this.allowRendering && this.getValue(renderState) > 0;
    }

    public int getWidth(EntityRenderState renderState, Font font) {
        return font.width(this.getComponent(renderState)) + TEXT_ICON_GAP + ICON_SIZE + BACKGROUND_BORDER_SIZE * 2;
    }

    public int getHeight(EntityRenderState renderState, Font font) {
        return PLAQUE_HEIGHT;
    }

    public abstract int getValue(EntityRenderState renderState);

    protected Component getComponent(EntityRenderState renderState) {
        return Component.literal(this.getValue(renderState) + "x");
    }

    protected int getColor(EntityRenderState renderState) {
        return 0xFFFFFF;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, Font font, EntityRenderState renderState) {
        poseStack.pushPose();
        this.renderBackground(poseStack, posX, posY, bufferSource, packedLight, font, renderState);
        this.renderComponent(poseStack, posX, posY, bufferSource, packedLight, font, renderState);
        this.renderIcon(poseStack, bufferSource, packedLight, posX, posY, font, renderState);
        poseStack.popPose();
    }

    private void renderBackground(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, EntityRenderState renderState) {
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBackground) {
            int totalWidth = this.getWidth(renderState, font);
            int backgroundColor = Minecraft.getInstance().options.getBackgroundColor(0.25F);
            GuiBlitHelper.fill(poseStack, bufferSource,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight,
                    posX - totalWidth / 2, posY, posX + totalWidth / 2, posY + this.getHeight(renderState, font), 0.03F, backgroundColor
            );
        }
    }

    private void renderComponent(PoseStack poseStack, int posX, int posY, MultiBufferSource bufferSource, int packedLight, Font font, EntityRenderState renderState) {
        Component component = this.getComponent(renderState);
        int totalWidth = this.getWidth(renderState, font);
        Matrix4f matrix4f = poseStack.last().pose();
        if (MobPlaques.CONFIG.get(ClientConfig.class).behindWalls) {
            font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE,
                    posY + BACKGROUND_BORDER_SIZE + 1, ARGB.color(0x20, this.getColor(renderState)), false,
                    matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, 0,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight
            );
        }
        font.drawInBatch(component, posX - totalWidth / 2.0F + BACKGROUND_BORDER_SIZE,
                posY + BACKGROUND_BORDER_SIZE + 1, ARGB.color(0xFF, this.getColor(renderState)), false, matrix4f,
                bufferSource, Font.DisplayMode.NORMAL, 0,
                MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight
        );
    }

    private void renderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, Font font, EntityRenderState renderState) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        posX += this.getWidth(renderState, font) / 2 - BACKGROUND_BORDER_SIZE - ICON_SIZE;
        posY += BACKGROUND_BORDER_SIZE;
        this.renderIconBackground(poseStack, bufferSource, packedLight, posX, posY, renderState);
        this.innerRenderIcon(poseStack, bufferSource, packedLight, posX, posY, 0.0F, this.getSprite(renderState));
    }

    protected void innerRenderIcon(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, float zOffset, ResourceLocation resourceLocation) {
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(resourceLocation);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                ModRenderType.ICON_SEE_THROUGH.apply(textureAtlasSprite.atlasLocation()));
        if (MobPlaques.CONFIG.get(ClientConfig.class).behindWalls) {
            GuiBlitHelper.blitSprite(poseStack, vertexConsumer,
                    MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight, posX, posY,
                    zOffset, textureAtlasSprite, ICON_SIZE, ICON_SIZE, ARGB.color(0x20, -1)
            );
        }
        vertexConsumer = bufferSource.getBuffer(ModRenderType.ICON.apply(textureAtlasSprite.atlasLocation()));
        GuiBlitHelper.blitSprite(poseStack, vertexConsumer,
                MobPlaques.CONFIG.get(ClientConfig.class).fullBrightness ? 15728880 : packedLight, posX, posY, zOffset,
                textureAtlasSprite, ICON_SIZE, ICON_SIZE, -1
        );
    }

    protected void renderIconBackground(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int posX, int posY, EntityRenderState renderState) {
        // NO-OP
    }

    protected abstract ResourceLocation getSprite(EntityRenderState renderState);

    public void setupConfig(ModConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Allow for rendering this type of plaque.").define("allow_rendering", true),
                v -> this.allowRendering = v
        );
    }

    public void extractRenderState(LivingEntity livingEntity, EntityRenderState renderState, float partialTick) {
        // NO-OP
    }

    public static <T> RenderPropertyKey<T> createKey(String path) {
        return new RenderPropertyKey<>(MobPlaques.id(path));
    }
}
