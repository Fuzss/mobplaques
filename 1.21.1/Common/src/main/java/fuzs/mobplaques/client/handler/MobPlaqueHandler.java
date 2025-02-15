package fuzs.mobplaques.client.handler;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.client.gui.plaque.*;
import fuzs.mobplaques.client.helper.EntityVisibilityHelper;
import fuzs.mobplaques.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MobPlaqueHandler {
    private static final int PLAQUE_HORIZONTAL_DISTANCE = 2;
    private static final int PLAQUE_VERTICAL_DISTANCE = 2;
    public static final Map<ResourceLocation, MobPlaqueRenderer> PLAQUE_RENDERERS = new LinkedHashMap<>();

    static {
        PLAQUE_RENDERERS.put(MobPlaques.id("health"), new HealthPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("air"), new AirPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("armor"), new ArmorPlaqueRenderer());
        PLAQUE_RENDERERS.put(MobPlaques.id("toughness"), new ToughnessPlaqueRenderer());
    }

    @SuppressWarnings("ConstantValue")
    public static EventResult onRenderNameTag(Entity entity, DefaultedValue<Component> content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick) {
        if (!MobPlaques.CONFIG.get(ClientConfig.class).allowRendering.get()) return EventResult.PASS;
        if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive() && MobPlaques.CONFIG.get(
                ClientConfig.class).isEntityAllowed(livingEntity)) {
            Minecraft minecraft = Minecraft.getInstance();
            EntityRenderDispatcher dispatcher = entityRenderer.entityRenderDispatcher;
            Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0,
                    entity.getViewYRot(partialTick)
            );
            // other mods might be rendering this mob without a level in some menu, so camera is null then
            if (vec3 != null && dispatcher.camera != null && dispatcher.camera.getEntity() instanceof LivingEntity) {
                if (EntityVisibilityHelper.isEntityVisible(minecraft.level, livingEntity, minecraft.player, partialTick,
                        dispatcher
                )) {
                    poseStack.pushPose();
                    poseStack.translate(vec3.x, vec3.y + 0.5, vec3.z);
                    poseStack.mulPose(dispatcher.cameraOrientation());
                    float plaqueScale = getPlaqueScale(livingEntity, dispatcher, minecraft.player);
                    // x and z are flipped as of 1.21
                    poseStack.scale(0.025F * plaqueScale, -0.025F * plaqueScale, -0.025F * plaqueScale);
                    int heightOffset = computeHeightOffset(livingEntity, content.get(), plaqueScale, minecraft.font);
                    renderAllPlaques(livingEntity, poseStack, multiBufferSource, packedLight, heightOffset,
                            minecraft.font
                    );
                    poseStack.popPose();
                }
            }
        }

        return EventResult.PASS;
    }

    private static int computeHeightOffset(LivingEntity livingEntity, Component nameComponent, float plaqueScale, Font font) {
        int heightOffset = "deadmau5".equals(nameComponent.getString()) ? -13 : -3;
        heightOffset -= (int) (MobPlaques.CONFIG.get(ClientConfig.class).offsetHeight * (0.5F / plaqueScale));
        if (MobPlaques.CONFIG.get(ClientConfig.class).renderBelowNameTag) {
            heightOffset += (int) (23 * (0.5F / plaqueScale));
        } else {
            int plaquesHeight = getPlaquesHeight(font, livingEntity);
            heightOffset -= (int) ((plaquesHeight + PLAQUE_VERTICAL_DISTANCE) * (0.5F / plaqueScale));
        }
        return heightOffset;
    }

    private static void renderAllPlaques(LivingEntity livingEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int heightOffset, Font font) {
        Iterator<MobPlaqueRenderer> iterator = PLAQUE_RENDERERS.values().iterator();
        List<MutableInt> widths = getPlaquesWidths(font, livingEntity);
        for (MutableInt width : widths) {
            int rowStart = -width.intValue() / 2;
            int maxRowHeight = 0;
            while (iterator.hasNext()) {
                MobPlaqueRenderer plaqueRenderer = iterator.next();
                if (!plaqueRenderer.wantsToRender(livingEntity)) continue;
                int plaqueWidth = plaqueRenderer.getWidth(font, livingEntity);
                plaqueRenderer.render(poseStack, multiBufferSource, packedLight, rowStart + plaqueWidth / 2,
                        heightOffset, font, livingEntity
                );
                maxRowHeight = Math.max(plaqueRenderer.getHeight(), maxRowHeight);
                plaqueWidth += PLAQUE_HORIZONTAL_DISTANCE;
                rowStart += plaqueWidth;
                if (width.addAndGet(-plaqueWidth) <= 0) break;
            }
            heightOffset += maxRowHeight + PLAQUE_VERTICAL_DISTANCE;
        }
    }

    private static float getPlaqueScale(LivingEntity targetEntity, EntityRenderDispatcher dispatcher, Player player) {
        float plaqueScale = (float) MobPlaques.CONFIG.get(ClientConfig.class).plaqueScale;
        if (MobPlaques.CONFIG.get(ClientConfig.class).scaleWithDistance) {
            double distanceSqr = dispatcher.distanceToSqr(targetEntity);
            double entityInteractionRange = player.entityInteractionRange();
            double scaleRatio = Mth.clamp((distanceSqr - Math.pow(entityInteractionRange / 2.0, 2.0)) /
                    (Math.pow(entityInteractionRange * 2.0, 2.0) / 2.0), 0.0, 2.0);
            plaqueScale *= (float) (1.0 + scaleRatio);
        }

        return plaqueScale;
    }

    private static List<MutableInt> getPlaquesWidths(Font font, LivingEntity entity) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        List<MutableInt> widths = Lists.newArrayList();
        int index = -1;
        for (MobPlaqueRenderer plaqueRenderer : PLAQUE_RENDERERS.values()) {
            if (plaqueRenderer.wantsToRender(entity)) {
                int plaqueWidth = plaqueRenderer.getWidth(font, entity);
                if (widths.isEmpty() || maxWidth < widths.get(index).intValue() + PLAQUE_HORIZONTAL_DISTANCE +
                        plaqueWidth) {
                    widths.add(new MutableInt(plaqueWidth));
                    index++;
                } else {
                    widths.get(index).add(PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth);
                }
            }
        }

        return widths;
    }

    private static int getPlaquesHeight(Font font, LivingEntity entity) {
        int maxWidth = MobPlaques.CONFIG.get(ClientConfig.class).maxPlaqueRowWidth;
        int currentWidth = -1;
        int currentMaxHeight = 0;
        int totalHeight = -1;
        for (MobPlaqueRenderer plaqueRenderer : PLAQUE_RENDERERS.values()) {
            if (plaqueRenderer.wantsToRender(entity)) {
                int plaqueWidth = plaqueRenderer.getWidth(font, entity);
                if (currentWidth == -1 || maxWidth < currentWidth + PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth) {
                    currentWidth = plaqueWidth;
                    currentMaxHeight = plaqueRenderer.getHeight();
                    totalHeight += plaqueRenderer.getHeight() + (totalHeight == -1 ? 0 : PLAQUE_VERTICAL_DISTANCE);
                } else {
                    currentWidth += PLAQUE_HORIZONTAL_DISTANCE + plaqueWidth;
                    if (plaqueRenderer.getHeight() > currentMaxHeight) {
                        totalHeight += plaqueRenderer.getHeight() - currentMaxHeight;
                        currentMaxHeight = plaqueRenderer.getHeight();
                    }
                }
            }
        }

        return totalHeight;
    }
}
