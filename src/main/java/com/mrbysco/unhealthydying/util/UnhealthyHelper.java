package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;

import javax.annotation.Nullable;

public class UnhealthyHelper {

    public static void initializeModifier(PlayerEntity player, double modifier) {
        if(!player.world.isRemote) {
            ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
            if(attributeInstance != null && attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID) == null)
                attributeInstance.applyPersistentModifier(getModifier(modifier));
        }
    }

    public static void changeModifier(PlayerEntity player, double modifierValue) {
        if(!player.world.isRemote) {
            ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeModifier modifier = getModifier(modifierValue);
            if(attributeInstance != null) {
                if(attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID) != null) {
                    if(attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID).getAmount() != modifierValue)
                        HealthUtil.sendHealthMessage(player, (int)attributeInstance.getValue(), (int)modifierValue);

                    attributeInstance.removePersistentModifier(Reference.HEALTH_MODIFIER_ID);
                }

                attributeInstance.applyPersistentModifier(modifier);
            }
        }
    }

    public static AttributeModifier getModifier(double modifier) {
        return new AttributeModifier(Reference.HEALTH_MODIFIER_ID, "UnhealthyHealthModifier", modifier, Operation.ADDITION);
    }

    @Nullable
    public static ModifierWorldData getSavedData(PlayerEntity player) {
        return !player.world.isRemote ? ModifierWorldData.get(player.world) : null;
    }

    public static void setEveryonesHealth(PlayerEntity player, int changeModifier) {
        setEveryonesHealth(player, changeModifier, true);
    }

	public static void setEveryonesHealth(PlayerEntity player, int changeModifier, boolean recalculate) {
        ModifierWorldData worldData = getSavedData(player);
        if(worldData != null) {
            int savedModifier = recalculate ? worldData.getEverybodyModifier() : changeModifier;
            if(recalculate) {
                savedModifier += changeModifier;
                savedModifier = (int)safetyCheck(player, savedModifier);
            }

            worldData.setEverybodyModifier(savedModifier);
            worldData.markDirty();
            for(PlayerEntity players : player.world.getPlayers()) {
                changeModifier(players, savedModifier);
            }
        }
	}

    public static void setScoreboardHealth(PlayerEntity player, int changeModifier) {
        setScoreboardHealth(player, changeModifier, true);
    }

    public static void setScoreboardHealth(PlayerEntity player, int changeModifier, boolean recalculate) {
        if(player.getTeam() != null) {
            Team team = player.getTeam();
            ModifierWorldData worldData = getSavedData(player);
            if(worldData != null) {
                int savedModifier = recalculate ? worldData.getScoreboardTeamModifier(team.getName()) : changeModifier;
                if(recalculate) {
                    savedModifier += changeModifier;
                    savedModifier = (int)safetyCheck(player, savedModifier);
                }

                worldData.setScoreboardTeamModifier(team.getName(), savedModifier);
                worldData.markDirty();
                for(PlayerEntity players : player.world.getPlayers()) {
                    changeModifier(players, savedModifier);
                }
            }
        } else {
            UnhealthyDying.LOGGER.error(player.getName() + " is not in a team");
        }
    }

    public static void setHealth(PlayerEntity player, int changeModifier) {
        setHealth(player, changeModifier, true);
    }

    public static void setHealth(PlayerEntity player, int changeModifier, boolean recalculate) {
        ModifierWorldData worldData = getSavedData(player);
        if(worldData != null) {
            int savedModifier = recalculate ? worldData.getPlayerModifier(player.getGameProfile().getId()) : changeModifier;
            if(recalculate) {
                savedModifier += changeModifier;
                savedModifier = (int)safetyCheck(player, savedModifier);
            }

            worldData.setPlayerModifier(player.getGameProfile().getId(), savedModifier);
            worldData.markDirty();
            changeModifier(player, savedModifier);
        }
    }

    public static void syncHealth(PlayerEntity player) {
        ModifierWorldData worldData = getSavedData(player);
        if(worldData != null) {
            switch (UnhealthyConfig.SERVER.healthSetting.get()) {
                case EVERYBODY:
                    UnhealthyHelper.setEveryonesHealth(player, worldData.getEverybodyModifier(), false);
                    break;
                case SCOREBOARD_TEAM:
                    if(player.getTeam() != null) {
                        UnhealthyHelper.setScoreboardHealth(player, worldData.getScoreboardTeamModifier(player.getTeam().getName()), false);
                    }
                    break;
                default:
                    UnhealthyHelper.setHealth(player, worldData.getPlayerModifier(player.getGameProfile().getId()), false);
                    break;
            }
        }
    }

    public static double getModifierForAmount(PlayerEntity player, double healthWanted) {
        ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
        if(attributeInstance != null) {
            AttributeModifier currentModifier = attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID);
            double health = attributeInstance.getValue();
            if(currentModifier != null)
                health -= currentModifier.getAmount();

            double modifierRequired = healthWanted - health;

            if(UnhealthyConfig.SERVER.regenHealth.get() && healthWanted > (double)UnhealthyConfig.SERVER.maxRegained.get())
                modifierRequired = health - (double)UnhealthyConfig.SERVER.maxRegained.get();

            if(healthWanted < (double)UnhealthyConfig.SERVER.minimumHealth.get())
                modifierRequired = (double)UnhealthyConfig.SERVER.minimumHealth.get() - health;

            return modifierRequired;
        }
        //This should never be reached
        UnhealthyDying.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
        return 0.0D;
    }

	public static double safetyCheck(PlayerEntity player, double modifierValue) {
        ModifiableAttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
        if(attributeInstance != null) {
            AttributeModifier currentModifier = attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID);
            double health = attributeInstance.getValue();
            if(currentModifier != null)
                health -= currentModifier.getAmount();

            double modifiedHealth = health + modifierValue;
            double usedModifier = modifierValue;

            if(UnhealthyConfig.SERVER.regenHealth.get() && modifiedHealth > (double)UnhealthyConfig.SERVER.maxRegained.get())
                usedModifier = modifiedHealth - (double)UnhealthyConfig.SERVER.maxRegained.get();

            if(modifiedHealth < (double)UnhealthyConfig.SERVER.minimumHealth.get())
                usedModifier = (double)UnhealthyConfig.SERVER.minimumHealth.get() - health;

            return usedModifier;
        }
        //This should never be reached
        UnhealthyDying.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
        return 0.0D;
	}
}
