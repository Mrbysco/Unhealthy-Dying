package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

public class UnhealthyHelper {

	public static void initializeModifier(Player player, double modifier) {
		if (!player.level().isClientSide) {
			AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
			if (attributeInstance != null && attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID) == null)
				attributeInstance.addPermanentModifier(getModifier(modifier));
		}
	}

	public static void changeModifier(Player player, double modifierValue) {
		if (!player.level().isClientSide) {
			AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
			AttributeModifier modifier = getModifier(modifierValue);
			if (attributeInstance != null) {

				if (attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID) != null) {
					attributeInstance.removePermanentModifier(Reference.HEALTH_MODIFIER_ID);
				}

				HealthUtil.sendHealthMessage(player, (int) (attributeInstance.getBaseValue() + modifier.amount()), (int) modifierValue);
				attributeInstance.addPermanentModifier(modifier);
			}
			player.setHealth(player.getHealth());
		}
	}

	public static AttributeModifier getModifier(double modifier) {
		return new AttributeModifier(Reference.HEALTH_MODIFIER_ID, "UnhealthyHealthModifier", modifier, Operation.ADD_VALUE);
	}

	@Nullable
	public static ModifierWorldData getSavedData(Player player) {
		return !player.level().isClientSide ? ModifierWorldData.get(player.getServer().getLevel(Level.OVERWORLD)) : null;
	}

	public static void setEveryonesHealth(Player player, int changeModifier) {
		setEveryonesHealth(player, changeModifier, true);
	}

	public static void setEveryonesHealth(Player player, int changeModifier, boolean recalculate) {
		ModifierWorldData worldData = getSavedData(player);
		if (worldData != null) {
			int savedModifier = recalculate ? worldData.getEverybodyModifier() : changeModifier;
			if (recalculate) {
				savedModifier += changeModifier;
				savedModifier = (int) safetyCheck(player, savedModifier);
			}

			worldData.setEverybodyModifier(savedModifier);
			worldData.setDirty();
			var playerList = player.level().players();
			for (Player player1 : playerList) {
				changeModifier(player1, savedModifier);
			}
		}
	}

	public static void setScoreboardHealth(Player player, int changeModifier) {
		setScoreboardHealth(player, changeModifier, true);
	}

	public static void setScoreboardHealth(Player player, int changeModifier, boolean recalculate) {
		if (player.getTeam() != null) {
			Team team = player.getTeam();
			ModifierWorldData worldData = getSavedData(player);
			if (worldData != null) {
				int savedModifier = recalculate ? worldData.getScoreboardTeamModifier(team.getName()) : changeModifier;
				if (recalculate) {
					savedModifier += changeModifier;
					savedModifier = (int) safetyCheck(player, savedModifier);
				}

				worldData.setScoreboardTeamModifier(team.getName(), savedModifier);
				worldData.setDirty();
				var playerList = player.level().players();
				for (Player player1 : playerList) {
					if (player1.getTeam() != null && player1.getTeam().getName().equals(team.getName())) {
						changeModifier(player1, savedModifier);
					}
				}
			}
		} else {
			UnhealthyDying.LOGGER.error(player.getName() + " is not in a team");
		}
	}

	public static void setHealth(Player player, int changeModifier) {
		setHealth(player, changeModifier, true);
	}

	public static void setHealth(Player player, int changeModifier, boolean recalculate) {
		ModifierWorldData worldData = getSavedData(player);
		if (worldData != null) {
			int savedModifier = recalculate ? worldData.getPlayerModifier(player.getGameProfile().getId()) : changeModifier;
			if (recalculate) {
				savedModifier += changeModifier;
				savedModifier = (int) safetyCheck(player, savedModifier);
			}

			worldData.setPlayerModifier(player.getGameProfile().getId(), savedModifier);
			worldData.setDirty();
			changeModifier(player, savedModifier);
		}
	}

	public static void syncHealth(Player player) {
		ModifierWorldData worldData = getSavedData(player);
		if (worldData != null) {
			switch (UnhealthyConfig.SERVER.healthSetting.get()) {
				case EVERYBODY:
					UnhealthyHelper.setEveryonesHealth(player, worldData.getEverybodyModifier(), false);
					break;
				case SCOREBOARD_TEAM:
					if (player.getTeam() != null) {
						UnhealthyHelper.setScoreboardHealth(player, worldData.getScoreboardTeamModifier(player.getTeam().getName()), false);
					}
					break;
				default:
					UnhealthyHelper.setHealth(player, worldData.getPlayerModifier(player.getGameProfile().getId()), false);
					break;
			}
		}
	}

	public static double getModifierForAmount(Player player, double healthWanted) {
		AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
		if (attributeInstance != null) {
			double health = attributeInstance.getBaseValue();
			double modifierRequired = healthWanted - health;

			if (UnhealthyConfig.SERVER.regenHealth.get() && healthWanted > (double) UnhealthyConfig.SERVER.maxRegained.get())
				modifierRequired = health - (double) UnhealthyConfig.SERVER.maxRegained.get();

			if (healthWanted < (double) UnhealthyConfig.SERVER.minimumHealth.get())
				modifierRequired = (double) UnhealthyConfig.SERVER.minimumHealth.get() - health;

			return modifierRequired;
		}
		//This should never be reached
		UnhealthyDying.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
		return 0.0D;
	}

	public static double safetyCheck(Player player, double modifierValue) {
		AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
		if (attributeInstance != null) {
			AttributeModifier currentModifier = attributeInstance.getModifier(Reference.HEALTH_MODIFIER_ID);
			double baseHealth = attributeInstance.getBaseValue();
			double health = baseHealth;
			if (currentModifier != null)
				health += currentModifier.amount();

			double modifiedHealth = baseHealth + modifierValue;
			double usedModifier = modifierValue;

			if (UnhealthyConfig.SERVER.regenHealth.get() && modifiedHealth > (double) UnhealthyConfig.SERVER.maxRegained.get())
				usedModifier = modifiedHealth - (double) UnhealthyConfig.SERVER.maxRegained.get();

			if (modifiedHealth < (double) UnhealthyConfig.SERVER.minimumHealth.get())
				usedModifier = (double) UnhealthyConfig.SERVER.minimumHealth.get() - health;

			return usedModifier;
		}
		//This should never be reached
		UnhealthyDying.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
		return 0.0D;
	}
}
