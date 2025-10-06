package fuzs.mobplaques.client.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.gui.plaque.*;
import fuzs.mobplaques.client.helper.EntityVisibilityHelper;
import fuzs.mobplaques.client.renderer.entity.state.MobPlaquesRenderState;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.client.renderer.v1.RenderStateExtraData;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Iterator;
import java.util.List;

public class MobPlaqueHandler {
    private static final ContextKey<MobPlaquesRenderState> RENDER_STATE_PROPERTY = new ContextKey<>(MobPlaques.id(
            "render_state"));
    private static final int PLAQUE_HORIZONTAL_DISTANCE = 2;
    private static final int PLAQUE_VERTICAL_DISTANCE = 2;
    public static final List<MobPlaqueRenderer> PLAQUE_RENDERERS = List.of(new HealthPlaqueRenderer(),
            new AirPlaqueRenderer(),
            new ArmorPlaqueRenderer(),
            new ToughnessPlaqueRenderer());
    ;

    public static void onExtractRenderState(Entity entity, EntityRenderState entityRenderState, float partialTick) {
        if (entity instanceof LivingEntity livingEntity && canPlaqueRender(livingEntity, partialTick)) {
            MobPlaquesRenderState renderState = new MobPlaquesRenderState();
            RenderStateExtraData.set(entityRenderState, RENDER_STATE_PROPERTY, renderState);
            for (MobPlaqueRenderer mobPlaqueRenderer : PLAQUE_RENDERERS) {
                mobPlaqueRenderer.extractRenderState(livingEntity, renderState, partialTick);
            }

            if (entityRenderState.nameTag == null) {
                // we must force the name tag to render, as the name tag render event does not run unless this is set
                entityRenderState.nameTag = CommonComponents.EMPTY;
                entityRenderState.nameTagAttachment = entity.getAttachments()
                        .getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
            }
        }
    }

    private static boolean canPlaqueRender(LivingEntity livingEntity, float partialTick) {
        if (!MobPlaques.CONFIG.get(ClientConfig.class).allowRendering.get()) {
            return false;
        } else if (livingEntity.isAlive() && MobPlaques.CONFIG.get(ClientConfig.class).isEntityAllowed(livingEntity)) {
            Vec3 nameTagAttachment = livingEntity.getAttachments()
                    .getNullable(EntityAttachment.NAME_TAG, 0, livingEntity.getViewYRot(partialTick));
            if (nameTagAttachment != null) {
                return EntityVisibilityHelper.isEntityVisible(livingEntity,
                        partialTick,
                        MobPlaques.CONFIG.get(ClientConfig.class).pickedEntity);
            }
        }

        return false;
    }

