package com.Mrbysco.UnhealthyDying.handlers;

import com.Mrbysco.UnhealthyDying.Reference;
import com.Mrbysco.UnhealthyDying.UnhealthyDying;
import com.Mrbysco.UnhealthyDying.config.DyingConfigGen;
import com.Mrbysco.UnhealthyDying.util.UnhealthyHelper;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EasterEgg {
	@SubscribeEvent
	public void killedEntityEvent(LivingDeathEvent event) {
		if(DyingConfigGen.regen.regenHealth)
		{
			String[] targets = DyingConfigGen.regen.regenTargets;
			if(targets.length > 0)
			{
				for(int i = 0; i < targets.length; i++)
				{
					if (event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
						EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();

						String[] targetInfo = targets[i].split(",");
						if(targetInfo.length > 2)
						{
							ResourceLocation EntityLocation = EntityList.getKey(event.getEntityLiving());;
							ResourceLocation targetEntity = UnhealthyHelper.getEntityLocation(targetInfo[0]);
							int healthFromKill = Integer.valueOf(targetInfo[1]);
							int targetAmount = Integer.valueOf(targetInfo[2]);

							if(targetInfo[0].equals("*:*")) {
								{
									ProcessKill(player, targetEntity, healthFromKill, targetAmount);
								}
							}
							else
							{
								if(targetEntity.getResourcePath().equals("*"))
								{
									if(EntityLocation.getResourceDomain().equals(targetEntity.getResourceDomain()))
									{
										ProcessKill(player, targetEntity, healthFromKill, targetAmount);
									}
								}
								else
								{
									if(isMatchingName(EntityLocation, targetEntity))
									{
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
	
	public void ProcessKill(EntityPlayer player, ResourceLocation target, int healthGained, int targetAmount)
	{
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
		
	    int playerHealth = (int)player.getMaxHealth();
	    int maxRegained = DyingConfigGen.regen.maxRegenned;
	    
	    if(playerHealth < maxRegained)
	    {
		    if(!data.hasKey(Reference.REDUCED_HEALTH_TAG))
			{
				if(!player.world.isRemote)
				{
					if(!data.hasKey(Reference.REDUCED_HEALTH_TAG)) 
					{
						data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)playerHealth);
						playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);

						UnhealthyHelper.setHealth(player, playerHealth, false, -1);
					}
				}
			}
		    else
		    {
		    	if(targetAmount == 1)
		    	{
		    		switch (DyingConfigGen.general.HealthSetting) {
					case EVERYBODY:
						UnhealthyHelper.setEveryonesHealth(player, true, healthGained);
						break;
					case SEPERATE:
						UnhealthyHelper.SetThatHealth(player, true, healthGained);
						break;
					case SCOREBOARD_TEAM:
						UnhealthyHelper.setScoreboardHealth(player, true, healthGained);
						break;
					case FTB_TEAMS:
						UnhealthyHelper.teamHealth(player, true, healthGained);
						break;
					default:
						UnhealthyHelper.SetThatHealth(player, true, healthGained);
						break;
					}
		    	}
		    	else
		    	{
			    	String customTag = Reference.MOD_PREFIX + target.toString() + ":" + targetAmount;
			    	switch (DyingConfigGen.general.HealthSetting) {
					case EVERYBODY:
						setEveryonesKillCount(player, customTag, healthGained, targetAmount);
						break;
					case SEPERATE:
						setAmountData(player, customTag, targetAmount, healthGained);
						break;
					case SCOREBOARD_TEAM:
						setScoreboardKillCount(player, customTag, targetAmount, healthGained);
						break;
					case FTB_TEAMS:
						teamKillCount(player, customTag, targetAmount, healthGained);
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
        if (originalEntity != null)
        {
            return originalEntity.equals(targetEntity);
        }
        else
        {
        	return false;
        }
    }
	
	@Optional.Method(modid = "ftblib")
	public static void teamKillCount(EntityPlayer player, String customTag, int healthGained, int targetAmount)
	{
		World world = player.world;
		String team = com.feed_the_beast.ftblib.lib.data.FTBLibAPI.getTeam(player.getUniqueID());
		if(!team.isEmpty())
		{
			for(EntityPlayer players : world.playerEntities)
			{
				if(players.equals(player))
					setAmountData(player, customTag, healthGained, targetAmount);
				else
				{
					if(com.feed_the_beast.ftblib.lib.data.FTBLibAPI.isPlayerInTeam(player.getOfflineUUID(players.getName()), team))
					{
						setAmountData(players, customTag, healthGained, targetAmount);
					}
				}
			}
		}
	}
	
	public static void setEveryonesKillCount(EntityPlayer player, String customTag, int healthGained, int targetAmount)
	{
		for(EntityPlayer players : player.world.playerEntities)
		{
			if(players.equals(player))
				setAmountData(player, customTag, healthGained, targetAmount);
			else
				setAmountData(players, customTag, healthGained, targetAmount);
		}
	}
	
	public static void setScoreboardKillCount(EntityPlayer player, String customTag, int healthGained, int targetAmount)
	{
		World world = player.world;
		if(player.getTeam() != null)
		{
			Team team = player.getTeam();
			for(EntityPlayer players : world.playerEntities)
			{
				if(players.equals(player))
					setAmountData(player, customTag, healthGained, targetAmount);
				else
				{
					if(players.isOnScoreboardTeam(team))
					{
						setAmountData(players, customTag, healthGained, targetAmount);
					}
				}
			}
		}
		else
		{
			UnhealthyDying.logger.error(player.getName() + " is not in a team");
		}
	}
	
	public static void setAmountData(EntityPlayer player, String customTag, int targetAmount, int healthGained)
	{
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

		if(data.hasKey(customTag))
    	{
    		int currentAmount = data.getInteger(customTag);
    		if(currentAmount + 1 >= targetAmount)
    		{
		    	switch (DyingConfigGen.general.HealthSetting) {
				case EVERYBODY:
					UnhealthyHelper.setEveryonesHealth(player, true, healthGained);
					break;
				case SEPERATE:
					UnhealthyHelper.SetThatHealth(player, true, healthGained);
					break;
				case SCOREBOARD_TEAM:
					UnhealthyHelper.setScoreboardHealth(player, true, healthGained);
					break;
				case FTB_TEAMS:
					UnhealthyHelper.teamHealth(player, true, healthGained);
					break;
				default:
					UnhealthyHelper.SetThatHealth(player, true, healthGained);
					break;
				}
		    	data.removeTag(customTag);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
		    	
    		}
    		else
    		{
    			data.setInteger(customTag, currentAmount + 1);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
    		}
    	}
    	else
    	{
    		data.setInteger(customTag, 1);
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
    	}
	}
}