package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.UnhealthyDying;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.team.TeamHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class UnhealthyHelper {

	public static int getNewModifiedAmount(PlayerEntity player, int healthModifier) {
		int oldModified = getOldModifier(player);
		int newModified = oldModified + healthModifier;
		
		newModified = getSafeModifier(newModified);
		
		if(newModified == 0) {
			ITextComponent text = new TranslationTextComponent("unhealthydying:modifierzero.message").mergeStyle(TextFormatting.DARK_GREEN);
			player.sendMessage(text, Util.DUMMY_UUID);
		}
		
		setModifier(player, newModified);
		
		return newModified;
	}
	
	public static void setModifier(PlayerEntity player, int modifier) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);

		data.putInt(Reference.HEALTH_MODIFIER_TAG, modifier);
		
		playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
	}
	
	private static int getOldModifier(PlayerEntity player) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);
		
		return data.getInt(Reference.HEALTH_MODIFIER_TAG);
	}
	
	public static int getSafeModifier(int oldAmount) {
		int newModified = oldAmount;
		int maxHealth = UnhealthyConfig.SERVER.defaultHealth.get();
		
		if(newModified > 0) {
			if(UnhealthyConfig.SERVER.regenHealth.get()) {
				int maxPositive = UnhealthyConfig.SERVER.maxRegained.get();
				if((maxHealth + newModified) > maxPositive)
					newModified = maxPositive - maxHealth;
			} else {
				return 0;
			}
		} else if(newModified < 0) {
			int maxNegative = UnhealthyConfig.SERVER.minimumHealth.get();
			if((maxHealth + newModified) < maxNegative)
				newModified = -(maxHealth - maxNegative);
		}
		return newModified;
	}
	
	public static int getModifiedAmount(PlayerEntity player) {
		CompoundNBT playerData = player.getPersistentData();
		CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);
		return data.getInt(Reference.HEALTH_MODIFIER_TAG);
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
	
	public static CompoundNBT getTag(CompoundNBT tag, String key) {
		if(tag == null || !tag.contains(key)) {
			return new CompoundNBT();
		}
		return tag.getCompound(key);
	}
	
	
//	@Optional.Method(modid = "ftblib")
//	public static void teamHealth(PlayerEntity player, int healthModifier) {
//		World world = player.world;
//		String team = com.feed_the_beast.ftblib.lib.data.FTBLibAPI.getTeam(player.getUniqueID());
//		if(!team.isEmpty()) {
//			for(PlayerEntity players : world.getPlayers()) {
//				if(players.equals(player))
//					SetHealth(player, healthModifier);
//				else {
//					if(com.feed_the_beast.ftblib.lib.data.FTBLibAPI.isPlayerInTeam(player.getOfflineUUID(players.getName()), team)) {
//						SetMaxHealth(players, healthModifier);
//					}
//				}
//			}
//		}
//	}
	
	public static void setEveryonesHealth(PlayerEntity player, int healthModifier) {
		for(PlayerEntity players : player.world.getPlayers()) {
			if(players.equals(player))
				SetHealth(player, healthModifier);
			else
				SetMaxHealth(players, healthModifier);
		}
	}
	
	public static void setScoreboardHealth(PlayerEntity player, int healthModifier) {
		World world = player.world;
		if(player.getTeam() != null) {
			Team team = player.getTeam();
			for(PlayerEntity players : world.getPlayers()) {
				int newModifier = TeamHelper.changeStoredScoreboardModifier(world, team, healthModifier);
				if(players.equals(player)) {
					SetHealth(player, newModifier, false);
				} else {
					if(players.isOnScoreboardTeam(team)) {
						SetMaxHealth(players, healthModifier, false);
					}
				}
			}
		}
		else
		{
			UnhealthyDying.logger.error(player.getName() + " is not in a team");
		}
	}
	
	public static void sendHealthMessage(PlayerEntity player, int newHealth, int gained) {
		if(gained > 0) {
			if(UnhealthyConfig.SERVER.regenHealthMessage.get()) {
				ITextComponent text = new TranslationTextComponent("unhealthydying:regennedHealth.message", newHealth).mergeStyle(TextFormatting.DARK_GREEN);
				player.sendStatusMessage(text, true);
			}
		} else {
			if(UnhealthyConfig.SERVER.reducedHealthMessage.get()) {
				ITextComponent text = new TranslationTextComponent("unhealthydying:reducedHealth.message", newHealth).mergeStyle(TextFormatting.DARK_RED);
				player.sendStatusMessage(text, true);
			}
		}
	}
	
	public static void SetHealth(PlayerEntity player, int healthModifier) {
		SetHealth(player, healthModifier, true);
	}
	
	public static void SetHealth(PlayerEntity player, int healthModifier, boolean recalculate) {
		int newModified = healthModifier;
		if(recalculate) {
			newModified = getNewModifiedAmount(player, healthModifier);
		}
	    int modifiedHealth = safetyCheck(HealthUtil.getNewHealth(newModified));
		
	    sendHealthMessage(player, modifiedHealth, healthModifier);

		setModifier(player, newModified);
		HealthUtil.setHealth(player, modifiedHealth);
	}
	
	public static void SetMaxHealth(PlayerEntity player, int healthModifier) {
		SetMaxHealth(player, healthModifier, true);
	}
	
	public static void SetMaxHealth(PlayerEntity player, int healthModifier, boolean recalculate) {
		int newModified = healthModifier;
		if(recalculate) {
			newModified = getNewModifiedAmount(player, healthModifier);
		}
		int modifiedHealth = safetyCheck(HealthUtil.getNewHealth(newModified));
		
		sendHealthMessage(player, modifiedHealth, healthModifier);
		
		setModifier(player, newModified);
		HealthUtil.setMaxHealth(player, modifiedHealth);
	}

	public static int safetyCheck(int health) {
		int newHealth = health;
		if(UnhealthyConfig.SERVER.regenHealth.get() && health > UnhealthyConfig.SERVER.maxRegained.get()) {
			newHealth = UnhealthyConfig.SERVER.maxRegained.get();
		} 
		if(health < UnhealthyConfig.SERVER.minimumHealth.get()) {
			newHealth = UnhealthyConfig.SERVER.minimumHealth.get();
		}
		
		return newHealth;
	}
}
