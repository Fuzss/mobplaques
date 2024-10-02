package fuzs.mobplaques.client.handler;

import fuzs.mobplaques.MobPlaques;
import fuzs.mobplaques.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class PickEntityHandler {
    private static WeakReference<@Nullable Entity> crosshairPickEntity = new WeakReference<>(null);
    private static int pickDelay;

    @Nullable
    public static Entity getCrosshairPickEntity() {
        return crosshairPickEntity.get();
    }

    public static void onBeforeGameRender(Minecraft minecraft, GameRenderer gameRenderer, DeltaTracker deltaTracker) {
        if (!MobPlaques.CONFIG.get(ClientConfig.class).pickedEntity) return;
        pick(minecraft, gameRenderer, deltaTracker.getGameTimeDeltaPartialTick(true));
    }

    /**
     * Mostly copied from {@link GameRenderer#pick(float)}.
     */
    private static void pick(Minecraft minecraft, GameRenderer gameRenderer, float partialTick) {
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null && minecraft.level != null && minecraft.player != null) {
            minecraft.getProfiler().push("pick");
            double blockInteractionRange = minecraft.player.blockInteractionRange();
            double entityInteractionRange = minecraft.player.entityInteractionRange();
            int interactionRange = MobPlaques.CONFIG.get(ClientConfig.class).pickedEntityInteractionRange;
            if (interactionRange != -1) {
                blockInteractionRange = entityInteractionRange = interactionRange;
            }
            HitResult hitResult = gameRenderer.pick(cameraEntity, blockInteractionRange, entityInteractionRange,
                    partialTick
            );
            if (hitResult instanceof EntityHitResult entityHitResult) {
                crosshairPickEntity = new WeakReference<>(entityHitResult.getEntity());
                pickDelay = MobPlaques.CONFIG.get(ClientConfig.class).pickedEntityDelay * 20;
            } else if (pickDelay == 0) {
                crosshairPickEntity = new WeakReference<>(null);
            }
            minecraft.getProfiler().pop();
        }
    }

    public static void onStartClientTick(Minecraft minecraft) {
        if (pickDelay > 0) pickDelay--;
    }
}
