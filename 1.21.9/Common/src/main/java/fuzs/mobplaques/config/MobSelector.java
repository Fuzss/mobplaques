package fuzs.mobplaques.config;

import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public enum MobSelector {
    ALL {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.allMobs;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return true;
        }
    },
    TAMED {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.tamedAnimals;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() != null;
        }
    },
    TAMED_ONLY_OWNER {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.ownedAnimals;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerReference() != null
                    && ownableEntity.getOwnerReference().matches(Minecraft.getInstance().player);
        }
    },
    PLAYER {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.players;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity instanceof Player;
        }
    },
    MONSTER {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.monsters;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity instanceof Enemy || !livingEntity.getType().getCategory().isFriendly();
        }
    },
    BOSS {
        static final TagKey<EntityType<?>> BOSSES_ENTITY_TYPE_TAG = TagFactory.COMMON.registerEntityTypeTag("bosses");

        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.bosses;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity.getType().is(BOSSES_ENTITY_TYPE_TAG);
        }
    },
    MOUNT {
        @Override
        public boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config) {
            return config.mounts;
        }

        @Override
        public boolean appliesToEntity(LivingEntity livingEntity) {
            return livingEntity.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
        }
    };

    public static final MobSelector[] VALUES = values();

    public abstract boolean isSettingEnabled(ClientConfig.MobSelectorsConfig config);

    public abstract boolean appliesToEntity(LivingEntity livingEntity);
}
