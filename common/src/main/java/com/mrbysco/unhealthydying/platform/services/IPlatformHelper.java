package com.mrbysco.unhealthydying.platform.services;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface IPlatformHelper {

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