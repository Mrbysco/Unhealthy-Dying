package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Constants;
import com.mrbysco.unhealthydying.data.ModifierWorldData;
import com.mrbysco.unhealthydying.platform.Services;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for the health modifier
 */
public class UnhealthyHelper {

	/**
	 * Initializes the modifier
	 *
	 * @param player   The player to initialize
	 * @param modifier The modifier value
	 */
	public static void initializeModifier(Player player, double modifier) {
		if (!player.level().isClientSide) {
			AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
			if (attributeInstance != null && attributeInstance.getModifier(Constants.HEALTH_MODIFIER_ID) == null)
				attributeInstance.addPermanentModifier(getModifier(modifier));
		}
	}

	/**
	 * Changes the modifier value
	 *
	 * @param player        The player to change
	 * @param modifierValue The amount to change the modifier with
	 */
	public static void changeModifier(Player player, double modifierValue) {
		if (!player.level().isClientSide) {
			AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
			AttributeModifier modifier = getModifier(modifierValue);
			if (attributeInstance != null) {
				if (attributeInstance.getModifier(Constants.HEALTH_MODIFIER_ID) != null) {
					attributeInstance.removeModifier(Constants.HEALTH_MODIFIER_ID);
				}

				HealthUtil.sendHealthMessage(player, (int) (attributeInstance.getBaseValue() + modifier.amount()), (int) modifierValue);
				attributeInstance.addPermanentModifier(modifier);
			}
			player.setHealth(player.getHealth());
		}
	}

	/**
	 * Gets the modifier
	 *
	 * @param modifier The modifier value
	 * @return The modifier
	 */
	public static AttributeModifier getModifier(double modifier) {
		return new AttributeModifier(Constants.HEALTH_MODIFIER_ID, modifier, Operation.ADD_VALUE);
	}

	/**
	 * Gets the saved data
	 *
	 * @param player The player who's world to get the data from
	 * @return The saved data
	 */
	@Nullable
	public static ModifierWorldData getSavedData(Player player) {
		return !player.level().isClientSide ? ModifierWorldData.get(player.getServer().getLevel(Level.OVERWORLD)) : null;
	}

	/**
	 * Recalculate and set the health of everyone
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 */
	public static void setEveryonesHealth(Player player, int changeModifier) {
		setEveryonesHealth(player, changeModifier, true);
	}

	/**
	 * Sets the health of everyone
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 * @param recalculate    If the health modifier should be recalculated
	 */
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

	/**
	 * Recalculate and set the health of the player based on the scoreboard team
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 */
	public static void setScoreboardHealth(Player player, int changeModifier) {
		setScoreboardHealth(player, changeModifier, true);
	}

	/**
	 * Sets the health of the player based on the scoreboard team
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 * @param recalculate    If the health modifier should be recalculated
	 */
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
			Constants.LOGGER.error("{} is not in a team", player.getName());
		}
	}

	/**
	 * Recalculate and set the health of the player
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 */
	public static void setHealth(Player player, int changeModifier) {
		setHealth(player, changeModifier, true);
	}

	/**
	 * Sets the health of the plater
	 *
	 * @param player         The player to change
	 * @param changeModifier The amount to change the modifier with
	 * @param recalculate    If the health modifier should be recalculated
	 */
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

	/**
	 * Syncs the health of the player with the saved data
	 *
	 * @param player The player to sync
	 */
	public static void syncHealth(Player player) {
		ModifierWorldData worldData = getSavedData(player);
		if (worldData != null) {
			switch (Services.PLATFORM.getHealthSetting()) {
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

	/**
	 * Gets the modifier value required to reach the wanted health
	 *
	 * @param player       The player to check
	 * @param healthWanted The health the player wants
	 * @return The modifier value required to reach the wanted health
	 */
	public static double getModifierForAmount(Player player, double healthWanted) {
		AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
		if (attributeInstance != null) {
			double health = attributeInstance.getValue();
			double modifierRequired = healthWanted - health;

			double maxRegained = Services.PLATFORM.getMaxRegained();
			if (Services.PLATFORM.isRegenHealthEnabled() && healthWanted > maxRegained)
				modifierRequired = health - maxRegained;

			double minimumHealth = Services.PLATFORM.getMinimumHealth();
			if (healthWanted < minimumHealth)
				modifierRequired = minimumHealth - health;

			return modifierRequired;
		}
		//This should never be reached
		Constants.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
		return 0.0D;
	}

	/**
	 * Checks if the modifier value is safe to apply
	 *
	 * @param player        The player to check
	 * @param modifierValue The modifier value to check
	 * @return The modifier value that is safe to apply
	 */
	public static double safetyCheck(Player player, double modifierValue) {
		AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
		if (attributeInstance != null) {
			AttributeModifier currentModifier = attributeInstance.getModifier(Constants.HEALTH_MODIFIER_ID);
			double baseHealth = attributeInstance.getBaseValue();
			double health = baseHealth;
			if (currentModifier != null)
				health += currentModifier.amount();

			double modifiedHealth = baseHealth + modifierValue;
			double usedModifier = modifierValue;

			double maxRegained = Services.PLATFORM.getMaxRegained();
			if (Services.PLATFORM.isRegenHealthEnabled() && modifiedHealth > maxRegained)
				usedModifier = modifiedHealth - maxRegained;

			double minimumHealth = Services.PLATFORM.getMinimumHealth();
			if (modifiedHealth < minimumHealth)
				usedModifier = minimumHealth - health;

			return usedModifier;
		}
		//This should never be reached
		Constants.LOGGER.error("Something went wrong. Somehow the player has no max_health attribute applied");
		return 0.0D;
	}
}
