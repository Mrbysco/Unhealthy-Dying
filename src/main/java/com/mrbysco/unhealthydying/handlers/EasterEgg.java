package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class EasterEgg {
	@SubscribeEvent
	public void killedEntityEvent(LivingDeathEvent event) {
		if (UnhealthyConfig.SERVER.regenHealth.get()) {
			List<? extends String> targets = UnhealthyConfig.SERVER.regenTargets.get();
			if (!targets.isEmpty()) {
				for (String target : targets) {
					if (event.getSource().getEntity() instanceof Player player && !(event.getSource().getEntity() instanceof FakePlayer)) {
						String[] targetInfo = target.split(",");
						if (targetInfo.length > 2) {
							ResourceLocation entityLocation = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
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
										if (new ResourceLocation(targetInfo[0]).equals(entityLocation)) {
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

	public void processKill(Player player, String target, int healthGained, int targetAmount) {
		float playerHealth = player.getMaxHealth();
		float maxRegained = (float) UnhealthyConfig.SERVER.maxRegained.get();

		if (playerHealth < maxRegained) {
			if (targetAmount == 1) {
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY -> UnhealthyHelper.setEveryonesHealth(player, healthGained);
					case SCOREBOARD_TEAM -> UnhealthyHelper.setScoreboardHealth(player, healthGained);
					default -> UnhealthyHelper.setHealth(player, healthGained);
				}
			} else {
				String customTag = Reference.MOD_PREFIX + target + ":" + targetAmount;
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY -> setEveryonesKillCount(player, customTag, healthGained, targetAmount);
					case SCOREBOARD_TEAM -> setScoreboardKillCount(player, customTag, targetAmount, healthGained);
					default -> setAmountData(player, customTag, targetAmount, healthGained);
				}
			}
		}
	}

	public static void setEveryonesKillCount(Player player, String customTag, int healthGained, int targetAmount) {
		var playerList = player.level().players();
		for (Player player1 : playerList) {
			if (player1.equals(player1))
				setAmountData(player1, customTag, healthGained, targetAmount);
			else
				setAmountData(player1, customTag, healthGained, targetAmount);
		}
	}

	public static void setScoreboardKillCount(Player player, String customTag, int healthGained, int targetAmount) {
		Level level = player.level();
		if (player.getTeam() != null) {
			Team team = player.getTeam();
			for (Player players : level.players()) {
				if (players.equals(player)) {
					setAmountData(player, customTag, healthGained, targetAmount);
				} else {
					if (players.isAlliedTo(team)) {
						setAmountData(players, customTag, healthGained, targetAmount);
					}
				}
			}
		} else {
			UnhealthyDying.LOGGER.error(player.getName() + " is not in a team");
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
	public static void setAmountData(Player player, String customTag, int targetAmount, int healthGained) {
		CompoundTag playerData = player.getPersistentData();

		if (playerData.contains(customTag)) {
			int currentAmount = playerData.getInt(customTag);
			if ((currentAmount + 1) >= targetAmount) {
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY -> UnhealthyHelper.setEveryonesHealth(player, healthGained);
					case SCOREBOARD_TEAM -> UnhealthyHelper.setScoreboardHealth(player, healthGained);
					default -> UnhealthyHelper.setHealth(player, healthGained);
				}
				playerData.remove(customTag);
			} else {
				playerData.putInt(customTag, currentAmount + 1);
			}
		} else {
			playerData.putInt(customTag, 1);
		}
	}
}