package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.config.DyingConfigGen;
import com.mrbysco.unhealthydying.config.DyingConfigGen.EnumHealthSetting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Optional;

public class TeamHelper {
	
	public static void scoreboardSync(EntityPlayer player) {
		if(DyingConfigGen.general.HealthSetting.equals(EnumHealthSetting.SCOREBOARD_TEAM) && player.getTeam() != null) {
			if(!player.world.isRemote) {
				if(scoreboardTeamModifierExists(player.getTeam())) {
					int newModifier = getScoreboardTeamModifier(player.getTeam());
					UnhealthyHelper.SetThatHealth(player, newModifier, false);
				}
			}
		}
	}

	@Optional.Method(modid = "ftblib")
	public static void FTBTeamSync(EntityPlayer player) {
		if ( DyingConfigGen.general.HealthSetting.equals(EnumHealthSetting.FTB_TEAMS) ) {
			if(!player.world.isRemote) {
				if(teamModifierExists(player.getTeam())) {
					int newModifier = getTeamModifier(player.getTeam());
					UnhealthyHelper.SetThatHealth(player, newModifier, false);
				}
			}
		}
	}
	
	public static int changeStoredScoreboardModifier(World worldIn, Team team, int healthModifier) {
		String teamName = team.getName();
		String teamTag = "Scoreboard" + teamName + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		
		modifierStorage.setScoreboardTeamModifier(teamName, healthModifier);
		return modifierStorage.getScoreboardTeamModifier(teamName);
	}
	
	public static boolean scoreboardTeamModifierExists(Team team) {
		String teamTag = "Scoreboard" + team.getName() + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		NBTTagCompound tag = modifierStorage.getModifierTag();
		return tag.hasKey(teamTag);
	}
	
	public static int getScoreboardTeamModifier(Team team) {
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		return modifierStorage.getScoreboardTeamModifier(team.getName());
	}

	@Optional.Method(modid = "ftblib")
	public static int changeStoredTeamModifier(World worldIn, Team team, int healthModifier) {
		String teamName = team.getName();
		String teamTag = "FTB_team" + teamName + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		
		modifierStorage.setTeamModifier(teamName, healthModifier);
		return modifierStorage.getTeamModifier(teamName);
	}

	@Optional.Method(modid = "ftblib")
	public static boolean teamModifierExists(Team team) {
		String teamTag = "FTB_team" + team.getName() + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		NBTTagCompound tag = modifierStorage.getModifierTag();
		return tag.hasKey(teamTag);
	}

	@Optional.Method(modid = "ftblib")
	public static int getTeamModifier(Team team) {
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
		return modifierStorage.getTeamModifier(team.getName());
	}
}
