package fuzs.mobplaques.client.handler;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PickEntityHandler {
    @Nullable
    private static UUID crosshairPickEntity;
    private static int pickDelay;

    @Nullable
    public static UUID getCrosshairPickEntity() {
        return crosshairPickEntity;
    }

    public static void onStartClientTick(Minecraft minecraft) {
        if (minecraft.level != null && !minecraft.isPaused()) {
            if (pickDelay > 0) {
                pickDelay--;
            }
        }
    }

    public static void onBeforeGameRender(Minecraft minecraft, GameRenderer gameRenderer, DeltaTracker deltaTracker) {
        if (!MobPlaques.CONFIG.get(ClientConfig.class).allowRendering.get()) return;
        pick(minecraft, gameRenderer, deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    /**
     * Copied from {@link GameRenderer#pick(float)}.
     */
    private static void pick(Minecraft minecraft, GameRenderer gameRenderer, float partialTick) {
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null && minecraft.level != null && minecraft.player != null) {
            Profiler.get().push("pick");
            double blockInteractionRange = minecraft.player.blockInteractionRange();
            double entityInteractionRange = minecraft.player.entityInteractionRange();
            int interactionRange = MobPlaques.CONFIG.get(ClientConfig.class).pickedEntityInteractionRange;
            if (interactionRange != -1) {
                blockInteractionRange = entityInteractionRange = interactionRange;
            }
            HitResult hitResult = pick(cameraEntity, blockInteractionRange, entityInteractionRange, partialTick);
            if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof EnderDragonPart enderDragonPart) {
                    entity = enderDragonPart.parentMob;
                }
                crosshairPickEntity = entity.getUUID();
                pickDelay = MobPlaques.CONFIG.get(ClientConfig.class).pickedEntityDelay * 20;
            } else if (pickDelay == 0) {
                crosshairPickEntity = null;
            }
            Profiler.get().pop();
        }
    }

    /**
     * Copied from {@link GameRenderer#pick(Entity, double, double, float)}.
     */
    private static HitResult pick(Entity entity, double blockInteractionRange, double entityInteractionRange, float partialTick) {
        double interactionRange = Math.max(blockInteractionRange, entityInteractionRange);
        double interactionRangeSqr = Mth.square(interactionRange);
        Vec3 eyePosition = entity.getEyePosition(partialTick);
        HitResult hitResult = pick(entity, interactionRange, partialTick, false);
        double distanceToHitResult = hitResult.getLocation().distanceToSqr(eyePosition);
        if (hitResult.getType() != HitResult.Type.MISS) {
            interactionRangeSqr = distanceToHitResult;
            interactionRange = Math.sqrt(distanceToHitResult);
        }

        Vec3 viewVector = entity.getViewVector(partialTick);
        Vec3 vec3 = eyePosition.add(viewVector.x * interactionRange,
                viewVector.y * interactionRange,
                viewVector.z * interactionRange);
        AABB aABB = entity.getBoundingBox().expandTowards(viewVector.scale(interactionRange)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity,
                eyePosition,
                vec3,
                aABB,
                entityX -> (entityX instanceof LivingEntity || entityX instanceof EnderDragonPart)
                        && !entityX.isSpectator() && entityX.isPickable(),
                interactionRangeSqr);
        return entityHitResult != null
                && entityHitResult.getLocation().distanceToSqr(eyePosition) < distanceToHitResult ?
                GameRenderer.filterHitResult(entityHitResult, eyePosition, entityInteractionRange) :
                GameRenderer.filterHitResult(hitResult, eyePosition, blockInteractionRange);
    }

    /**
     * Copied from {@link Entity#pick(double, float, boolean)}, with {@link ClipContext.Block} changed to
     * {@link ClipContext.Block#VISUAL}.
     */
    private static HitResult pick(Entity entity, double hitDistance, float partialTicks, boolean hitFluids) {
        Vec3 eyePosition = entity.getEyePosition(partialTicks);
        Vec3 viewVector = entity.getViewVector(partialTicks);
        Vec3 vec3 = eyePosition.add(viewVector.x * hitDistance, viewVector.y * hitDistance, viewVector.z * hitDistance);
        return entity.level()
                .clip(new ClipContext(eyePosition,
                        vec3,
                        ClipContext.Block.VISUAL,
                        hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                        entity));
    }
}