    public static EventResult onRenderNameTag(EntityRenderer<?, ?> entityRenderer, EntityRenderState entityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (entityRenderState.nameTag != null && RenderStateExtraData.has(entityRenderState, RENDER_STATE_PROPERTY)) {
            poseStack.pushPose();
            Vec3 nameTagAttachment = entityRenderState.nameTagAttachment;
            if (nameTagAttachment != null) {
                poseStack.translate(nameTagAttachment.x, nameTagAttachment.y + 0.5, nameTagAttachment.z);
            }

            poseStack.mulPose(cameraRenderState.orientation);
            float plaqueScale = getPlaqueScale(entityRenderState.distanceToCameraSq);
            poseStack.scale(0.025F * plaqueScale, -0.025F * plaqueScale, 0.025F * plaqueScale);
            MobPlaquesRenderState renderState = RenderStateExtraData.get(entityRenderState, RENDER_STATE_PROPERTY);
            int heightOffset = computeHeightOffset(renderState, entityRenderState.nameTag, plaqueScale);
            renderAllPlaques(entityRenderState,
                    renderState,
                    poseStack,
                    submitNodeCollector,
                    cameraRenderState,
                    heightOffset);

            poseStack.popPose();
            if (entityRenderState.nameTag == CommonComponents.EMPTY) {
                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }

    private static int computeHeightOffset(MobPlaquesRenderState renderState, Component component, float plaqueScale) {
        int heightOffset = "deadmau5".equals(component.getString()) ? -13 : -3;
        heightOffset -= (int) (MobPlaques.CONFIG.get(ClientConfig.class).offsetHeight * (0.5F / plaqueScale));
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBelowNameTag) {
            heightOffset += (int) (23 * (0.5F / plaqueScale));
        } else {
            int plaquesHeight = getPlaquesHeight(renderState);
            heightOffset -= (int) ((plaquesHeight + PLAQUE_VERTICAL_DISTANCE) * (0.5F / plaqueScale));
        }

        return heightOffset;
    }

    private static void renderAllPlaques(EntityRenderState entityRenderState, MobPlaquesRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, int heightOffset) {
        Iterator<MobPlaqueRenderer> iterator = PLAQUE_RENDERERS.iterator();
        List<MutableInt> widths = getPlaquesWidths(renderState);
        for (MutableInt width : widths) {
            int rowStart = -width.intValue() / 2;
            int maxRowHeight = 0;
            while (iterator.hasNext()) {
                MobPlaqueRenderer mobPlaqueRenderer = iterator.next();
                if (!mobPlaqueRenderer.isRenderingAllowed(renderState)) {
                    continue;
                }

                int plaqueWidth = mobPlaqueRenderer.getWidth(renderState);
                mobPlaqueRenderer.submit(rowStart + plaqueWidth / 2,
                        heightOffset,
                        poseStack,
                        submitNodeCollector,
                        entityRenderState,
                        renderState);
                maxRowHeight = Math.max(mobPlaqueRenderer.getHeight(renderState), maxRowHeight);
                plaqueWidth += PLAQUE_HORIZONTAL_DISTANCE;
                rowStart += plaqueWidth;
                if (width.addAndGet(-plaqueWidth) <= 0) {
                    break;
                }
            }

            heightOffset += maxRowHeight + PLAQUE_VERTICAL_DISTANCE;
        }
    }

    private static float getPlaqueScale(double distanceToCameraSq) {
        float plaqueScale = (float) MobPlaques.CONFIG.get(ClientConfig.class).plaqueScale;
        if (MobPlaques.CONFIG.get(ClientConfig.class).scaleWithDistance) {
            double entityInteractionRange = Minecraft.getInstance().player.entityInteractionRange();
            double scaleRatio = Mth.clamp((distanceToCameraSq - Math.pow(entityInteractionRange / 2.0, 2.0)) / (
                    Math.pow(entityInteractionRange * 2.0, 2.0) / 2.0), 0.0, 2.0);
            plaqueScale *= (float) (1.0 + scaleRatio);
        }

        return plaqueScale;
    }

    private static List<MutableInt> getPlaquesWidths(MobPlaquesRenderState renderState) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        List<MutableInt> widths = Lists.newArrayList();
        int index = -1;
        for (MobPlaqueRenderer mobPlaqueRenderer : PLAQUE_RENDERERS) {
            if (mobPlaqueRenderer.isRenderingAllowed(renderState)) {
                int plaqueWidth = mobPlaqueRenderer.getWidth(renderState);
                if (widths.isEmpty()
                        || maxWidth < widths.get(index).intValue() + PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth) {
                    widths.add(new MutableInt(plaqueWidth));
                    index++;
                } else {
                    widths.get(index).add(PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth);
                }
            }
        }

        return widths;
    }

    private static int getPlaquesHeight(MobPlaquesRenderState renderState) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        int currentWidth = -1;
        int currentMaxHeight = 0;
        int totalHeight = -1;
        for (MobPlaqueRenderer mobPlaqueRenderer : PLAQUE_RENDERERS) {
            if (mobPlaqueRenderer.isRenderingAllowed(renderState)) {
                int plaqueWidth = mobPlaqueRenderer.getWidth(renderState);
                if (currentWidth == -1 || maxWidth < currentWidth + PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth) {
                    currentWidth = plaqueWidth;
                    currentMaxHeight = mobPlaqueRenderer.getHeight(renderState);
                    totalHeight +=
                            mobPlaqueRenderer.getHeight(renderState) + (totalHeight == -1 ? 0 : PLAQUE_VERTICAL_DISTANCE);
                } else {
                    currentWidth += PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth;
                    if (mobPlaqueRenderer.getHeight(renderState) > currentMaxHeight) {
                        totalHeight += mobPlaqueRenderer.getHeight(renderState) - currentMaxHeight;
                        currentMaxHeight = mobPlaqueRenderer.getHeight(renderState);
                    }
                }
            }
        }

        return totalHeight;
    }
}
