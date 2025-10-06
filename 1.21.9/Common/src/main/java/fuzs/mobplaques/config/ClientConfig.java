package fuzs.mobplaques.config;

import fuzs.mobplaques.client.gui.plaque.MobPlaqueRenderer;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;

public class ClientConfig implements ConfigCore {
    private static final String KEY_GENERAL_CATEGORY = "general";

    public ModConfigSpec.ConfigValue<Boolean> allowRendering;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Hide all plaques when mob has full health.")
    public boolean hideAtFullHealth = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show plaques for the entity picked by the crosshair only.")
    public boolean pickedEntity = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = {
            "The raytrace range for finding a picked entity.",
            "Setting this to -1 will make it use the player entity interaction range, which is 3 in survival."
    })
    @Config.IntRange(min = -1, max = 128)
    public int pickedEntityInteractionRange = -1;
    @Config(category = KEY_GENERAL_CATEGORY, description = {
            "Coyote time in seconds after which a no longer picked entity will still show the plaques.",
            "Set to -1 to keep the old entity until a new one is picked by the crosshair."
    })
    @Config.IntRange(min = -1)
    public int pickedEntityDelay = 2;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Custom scale for rendering plaques.")
    @Config.DoubleRange(min = 0.05, max = 2.0)
    public double plaqueScale = 0.5;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Amount of pixels a row of plaques may take up, when exceeding this value a new row will be started.")
    @Config.IntRange(min = 0)
    public int maxPlaqueRowWidth = 108;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Render mob plaques below the mob's name tag instead of above.")
    public boolean renderBelowNameTag = false;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Show a black background box behind plaques. Disabled by default as it doesn't work with shaders.")
    public boolean renderBackground = true;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Always render plaques with full brightness to be most visible, ignoring local lighting conditions.")
    public boolean fullBrightness = true;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Height offset from default position.")
    public int offsetHeight = 0;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Distance to the mob at which plaques will still be visible. The distance is halved when the mob is crouching.")
    @Config.IntRange(min = 0)
    public int maxRenderDistance = 96;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Show plaques from mobs obstructed by walls the player cannot see through, similar to the nameplates of other players.")
    public boolean behindWalls = true;
    @Config(category = KEY_GENERAL_CATEGORY,
            description = "Dynamically increase plaque size the further away the camera is to simplify readability.")
    public boolean scaleWithDistance = true;
    @Config(category = KEY_GENERAL_CATEGORY,
            name = "no_plaque_mobs",
            description = {"Entities blacklisted from showing any plaques.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> noPlaqueMobsRaw = KeyedValueProvider.asString(Registries.ENTITY_TYPE, EntityType.ARMOR_STAND);
    @Config(category = KEY_GENERAL_CATEGORY, description = "Selectors for choosing mobs to render plaques for.")
    public final MobSelectorsConfig mobSelectors = new MobSelectorsConfig();

    public ConfigDataSet<EntityType<?>> noPlaqueMobs;

    @Override
    public void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
        this.allowRendering = builder.comment(
                        "Are mob plaques enabled, toggleable in-game via the dedicated keybinding.")
                .define("allow_rendering", true);
        for (Map.Entry<ResourceLocation, MobPlaqueRenderer> entry : MobPlaqueHandler.PLAQUE_RENDERERS.entrySet()) {
            builder.push(entry.getKey().getPath());
            entry.getValue().setupConfig(builder, callback);
            builder.pop();
        }
    }

    @Override
    public void afterConfigReload() {
        this.noPlaqueMobs = ConfigDataSet.from(Registries.ENTITY_TYPE, this.noPlaqueMobsRaw);
    }

    public boolean isEntityAllowed(LivingEntity livingEntity) {
        if (this.noPlaqueMobs.contains(livingEntity.getType())) {
            return false;
        } else if (this.hideAtFullHealth && livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
            return false;
        } else {
            for (MobSelector mobSelector : MobSelector.VALUES) {
                if (mobSelector.isSettingEnabled(this.mobSelectors) && mobSelector.appliesToEntity(livingEntity)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class MobSelectorsConfig implements ConfigCore {
        @Config(description = "Show plaques on all mobs.")
        public boolean allMobs = true;
        @Config(description = "Show plaques on tamed animals.")
        public boolean tamedAnimals = true;
        @Config(description = "Show plaques on tamed animals owned by you.")
        public boolean ownedAnimals = true;
        @Config(description = "Show plaques on players.")
        public boolean players = true;
        @Config(description = "Show plaques on monsters.")
        public boolean monsters = true;
        @Config(description = "Show plaques on bosses.")
        public boolean bosses = true;
        @Config(description = "Show plaques on mobs that can equipt a saddle.")
        public boolean mounts = true;
    }
}
