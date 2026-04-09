package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Constants;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.platform.Services;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class EasterEgg {

	public static void killedEntityEvent(LivingEntity livingEntity, DamageSource source) {
		if (UnhealthyConfig.SERVER.regenHealth.get() && !livingEntity.level().isClientSide()) {
			List<? extends String> targets = UnhealthyConfig.SERVER.regenTargets.get();
			if (!targets.isEmpty()) {
				for (String target : targets) {
					if (source.getEntity() instanceof Player player && Services.PLATFORM.isPlayer(player)) {
						String[] targetInfo = target.split(",");
						if (targetInfo.length > 2) {
							Identifier entityLocation = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType());
							int healthFromKill = NumberUtils.toInt(targetInfo[1], 0);
							int targetAmount = NumberUtils.toInt(targetInfo[2], 0);
							if (targetInfo[0].contains(":") && entityLocation != null) {
								String[] splitResource = targetInfo[0].split(":");
								if (targetInfo[0].equals("*:*")) {
									processKill(player, targetInfo[0], healthFromKill, targetAmount);
								} else {
									if (splitResource[0].equals("*") || splitResource[1].equals("*")) {
										if (splitResource[0].equals("*") && entityLocation.getPath().equals(splitResource[1])) {
											processKill(player, targetInfo[0], healthFromKill, targetAmount);
										} else if (splitResource[1].equals("*") && entityLocation.getNamespace().equals(splitResource[0])) {
											processKill(player, targetInfo[0], healthFromKill, targetAmount);
										}
									} else {
										Identifier targetEntity = Identifier.tryParse(targetInfo[0]);
										if (targetEntity != null && targetEntity.equals(entityLocation)) {
											processKill(player, targetInfo[0], healthFromKill, targetAmount);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private static void processKill(Player player, String target, int healthGained, int targetAmount) {
		float playerHealth = player.getMaxHealth();
		float maxRegained = UnhealthyConfig.SERVER.maxRegained.get();

		if (playerHealth < maxRegained) {
			if (targetAmount == 1) {
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY -> UnhealthyHelper.setEveryonesHealth(player, healthGained);
					case SCOREBOARD_TEAM -> UnhealthyHelper.setScoreboardHealth(player, healthGained);
					default -> UnhealthyHelper.setHealth(player, healthGained);
				}
			} else {
				String customTag = Constants.MOD_PREFIX + target + ":" + targetAmount;
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY -> setEveryonesKillCount(player, customTag, targetAmount, healthGained);
					case SCOREBOARD_TEAM -> setScoreboardKillCount(player, customTag, targetAmount, healthGained);
					default -> setAmountData(player, customTag, targetAmount, healthGained);
				}
			}
		}
	}

	private static void setEveryonesKillCount(Player player, String customTag, int targetAmount, int healthGained) {
		var playerList = player.level().players();
		for (Player player1 : playerList) {
			if (player1.equals(player))
				setAmountData(player, customTag, targetAmount, healthGained);
			else
				setAmountData(player1, customTag, targetAmount, healthGained);
		}
	}

	private static void setScoreboardKillCount(Player player, String customTag, int targetAmount, int healthGained) {
		Level level = player.level();
		if (player.getTeam() != null) {
			Team team = player.getTeam();
			for (Player players : level.players()) {
				if (players.equals(player)) {
					setAmountData(player, customTag, targetAmount, healthGained);
				} else {
					if (players.isAlliedTo(team)) {
						setAmountData(players, customTag, targetAmount, healthGained);
					}
				}
			}
		} else {
			Constants.LOGGER.error("{} is not in a team", player.getName());
		}
	}

	/**
	 * Sets the amount of kills for the player
	 *
	 * @param player       The player to set the data for
	 * @param customTag    The custom tag to use
	 * @param targetAmount The amount of kills needed
	 * @param healthGained The amount of health gained
	 */
	private static void setAmountData(Player player, String customTag, int targetAmount, int healthGained) {
		Services.PLATFORM.setAmountData(player, customTag, targetAmount, healthGained);
	}
}