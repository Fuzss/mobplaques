package fuzs.mobplaques.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fuzs.mobplaques.client.gui.plaque.MobPlaqueRenderer;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    private static final String KEY_GENERAL_CATEGORY = "general";

    public ForgeConfigSpec.ConfigValue<Boolean> allowRendering;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Hide all plaques when mob has full health.")
    public boolean hideAtFullHealth = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show plaques for the entity picked by the crosshair only.")
    public boolean pickedEntity = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Custom scale for rendering plaques.")
    @Config.DoubleRange(min = 0.05, max = 2.0)
    public double plaqueScale = 0.5;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Amount of pixels a row of plaques may take up, when exceeding this value a new row will be started.")
    @Config.IntRange(min = 0)
    public int maxPlaqueRowWidth = 108;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Render mob plaques below the mob's name tag instead of above.")
    public boolean renderBelowNameTag = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show a black background box behind plaques. Disabled by default as it doesn't work with shaders.")
    public boolean plaqueBackground = true;
//    @Config(category = KEY_GENERAL_CATEGORY, description = "Always render plaques with full brightness to be most visible, ignoring local lighting conditions.")
//    public boolean renderFulbright = true;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Height offset from default position.")
    public int heightOffset = 0;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Distance to the mob at which plaques will still be visible. The distance is halved when the mob is crouching.")
    @Config.IntRange(min = 0)
    public int maxRenderDistance = 64;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show plaques from mobs obstructed by walls the player cannot see through.")
    public boolean behindWalls = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Dynamically increase plaque size the further away the camera is to simplify readability.")
    public boolean scaleWithDistance = true;
    @Config(category = KEY_GENERAL_CATEGORY, name = "mob_blacklist", description = {"Entities blacklisted from showing any plaques.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> mobBlacklistRaw = ConfigDataSet.toString(Registries.ENTITY_TYPE, EntityType.ARMOR_STAND);
    @Config(category = KEY_GENERAL_CATEGORY, name = "disallowed_mob_selectors", description = {"Selectors for choosing mobs to prevent rendering plaques for, takes priority over allowed list."})
    @Config.AllowedValues(values = {"ALL", "TAMED", "TAMED_ONLY_OWNER", "PLAYER", "MONSTER", "BOSS", "MOUNT"})
    List<String> disallowedMobSelectorsRaw = Lists.newArrayList();
    @Config(category = KEY_GENERAL_CATEGORY, name = "allowed_mob_selectors", description = {"Selectors for choosing mobs to render plaques for."})
    @Config.AllowedValues(values = {"ALL", "TAMED", "TAMED_ONLY_OWNER", "PLAYER", "MONSTER", "BOSS", "MOUNT"})
    List<String> allowedMobSelectorsRaw = Stream.of(MobPlaquesSelector.ALL).map(Enum::name).collect(Collectors.toList());

    public ConfigDataSet<EntityType<?>> mobBlacklist;
    public List<MobPlaquesSelector> disallowedMobSelectors;
    public List<MobPlaquesSelector> allowedMobSelectors;

    @Override
    public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        builder.push(KEY_GENERAL_CATEGORY);
        // we need this here to be able to set a value
        this.allowRendering = builder.comment("Are mob plaques enabled, toggleable in-game using the 'J' key by default.").define("allow_rendering", true);
        builder.pop();
        for (Map.Entry<ResourceLocation, MobPlaqueRenderer> entry : MobPlaqueHandler.PLAQUE_RENDERERS.entrySet()) {
            builder.push(entry.getKey().getPath());
            entry.getValue().setupConfig(builder, callback);
            builder.pop();
        }
    }

    @Override
    public void afterConfigReload() {
        this.mobBlacklist = ConfigDataSet.from(Registries.ENTITY_TYPE, this.mobBlacklistRaw);
        // manually process enum lists as night config keeps values as strings, making it hard to deal with as generic type suggests an enum value
        this.disallowedMobSelectors = this.disallowedMobSelectorsRaw.stream().map(MobPlaquesSelector::valueOf).collect(ImmutableList.toImmutableList());
        this.allowedMobSelectors = this.allowedMobSelectorsRaw.stream().map(MobPlaquesSelector::valueOf).collect(ImmutableList.toImmutableList());
    }

    public enum MobPlaquesSelector {
        ALL {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return true;
            }
        },
        TAMED {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return entity instanceof OwnableEntity tamableAnimal && tamableAnimal.getOwnerUUID() != null || entity instanceof AbstractHorse abstractHorse && abstractHorse.getOwnerUUID() != null;
            }
        },
        TAMED_ONLY_OWNER {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                UUID owner = Minecraft.getInstance().player.getUUID();
                return entity instanceof OwnableEntity tamableAnimal && owner.equals(tamableAnimal.getOwnerUUID()) || entity instanceof AbstractHorse abstractHorse && owner.equals(abstractHorse.getOwnerUUID());
            }
        },
        PLAYER {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return entity instanceof Player;
            }
        },
        MONSTER {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return entity instanceof Monster || !entity.getType().getCategory().isFriendly();
            }
        },
        BOSS {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return CommonAbstractions.INSTANCE.isBossMob(entity.getType());
            }
        },
        MOUNT {

            @Override
            public boolean canMobRenderPlaque(LivingEntity entity) {
                return entity instanceof Saddleable saddleable && saddleable.isSaddleable();
            }
        };

        public abstract boolean canMobRenderPlaque(LivingEntity entity);
    }
}
