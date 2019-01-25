package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.DyingConfigGen;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

public class UnhealthyHelper {
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(EntityPlayer entity, int maxHealth, boolean regained, int healthGained) {
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        if(regained)
        {
        	int entityHealth = (int)entity.getHealth() + healthGained;
            entity.setHealth(entityHealth);
        }
        else
        {
            entity.setHealth(maxHealth);
        }
	}
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(EntityPlayer entity, double maxHealth, boolean regained, int healthGained) {
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        if(regained)
        {
        	int entityHealth = (int)entity.getHealth() + healthGained;
            entity.setHealth(entityHealth);
        }
        else
        {
            entity.setHealth((int)maxHealth);
        }
	}

	/**
	 * Sets the players Max Health only.
	 */
	public static void setMaxHealth(EntityPlayer entity, int maxHealth) {
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        int entityHealth = (int)entity.getHealth();
        entity.setHealth(entityHealth);
	}
	
	/**
	 * Returns a proper ResourceLocation for the given String.
	 */
	public static ResourceLocation getEntityLocation(String name)
	{
		String[] splitResource = name.split(":");
		if (splitResource.length != 2)
			return null;
		else
			return new ResourceLocation(splitResource[0], splitResource[1]);
	}
	
	public static NBTTagCompound getTag(NBTTagCompound tag, String key) {
		if(tag == null || !tag.hasKey(key)) {
			return new NBTTagCompound();
		}
		return tag.getCompoundTag(key);
	}
	
	
	@Optional.Method(modid = "ftblib")
	public static void teamHealth(EntityPlayer player, boolean regained, int healthGained)
	{
		World world = player.world;
		String team = com.feed_the_beast.ftblib.lib.data.FTBLibAPI.getTeam(player.getUniqueID());
		if(!team.isEmpty())
		{
			for(EntityPlayer players : world.playerEntities)
			{
				if(players.equals(player))
					SetThatHealth(player, regained, healthGained);
				else
				{
					if(com.feed_the_beast.ftblib.lib.data.FTBLibAPI.isPlayerInTeam(player.getOfflineUUID(players.getName()), team))
					{
						SetThatMaxHealth(players, regained, healthGained);
					}
				}
			}
		}
	}
	
	public static void setEveryonesHealth(EntityPlayer player, boolean regained, int healthGained)
	{
		for(EntityPlayer players : player.world.playerEntities)
		{
			if(players.equals(player))
				SetThatHealth(player, regained, healthGained);
			else
				SetThatMaxHealth(players, regained, healthGained);
		}
	}
	
	public static void setScoreboardHealth(EntityPlayer player, boolean regained, int healthGained)
	{
		World world = player.world;
		if(player.getTeam() != null)
		{
			Team team = player.getTeam();
			for(EntityPlayer players : world.playerEntities)
			{
				if(players.equals(player))
					SetThatHealth(player, regained, healthGained);
				else
				{
					if(players.isOnScoreboardTeam(team))
					{
						SetThatMaxHealth(players, regained, healthGained);
					}
				}
			}
		}
		else
		{
			UnhealthyDying.logger.error(player.getName() + " is not in a team");
		}
	}
	
	public static void SetThatHealth(EntityPlayer player, boolean regained, int healthGained)
	{
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

	    int maxRegained = DyingConfigGen.regen.maxRegenned;
	    int healthPerDeath = DyingConfigGen.general.healthPerDeath;
	    
		int oldMaxHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);
		
		if(regained)
		{
			int healthPlusKill = oldMaxHealth + healthGained;
			int newMaxHealth = healthPlusKill >= maxRegained ? maxRegained : healthPlusKill;
			
			data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			setHealth(player, newMaxHealth, true, healthGained);

			if(DyingConfigGen.regen.regennedHealthMessage)
			{
				ITextComponent text = new TextComponentTranslation("unhealthydying:regennedHealth.message", new Object[] {newMaxHealth});
				text.getStyle().setColor(TextFormatting.DARK_GREEN);
				player.sendStatusMessage(text, true);
			}
		}
		else
		{
			int healthMinusDeath = oldMaxHealth - healthPerDeath;
			int newMaxHealth = healthMinusDeath <= DyingConfigGen.general.minimumHealth ? DyingConfigGen.general.minimumHealth : healthMinusDeath;
			data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			setHealth(player, newMaxHealth, false, -1);
			
			if(DyingConfigGen.general.reducedHealthMessage)
			{
				ITextComponent text = new TextComponentTranslation("unhealthydying:reducedHealth.message", new Object[] {newMaxHealth});
				text.getStyle().setColor(TextFormatting.DARK_RED);
				player.sendStatusMessage(text, true);
			}
		}
	}
	
	public static void SetThatMaxHealth(EntityPlayer player, boolean regained, int healthGained)
	{
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

	    int maxRegained = DyingConfigGen.regen.maxRegenned;
	    int healthPerDeath = DyingConfigGen.general.healthPerDeath;
	    
		int oldMaxHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);

		if(regained)
		{
			int healthPlusKill = oldMaxHealth + healthGained;
			int newMaxHealth = healthPlusKill >= maxRegained ? maxRegained : healthPlusKill;

			data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			setMaxHealth(player, newMaxHealth);

			if(DyingConfigGen.regen.regennedHealthMessage)
			{
				ITextComponent text = new TextComponentTranslation("unhealthydying:regennedHealth.message", new Object[] {newMaxHealth});
				text.getStyle().setColor(TextFormatting.DARK_GREEN);
				player.sendStatusMessage(text, true);
			}
		}
		else
		{
			int healthMinusDeath = oldMaxHealth - healthPerDeath;
			int newMaxHealth = healthMinusDeath <= DyingConfigGen.general.minimumHealth ? DyingConfigGen.general.minimumHealth : healthMinusDeath;

			data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			setMaxHealth(player, newMaxHealth);
			
			if(DyingConfigGen.general.reducedHealthMessage)
			{
				ITextComponent text = new TextComponentTranslation("unhealthydying:reducedHealth.message", new Object[] {newMaxHealth});
				text.getStyle().setColor(TextFormatting.DARK_RED);
				player.sendStatusMessage(text, true);
			}
		}
	}
	
	public static void SyncHealth(EntityPlayer player)
	{
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
	    
		int oldMaxHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);

		data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)oldMaxHealth);
		playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
		setMaxHealth(player, oldMaxHealth);
	}
}
