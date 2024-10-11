package com.mrbysco.unhealthydying.platform.services;

import com.mrbysco.unhealthydying.config.EnumHealthSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IPlatformHelper {

	/**
	 * Get the minimum health configured.
	 *
	 * @return The minimum health configured
	 */
	int getMinimumHealth();

	/**
	 * Get the health per death configured.
	 *
	 * @return The health per death configured
	 */
	int getHealthPerDeath();

	/**
	 * Check if the reduced health message is enabled.
	 *
	 * @return True if the reduced health message is enabled, false otherwise
	 */
	boolean isReducedHealthMessageEnabled();

	/**
	 * Get the health setting configured.
	 *
	 * @return The health setting configured
	 */
	EnumHealthSetting getHealthSetting();

	/**
	 * Check if health regeneration is enabled.
	 *
	 * @return True if health regeneration is enabled, false otherwise
	 */
	boolean isRegenHealthEnabled();

	/**
	 * Get the maximum health that can be regained.
	 *
	 * @return The maximum health that can be regained
	 */
	int getMaxRegained();

	/**
	 * Check if the regeneration health message is enabled.
	 *
	 * @return True if the regeneration health message is enabled, false otherwise
	 */
	boolean isRegenHealthMessageEnabled();

	/**
	 * Get the list of regeneration targets.
	 *
	 * @return The list of regeneration targets
	 */
	List<? extends String> getRegenTargets();

	/**
	 * If the entity is a player.
	 *
	 * @return True if the entity is a player, false otherwise
	 */
	boolean isPlayer(Entity entity);

	/**
	 * Sets the amount of kills for the player
	 *
	 * @param player       The player to set the data for
	 * @param customTag    The custom tag to use
	 * @param targetAmount The amount of kills needed
	 * @param healthGained The amount of health gained
	 */
	void setAmountData(Player player, String customTag, int targetAmount, int healthGained);
}