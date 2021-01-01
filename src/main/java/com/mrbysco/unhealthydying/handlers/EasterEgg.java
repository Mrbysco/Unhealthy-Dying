package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.HealthUtil;
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
					if (event.getSource().getTrueSource() instanceof PlayerEntity && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
						PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();

						String[] targetInfo = target.split(",");
						if (targetInfo.length > 2) {
							ResourceLocation EntityLocation = event.getEntityLiving().getType().getRegistryName();
							if (event.getEntityLiving() instanceof PlayerEntity) {
								EntityLocation = new ResourceLocation("minecraft", "player");
							}

							ResourceLocation targetEntity = UnhealthyHelper.getEntityLocation(targetInfo[0]);

							int healthFromKill = NumberUtils.toInt(targetInfo[1], 0);
							int targetAmount = NumberUtils.toInt(targetInfo[2], 0);

							if (targetInfo[0].equals("*:*")) {
								ProcessKill(player, targetEntity, healthFromKill, targetAmount);
							} else {
								if (targetEntity != null && targetEntity.getNamespace().equals("*")) {
									if (EntityLocation != null && EntityLocation.getNamespace().equals(targetEntity.getNamespace())) {
										ProcessKill(player, targetEntity, healthFromKill, targetAmount);
									}
								} else {
									if (isMatchingName(EntityLocation, targetEntity)) {
										ProcessKill(player, targetEntity, healthFromKill, targetAmount);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void ProcessKill(PlayerEntity player, ResourceLocation target, int healthGained, int targetAmount) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);
		
	    int playerHealth = (int)player.getMaxHealth();
	    int maxRegained = UnhealthyConfig.SERVER.maxRegained.get();
	    
	    if(playerHealth < maxRegained) {
		    if(!data.contains(Reference.HEALTH_MODIFIER_TAG)) {
				if(!player.world.isRemote) {
					HealthUtil.setHealth(player, healthGained);
				}
			} else {
		    	if(targetAmount == 1) {
		    		switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY:
						UnhealthyHelper.setEveryonesHealth(player, healthGained);
						break;
					case SCOREBOARD_TEAM:
						UnhealthyHelper.setScoreboardHealth(player, healthGained);
						break;
					default:
						UnhealthyHelper.SetHealth(player, healthGained);
						break;
					}
		    	} else {
			    	String customTag = Reference.MOD_PREFIX + target.toString() + ":" + targetAmount;
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
	}
	
	public static boolean isMatchingName(ResourceLocation originalEntity, ResourceLocation targetEntity)
    {
        if (originalEntity != null) {
            return originalEntity.equals(targetEntity);
        } else {
        	return false;
        }
    }
	
	public static void setEveryonesKillCount(PlayerEntity player, String customTag, int healthGained, int targetAmount) {
		for(PlayerEntity players : player.world.getPlayers()) {
			if(players.equals(player))
				setAmountData(player, customTag, healthGained, targetAmount);
			else
				setAmountData(players, customTag, healthGained, targetAmount);
		}
	}
	
	public static void setScoreboardKillCount(PlayerEntity player, String customTag, int healthGained, int targetAmount) {
		World world = player.world;
		if(player.getTeam() != null) {
			Team team = player.getTeam();
			for(PlayerEntity players : world.getPlayers()) {
				if(players.equals(player)) {
					setAmountData(player, customTag, healthGained, targetAmount);
				} else {
					if(players.isOnScoreboardTeam(team)) {
						setAmountData(players, customTag, healthGained, targetAmount);
					}
				}
			}
		} else {
			UnhealthyDying.logger.error(player.getName() + " is not in a team");
		}
	}
	
	public static void setAmountData(PlayerEntity player, String customTag, int targetAmount, int healthGained) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);

		if(data.contains(customTag)) {
    		int currentAmount = data.getInt(customTag);
    		if((currentAmount + 1) >= targetAmount) {
		    	switch (UnhealthyConfig.SERVER.healthSetting.get()) {
				case EVERYBODY:
					UnhealthyHelper.setEveryonesHealth(player, healthGained);
					break;
				case SCOREBOARD_TEAM:
					UnhealthyHelper.setScoreboardHealth(player, healthGained);
					break;
				default:
					UnhealthyHelper.SetHealth(player, healthGained);
					break;
				}
		    	data.remove(customTag);
			} else {
    			data.putInt(customTag, currentAmount + 1);
			}
		} else {
    		data.putInt(customTag, 1);
		}
		playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
	}
}