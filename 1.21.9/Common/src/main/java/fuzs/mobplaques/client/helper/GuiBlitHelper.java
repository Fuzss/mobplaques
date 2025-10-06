package fuzs.mobplaques.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.renderer.ModRenderType;
import fuzs.mobplaques.config.ClientConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;

public class GuiBlitHelper {

    public static void blitSprite(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int posX, int posY, float zOffset, TextureAtlasSprite sprite, int spriteWidth, int spriteHeight, int color) {
        innerBlit(poseStack,
                vertexConsumer,
                packedLight,
                posX,
                posX + spriteWidth,
                posY,
                posY + spriteHeight,
                zOffset,
                sprite.getU0(),
                sprite.getU1(),
                sprite.getV0(),
                sprite.getV1(),
                color);
    }

    public static void innerBlit(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int minX, int maxX, int minY, int maxY, float zOffset, float u1, float u2, float v1, float v2, int color) {
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.addVertex(matrix4f, minX, minY, zOffset).setColor(color).setUv(u1, v1).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, minX, maxY, zOffset).setColor(color).setUv(u1, v2).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, maxX, maxY, zOffset).setColor(color).setUv(u2, v2).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, maxX, minY, zOffset).setColor(color).setUv(u2, v1).setLight(packedLight);
    }

    public static void fill(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int minX, int minY, int maxX, int maxY, float zOffset, int color) {
        Matrix4f matrix4f = poseStack.last().pose();
        if (minX < maxX) {
            int tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minY < maxY) {
            int tmp = minY;
            minY = maxY;
            maxY = tmp;
        }
        RenderType renderType =
                MobPlaques.CONFIG.get(ClientConfig.class).behindWalls ? RenderType.textBackgroundSeeThrough() :
                        ModRenderType.textBackground();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(renderType);
        bufferBuilder.addVertex(matrix4f, minX, minY, zOffset).setColor(color).setLight(packedLight);
        bufferBuilder.addVertex(matrix4f, minX, maxY, zOffset).setColor(color).setLight(packedLight);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, zOffset).setColor(color).setLight(packedLight);
        bufferBuilder.addVertex(matrix4f, maxX, minY, zOffset).setColor(color).setLight(packedLight);
    }
}
