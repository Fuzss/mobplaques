package fuzs.mobplaques.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.config.ClientConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class GuiBlitHelper {

    public static void blit(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int posX, int posY, float zOffset, float textureX, float textureY, int sizeX, int sizeY, int imageWidth, int imageHeight, int color) {
        blit(poseStack, vertexConsumer, packedLight, posX, posY, sizeX, sizeY, zOffset, textureX, textureY, sizeX, sizeY, imageWidth, imageHeight, color);
    }

    public static void blit(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int posX, int posY, int width, int height, float zOffset, float textureX, float textureY, int sizeX, int sizeY, int imageWidth, int imageHeight, int color) {
        innerBlit(poseStack, vertexConsumer, packedLight, posX, posX + width, posY, posY + height, zOffset, textureX, textureY, sizeX, sizeY, imageWidth, imageHeight, color);
    }

    public static void innerBlit(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int minX, int maxX, int minY, int maxY, float zOffset, float textureX, float textureY, int sizeX, int sizeY, int imageWidth, int imageHeight, int color) {
        innerBlit(poseStack, vertexConsumer, packedLight, minX, maxX, minY, maxY, zOffset, (textureX + 0.0F) / (float) imageWidth, (textureX + (float) sizeX) / (float) imageWidth, (textureY + 0.0F) / (float) imageHeight, (textureY + (float) sizeY) / (float) imageHeight, color);
    }

    public static void innerBlit(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int minX, int maxX, int minY, int maxY, float zOffset, float u1, float u2, float v1, float v2, int color) {
        Matrix4f matrix4f = poseStack.last().pose();
        float a = FastColor.ABGR32.alpha(color) / 255.0F;
        float r = FastColor.ABGR32.red(color) / 255.0F;
        float g = FastColor.ABGR32.green(color) / 255.0F;
        float b = FastColor.ABGR32.blue(color) / 255.0F;
        vertexConsumer.vertex(matrix4f, minX, minY, zOffset).color(r, g, b, a).uv(u1, v1).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, minX, maxY, zOffset).color(r, g, b, a).uv(u1, v2).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, maxY, zOffset).color(r, g, b, a).uv(u2, v2).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, minY, zOffset).color(r, g, b, a).uv(u2, v1).uv2(packedLight).endVertex();
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

        float a = FastColor.ABGR32.alpha(color) / 255.0F;
        float r = FastColor.ABGR32.red(color) / 255.0F;
        float g = FastColor.ABGR32.green(color) / 255.0F;
        float b = FastColor.ABGR32.blue(color) / 255.0F;
        RenderType renderType = MobPlaques.CONFIG.get(ClientConfig.class).behindWalls ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(renderType);
        bufferBuilder.vertex(matrix4f, minX, minY, zOffset).color(r, g, b, a).uv2(packedLight).endVertex();
        bufferBuilder.vertex(matrix4f, minX, maxY, zOffset).color(r, g, b, a).uv2(packedLight).endVertex();
        bufferBuilder.vertex(matrix4f, maxX, maxY, zOffset).color(r, g, b, a).uv2(packedLight).endVertex();
        bufferBuilder.vertex(matrix4f, maxX, minY, zOffset).color(r, g, b, a).uv2(packedLight).endVertex();
    }
}
