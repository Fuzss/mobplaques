package fuzs.mobplaques.client.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.gui.plaque.*;
import fuzs.mobplaques.client.helper.EntityVisibilityHelper;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MobPlaqueHandler {
    private static final RenderPropertyKey<EventResult> RENDER_PLAQUE_PROPERTY = MobPlaqueRenderer.createKey("render_plaque");
    private static final int PLAQUE_HORIZONTAL_DISTANCE = 2;
    private static final int PLAQUE_VERTICAL_DISTANCE = 2;
    public static final Map<ResourceLocation, MobPlaqueRenderer> PLAQUE_RENDERERS = new LinkedHashMap<>();
    @Nullable
    private static EventResult renderPlaque;

    static {
        PLAQUE_RENDERERS.put(MobPlaques.id("health"), new HealthPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("air"), new AirPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("armor"), new ArmorPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("toughness"), new ToughnessPlaqueRenderer());
    }

    public static EventResult onAllowNameTag(Entity entity, EntityRenderState renderState, Component content, EntityRenderer<?, ?> entityRenderer, float partialTick) {
        if (MobPlaqueHandler.canPlaqueRender(entity, entityRenderer, partialTick)) {
            renderPlaque = shouldShowName(entity, entityRenderer) ? EventResult.PASS : EventResult.INTERRUPT;
            return EventResult.ALLOW;
        } else {
            return EventResult.PASS;
        }
    }

    public static void onExtractRenderState(Entity entity, EntityRenderState renderState, EntityRenderer<?, ?> entityRenderer, float partialTick) {
        if (renderPlaque != null) {
            RenderPropertyKey.setRenderProperty(renderState, RENDER_PLAQUE_PROPERTY, renderPlaque);
            renderPlaque = null;
            for (MobPlaqueRenderer mobPlaqueRenderer : PLAQUE_RENDERERS.values()) {
                mobPlaqueRenderer.extractRenderState((LivingEntity) entity, renderState, partialTick);
            }
        }
    }

    private static boolean canPlaqueRender(Entity entity, EntityRenderer<?, ?> entityRenderer, float partialTick) {
        if (!MobPlaques.CONFIG.get(ClientConfig.class).allowRendering.get()) {
            return false;
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive() &&
                MobPlaques.CONFIG.get(ClientConfig.class).isEntityAllowed(livingEntity)) {
            Minecraft minecraft = Minecraft.getInstance();
            Vec3 nameTagAttachment = entity.getAttachments()
                    .getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
            // other mods might be rendering this mob without a level in some menu, so camera is null then
            if (nameTagAttachment != null && entityRenderer.entityRenderDispatcher.camera != null) {
                if (EntityVisibilityHelper.isEntityVisible(minecraft.level, livingEntity, minecraft.player, partialTick, entityRenderer.entityRenderDispatcher)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static <T extends Entity> boolean shouldShowName(Entity entity, EntityRenderer<?, ?> entityRenderer) {
        double distanceToCameraSq = entityRenderer.entityRenderDispatcher.distanceToSqr(entity);
        return distanceToCameraSq < 4096.0 &&
                ((EntityRenderer<T, ?>) entityRenderer).shouldShowName((T) entity, distanceToCameraSq);
    }

    public static EventResult onRenderNameTag(EntityRenderState renderState, Component content, EntityRenderer<?, ?> entityRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {

        if (RenderPropertyKey.containsRenderProperty(renderState, RENDER_PLAQUE_PROPERTY)) {

            poseStack.pushPose();
            Vec3 nameTagAttachment = renderState.nameTagAttachment;
            if (nameTagAttachment != null) {
                poseStack.translate(nameTagAttachment.x, nameTagAttachment.y + 0.5, nameTagAttachment.z);
            }

            poseStack.mulPose(entityRenderer.entityRenderDispatcher.cameraOrientation());
            Minecraft minecraft = Minecraft.getInstance();
            float plaqueScale = getPlaqueScale(renderState, minecraft.player);
            // x and z are flipped as of 1.21
            poseStack.scale(0.025F * plaqueScale, -0.025F * plaqueScale, -0.025F * plaqueScale);
            int heightOffset = computeHeightOffset(renderState, content, plaqueScale, minecraft.font);
            renderAllPlaques(renderState, poseStack, bufferSource, packedLight, heightOffset, minecraft.font);
            poseStack.popPose();

            return RenderPropertyKey.getRenderProperty(renderState, RENDER_PLAQUE_PROPERTY);
        }

        return EventResult.PASS;
    }

    private static int computeHeightOffset(EntityRenderState renderState, Component nameComponent, float plaqueScale, Font font) {
        int heightOffset = "deadmau5".equals(nameComponent.getString()) ? -13 : -3;
        heightOffset -= (int) (MobPlaques.CONFIG.get(ClientConfig.class).offsetHeight * (0.5F / plaqueScale));
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBelowNameTag) {
            heightOffset += (int) (23 * (0.5F / plaqueScale));
        } else {
            int plaquesHeight = getPlaquesHeight(renderState, font);
            heightOffset -= (int) ((plaquesHeight + PLAQUE_VERTICAL_DISTANCE) * (0.5F / plaqueScale));
        }
        return heightOffset;
    }

    private static void renderAllPlaques(EntityRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int heightOffset, Font font) {
        Iterator<MobPlaqueRenderer> iterator = PLAQUE_RENDERERS.values().iterator();
        List<MutableInt> widths = getPlaquesWidths(renderState, font);
        for (MutableInt width : widths) {
            int rowStart = -width.intValue() / 2;
            int maxRowHeight = 0;
            while (iterator.hasNext()) {
                MobPlaqueRenderer plaqueRenderer = iterator.next();
                if (!plaqueRenderer.isRenderingAllowed(renderState)) continue;
                int plaqueWidth = plaqueRenderer.getWidth(renderState, font);
                plaqueRenderer.render(poseStack, multiBufferSource, packedLight,
                        rowStart + plaqueWidth / 2, heightOffset, font, renderState);
                maxRowHeight = Math.max(plaqueRenderer.getHeight(renderState, font), maxRowHeight);
                plaqueWidth += PLAQUE_HORIZONTAL_DISTANCE;
                rowStart += plaqueWidth;
                if (width.addAndGet(-plaqueWidth) <= 0) break;
            }
            heightOffset += maxRowHeight + PLAQUE_VERTICAL_DISTANCE;
        }
    }

    private static float getPlaqueScale(EntityRenderState renderState, Player player) {
        float plaqueScale = (float) MobPlaques.CONFIG.get(ClientConfig.class).plaqueScale;
        if (MobPlaques.CONFIG.get(ClientConfig.class).scaleWithDistance) {
            double distanceToCameraSq = renderState.distanceToCameraSq;
            double entityInteractionRange = player.entityInteractionRange();
            double scaleRatio = Mth.clamp((distanceToCameraSq - Math.pow(entityInteractionRange / 2.0, 2.0)) /
                    (Math.pow(entityInteractionRange * 2.0, 2.0) / 2.0), 0.0, 2.0);
            plaqueScale *= (float) (1.0 + scaleRatio);
        }

        return plaqueScale;
    }

    private static List<MutableInt> getPlaquesWidths(EntityRenderState renderState, Font font) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        List<MutableInt> widths = Lists.newArrayList();
        int index = -1;
        for (MobPlaqueRenderer plaqueRenderer : PLAQUE_RENDERERS.values()) {
            if (plaqueRenderer.isRenderingAllowed(renderState)) {
                int plaqueWidth = plaqueRenderer.getWidth(renderState, font);
                if (widths.isEmpty() ||
                        maxWidth < widths.get(index).intValue() + PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth) {
                    widths.add(new MutableInt(plaqueWidth));
                    index++;
                } else {
                    widths.get(index).add(PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth);
                }
            }
        }

        return widths;
    }

    private static int getPlaquesHeight(EntityRenderState renderState, Font font) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        int currentWidth = -1;
        int currentMaxHeight = 0;
        int totalHeight = -1;
        for (MobPlaqueRenderer plaqueRenderer : PLAQUE_RENDERERS.values()) {
            if (plaqueRenderer.isRenderingAllowed(renderState)) {
                int plaqueWidth = plaqueRenderer.getWidth(renderState, font);
                if (currentWidth == -1 || maxWidth < currentWidth + PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth) {
                    currentWidth = plaqueWidth;
                    currentMaxHeight = plaqueRenderer.getHeight(renderState, font);
                    totalHeight += plaqueRenderer.getHeight(renderState, font) +
                            (totalHeight == -1 ? 0 : PLAQUE_VERTICAL_DISTANCE);
                } else {
                    currentWidth += PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth;
                    if (plaqueRenderer.getHeight(renderState, font) > currentMaxHeight) {
                        totalHeight += plaqueRenderer.getHeight(renderState, font) - currentMaxHeight;
                        currentMaxHeight = plaqueRenderer.getHeight(renderState, font);
                    }
                }
            }
        }

        return totalHeight;
    }
}
