package fuzs.mobplaques.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fuzs.mobplaques.client.core.ClientModServices;
import fuzs.mobplaques.client.gui.plaque.MobPlaqueRenderer;
import fuzs.mobplaques.client.handler.MobPlaqueHandler;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ValueCallback;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.core.AbstractConfigBuilder;
import fuzs.puzzleslib.config.core.AbstractConfigValue;
import fuzs.puzzleslib.config.serialization.ConfigDataSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    private static final String KEY_GENERAL_CATEGORY = "general";

    public AbstractConfigValue<Boolean> enableRendering;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show plaques for the entity picked by the crosshair.")
    public boolean pickedEntity = false;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Custom scale for rendering plaques.")
    @Config.DoubleRange(min = 0.05, max = 2.0)
    public double plaqueScale = 0.5;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Amount of pixels a row of plaques may take up, when exceeding this value a new row will be begun.")
    @Config.IntRange(min = 0)
    public int maxPlaqueRowWidth = 32;
    @Config(category = KEY_GENERAL_CATEGORY, description = "Show a black background box behind plaques.")
    public boolean plaqueBackground = true;
    @Config(category = KEY_GENERAL_CATEGORY, description = {"Entities blacklisted from showing any plaques.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> mobBlacklistRaw = ConfigDataSet.toString(Registry.ENTITY_TYPE_REGISTRY, EntityType.ARMOR_STAND);
    @Config(name = "disallowed_mob_selectors", description = {"Selectors for choosing mobs to prevent rendering plaques for, takes priority over allowed list."})
    @Config.AllowedValues(values = {"ALL", "TAMED", "TAMED_ONLY_OWNER", "PLAYER", "MONSTER", "BOSS", "MOUNT"})
    List<String> disallowedMobSelectorsRaw = Lists.newArrayList();
    @Config(name = "allowed_mob_selectors", description = {"Selectors for choosing mobs to render plaques for."})
    @Config.AllowedValues(values = {"ALL", "TAMED", "TAMED_ONLY_OWNER", "PLAYER", "MONSTER", "BOSS", "MOUNT"})
    List<String> allowedMobSelectorsRaw = Stream.of(MobPlaquesSelector.ALL).map(Enum::name).collect(Collectors.toList());

    public ConfigDataSet<EntityType<?>> mobBlacklist;
    public List<MobPlaquesSelector> disallowedMobSelectors;
    public List<MobPlaquesSelector> allowedMobSelectors;

    @Override
    public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
        builder.push(KEY_GENERAL_CATEGORY);
        this.enableRendering = builder.comment("Are mob plaques enabled, toggleable in-game using the 'P' key by default.").define("enable_rendering", true);
        builder.pop();
        for (Map.Entry<ResourceLocation, MobPlaqueRenderer> entry : MobPlaqueHandler.PLAQUE_RENDERERS.entrySet()) {
            builder.push(entry.getKey().getPath());
            entry.getValue().setupConfig(builder, callback);
            builder.pop();
        }
    }

    @Override
    public void afterConfigReload() {
        this.mobBlacklist = ConfigDataSet.of(Registry.ENTITY_TYPE_REGISTRY, this.mobBlacklistRaw);
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
                return ClientModServices.ABSTRACTIONS.isBossMob(entity);
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
