package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.config.DyingConfigGen;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

public class HealthUtil {
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(EntityPlayer player, double oldHealth, int healthModifier) {
		int newModifier = UnhealthyHelper.getNewModifiedAmount(player, healthModifier);
		int newHealth = (int)oldHealth + newModifier;
		
		newHealth = UnhealthyHelper.safetyCheck(newHealth);
		
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
		int modifier = UnhealthyHelper.getModifiedAmount(player);
		System.out.println("Max Health: " + player.getMaxHealth());
		int health = DyingConfigGen.defaultSettings.defaultHealth;
		
		int newHealth = health + modifier;
		
		return UnhealthyHelper.safetyCheck(newHealth);
	}
}
