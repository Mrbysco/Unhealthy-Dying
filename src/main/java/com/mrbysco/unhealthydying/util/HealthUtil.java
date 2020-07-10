package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.config.DyingConfigGen;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;

public class HealthUtil {
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(PlayerEntity player, double oldHealth, int healthModifier) {
		int newModifier = UnhealthyHelper.getNewModifiedAmount(player, healthModifier);
		int newHealth = (int)oldHealth + newModifier;
		
		newHealth = UnhealthyHelper.safetyCheck(newHealth);
		
		player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
       
		player.setHealth(newHealth);
	}	
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setHealth(PlayerEntity player, int newHealth) {
		player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
        player.setHealth(newHealth);
	}
	
	/**
	 * Sets the players health and maxHealth.
	 */
	public static void setMaxHealth(PlayerEntity player, int newHealth) {
		player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHealth);
	}
	
	/*
	 * Sets the players health without updating the modifier
	 */
	public static void SyncHealth(PlayerEntity player) {
		int oldHealth = getOldHealth(player);
		player.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(oldHealth);
        player.setHealth(oldHealth);
	}
	
	/*
	 * Gets modified health
	 */
	public static int getNewHealth(int healthModifier) {
		int health = DyingConfigGen.SERVER.defaultHealth.get();
		
		return (health + healthModifier);
	}
	
	/*
	 * Get's the players old max health
	 */	
	public static int getOldHealth(PlayerEntity player) {
		int modifier = UnhealthyHelper.getModifiedAmount(player);
		System.out.println("Max Health: " + player.getMaxHealth());
		int health = DyingConfigGen.SERVER.defaultHealth.get();
		
		int newHealth = health + modifier;
		
		return UnhealthyHelper.safetyCheck(newHealth);
	}
}
