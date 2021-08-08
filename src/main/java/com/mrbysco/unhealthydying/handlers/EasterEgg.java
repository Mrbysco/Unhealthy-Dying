package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class EasterEgg {
	@SubscribeEvent
	public void killedEntityEvent(LivingDeathEvent event) {
		if(UnhealthyConfig.SERVER.regenHealth.get()) {
			List<? extends String> targets = UnhealthyConfig.SERVER.regenTargets.get();
			if(!targets.isEmpty()) {
				for (String target : targets) {
					if (event.getSource().getEntity() instanceof PlayerEntity && !(event.getSource().getEntity() instanceof FakePlayer)) {
						PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
						String[] targetInfo = target.split(",");
						if (targetInfo.length > 2) {
							ResourceLocation entityLocation = event.getEntityLiving().getType().getRegistryName();
							int healthFromKill = NumberUtils.toInt(targetInfo[1], 0);
							int targetAmount = NumberUtils.toInt(targetInfo[2], 0);
							if(targetInfo[0].contains(":") && entityLocation != null) {
								String[] splitResource = targetInfo[0].split(":");
								if (targetInfo[0].equals("*:*")) {
									processKill(player, targetInfo[0], healthFromKill, targetAmount);
								} else {
									if(splitResource[0].equals("*") || splitResource[1].equals("*")) {
										if(splitResource[0].equals("*") && entityLocation.getPath().equals(splitResource[1])) {
											processKill(player, targetInfo[0], healthFromKill, targetAmount);
										} else if(splitResource[1].equals("*") && entityLocation.getNamespace().equals(splitResource[0])) {
											processKill(player, targetInfo[0], healthFromKill, targetAmount);
										}
									} else {
										if(new ResourceLocation(targetInfo[0]).equals(entityLocation)) {
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
	
	public void processKill(PlayerEntity player, String target, int healthGained, int targetAmount) {
	    float playerHealth = player.getMaxHealth();
		float maxRegained = (float)UnhealthyConfig.SERVER.maxRegained.get();
	    
	    if(playerHealth < maxRegained) {
			if(targetAmount == 1) {
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY:
						UnhealthyHelper.setEveryonesHealth(player, healthGained);
						break;
					case SCOREBOARD_TEAM:
						UnhealthyHelper.setScoreboardHealth(player, healthGained);
						break;
					default:
						UnhealthyHelper.setHealth(player, healthGained);
						break;
				}
			} else {
				String customTag = Reference.MOD_PREFIX + target + ":" + targetAmount;
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY:
						setEveryonesKillCount(player, customTag, healthGained, targetAmount);
						break;
					case SCOREBOARD_TEAM:
						setScoreboardKillCount(player, customTag, targetAmount, healthGained);
						break;
					default:
						setAmountData(player, customTag, targetAmount, healthGained);
						break;
				}
			}
	    }
	}
	
	public static void setEveryonesKillCount(PlayerEntity player, String customTag, int healthGained, int targetAmount) {
		for(PlayerEntity players : player.level.players()) {
			if(players.equals(player))
				setAmountData(player, customTag, healthGained, targetAmount);
			else
				setAmountData(players, customTag, healthGained, targetAmount);
		}
	}
	
	public static void setScoreboardKillCount(PlayerEntity player, String customTag, int healthGained, int targetAmount) {
		World world = player.level;
		if(player.getTeam() != null) {
			Team team = player.getTeam();
			for(PlayerEntity players : world.players()) {
				if(players.equals(player)) {
					setAmountData(player, customTag, healthGained, targetAmount);
				} else {
					if(players.isAlliedTo(team)) {
						setAmountData(players, customTag, healthGained, targetAmount);
					}
				}
			}
		} else {
			UnhealthyDying.LOGGER.error(player.getName() + " is not in a team");
		}
	}
	
	public static void setAmountData(PlayerEntity player, String customTag, int targetAmount, int healthGained) {
		CompoundNBT playerData = player.getPersistentData();

		if(playerData.contains(customTag)) {
    		int currentAmount = playerData.getInt(customTag);
    		if((currentAmount + 1) >= targetAmount) {
		    	switch (UnhealthyConfig.SERVER.healthSetting.get()) {
				case EVERYBODY:
					UnhealthyHelper.setEveryonesHealth(player, healthGained);
					break;
				case SCOREBOARD_TEAM:
					UnhealthyHelper.setScoreboardHealth(player, healthGained);
					break;
				default:
					UnhealthyHelper.setHealth(player, healthGained);
					break;
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