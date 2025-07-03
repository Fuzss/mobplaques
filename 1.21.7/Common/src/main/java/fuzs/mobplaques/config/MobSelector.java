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
        public boolean isValid(LivingEntity entity) {
            return true;
        }
    },
    TAMED {
        @Override
        public boolean isValid(LivingEntity entity) {
            return entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() != null;
        }
    },
    TAMED_ONLY_OWNER {
        @Override
        public boolean isValid(LivingEntity entity) {
            return entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerReference() != null &&
                    ownableEntity.getOwnerReference().matches(Minecraft.getInstance().player);
        }
    },
    PLAYER {
        @Override
        public boolean isValid(LivingEntity entity) {
            return entity instanceof Player;
        }
    },
    MONSTER {
        @Override
        public boolean isValid(LivingEntity entity) {
            return entity instanceof Enemy || !entity.getType().getCategory().isFriendly();
        }
    },
    BOSS {
        static final TagKey<EntityType<?>> BOSSES_ENTITY_TYPE_TAG = TagFactory.COMMON.registerEntityTypeTag("bosses");

        @Override
        public boolean isValid(LivingEntity entity) {
            return entity.getType().is(BOSSES_ENTITY_TYPE_TAG);
        }
    },
    MOUNT {
        @Override
        public boolean isValid(LivingEntity entity) {
            return entity.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
        }
    };

    public abstract boolean isValid(LivingEntity entity);
}
