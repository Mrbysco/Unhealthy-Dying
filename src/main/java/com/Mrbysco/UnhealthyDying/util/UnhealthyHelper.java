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

	public static int getNewModifiedAmount(EntityPlayer player, int healthModifier) {		
		int oldModified = getOldModifier(player);
		int newModified = oldModified + healthModifier;
		
		newModified = getSafeModifier(newModified);
		
		if(newModified == 0) {
			ITextComponent text = new TextComponentTranslation("unhealthydying:modifierzero.message", new Object[0]);
			text.getStyle().setColor(TextFormatting.DARK_GREEN);
			player.sendMessage(text);
		}
		
		setModifier(player, newModified);
		
		return newModified;
	}
	
	public static void setModifier(EntityPlayer player, int modifier) {
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

		data.setInteger(Reference.MODIFIED_HEALTH_TAG, modifier);
		
		playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
	}
	
	private static int getOldModifier(EntityPlayer player) {
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
		
		return data.getInteger(Reference.MODIFIED_HEALTH_TAG);
	}
	
	public static int getSafeModifier(int oldAmount) {
		int newModified = oldAmount;
		int maxHealth = DyingConfigGen.defaultSettings.defaultHealth;
		
		if(newModified > 0) {
			if(DyingConfigGen.regen.regenHealth) {
				int maxPositive = DyingConfigGen.regen.maxRegenned;
				if((maxHealth + newModified) > maxPositive)
					newModified = maxPositive;
			} else {
				return 0;
			}
		}
		
		if(newModified < 0) {
			int maxNegative = DyingConfigGen.general.minimumHealth;
			if((maxHealth + newModified) < maxNegative)
				newModified = maxNegative;
		}
		System.out.println(newModified);
		return newModified;
	}
	
	public static int getModifiedAmount(EntityPlayer player) {
		NBTTagCompound playerData = player.getEntityData();
		NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
		return data.getInteger(Reference.MODIFIED_HEALTH_TAG);
	}
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(EntityPlayer player, double oldHealth, int healthModifier) {
		int newModifier = getNewModifiedAmount(player, healthModifier);
		int newHealth = (int)oldHealth + newModifier;
		
		newHealth = safetyCheck(newHealth);
		
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
       
		player.setHealth(newHealth);
	}	
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(EntityPlayer player, int newHealth) {
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
        player.setHealth(newHealth);
	}
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setMaxHealth(EntityPlayer player, int newHealth) {
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
	}
	
	/*
	 * Sets the players health without updating the modifier
	 */
	public static void SyncHealth(EntityPlayer player) {
		int oldHealth = getOldHealth(player);
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(oldHealth);
        player.setHealth(oldHealth);
	}
	
	/*
	 * Gets modified health
	 */
	public static int getNewHealth(int healthModifier) {
		int health = DyingConfigGen.defaultSettings.defaultHealth;
		
		return (health + healthModifier);
	}
	
	/*
	 * Get's the players old max health
	 */	
	public static int getOldHealth(EntityPlayer player) {
		int modifier = getModifiedAmount(player);
		System.out.println("Max Health: " + player.getMaxHealth());
		int health = DyingConfigGen.defaultSettings.defaultHealth;
		
		int newHealth = health + modifier;
		
		return safetyCheck(newHealth);
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
	public static void teamHealth(EntityPlayer player, int healthModifier) {
		World world = player.world;
		String team = com.feed_the_beast.ftblib.lib.data.FTBLibAPI.getTeam(player.getUniqueID());
		if(!team.isEmpty()) {
			for(EntityPlayer players : world.playerEntities) {
				if(players.equals(player))
					SetThatHealth(player, healthModifier);
				else {
					if(com.feed_the_beast.ftblib.lib.data.FTBLibAPI.isPlayerInTeam(player.getOfflineUUID(players.getName()), team)) {
						SetThatHealth(players, healthModifier);
					}
				}
			}
		}
	}
	
	public static void setEveryonesHealth(EntityPlayer player, int healthModifier) {
		for(EntityPlayer players : player.world.playerEntities) {
			if(players.equals(player))
				SetThatHealth(player, healthModifier);
			else
				SetThatHealth(players, healthModifier);
		}
	}
	
	public static void setScoreboardHealth(EntityPlayer player, int healthModifier) {
		World world = player.world;
		if(player.getTeam() != null) {
			Team team = player.getTeam();
			for(EntityPlayer players : world.playerEntities) {
				int newModifier = TeamHelper.changeStoredScoreboardModifier(world, team, healthModifier);
				if(players.equals(player)) {
					SetThatHealth(player, newModifier, false);
				} else {
					if(players.isOnScoreboardTeam(team)) {
						SetThatHealth(players, healthModifier, false);
					}
				}
			}
		}
		else
		{
			UnhealthyDying.logger.error(player.getName() + " is not in a team");
		}
	}
	
	public static void sendHealthMessage(EntityPlayer player, int newHealth, int gained) {
		if(gained > 0) {
			if(DyingConfigGen.regen.regennedHealthMessage) {
				ITextComponent text = new TextComponentTranslation("unhealthydying:regennedHealth.message", new Object[] { newHealth });
				text.getStyle().setColor(TextFormatting.DARK_GREEN);
				player.sendStatusMessage(text, true);
			}
		} else {
			if(DyingConfigGen.general.reducedHealthMessage) {
				ITextComponent text = new TextComponentTranslation("unhealthydying:reducedHealth.message", new Object[] { newHealth });
				text.getStyle().setColor(TextFormatting.DARK_RED);
				player.sendStatusMessage(text, true);
			}
		}
	}
	
	public static void SetThatHealth(EntityPlayer player, int healthModifier) {
		SetThatHealth(player, healthModifier, true);
	}
	
	public static void SetThatHealth(EntityPlayer player, int healthModifier, boolean recalculate) {
		int newModified = healthModifier;
		if(recalculate) {
			newModified = getNewModifiedAmount(player, healthModifier);
		} else {
			setModifier(player, healthModifier);
		}
	    int modifiedHealth = safetyCheck(getNewHealth(newModified));
		
	    sendHealthMessage(player, modifiedHealth, healthModifier);
	    
		setHealth(player, modifiedHealth);
	}

	public static int safetyCheck(int health) {
		int newHealth = health;
		if(DyingConfigGen.regen.regenHealth && newHealth > DyingConfigGen.regen.maxRegenned) {
			newHealth = DyingConfigGen.regen.maxRegenned;
		}
		
		if(newHealth < DyingConfigGen.general.minimumHealth) {
			newHealth = DyingConfigGen.general.minimumHealth;
		}
		
		return newHealth;
	}
}
