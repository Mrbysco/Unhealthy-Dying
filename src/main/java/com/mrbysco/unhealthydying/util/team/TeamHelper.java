package com.mrbysco.unhealthydying.util.team;

import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.config.EnumHealthSetting;
import com.mrbysco.unhealthydying.util.ModifierWorldData;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;

public class TeamHelper {
	
	public static void scoreboardSync(PlayerEntity player) {
		if(UnhealthyConfig.SERVER.healthSetting.get().equals(EnumHealthSetting.SCOREBOARD_TEAM) && player.getTeam() != null) {
			if(!player.world.isRemote) {
				World world = player.getEntityWorld();
				if(scoreboardTeamModifierExists(world, player.getTeam())) {
					int newModifier = getScoreboardTeamModifier(world, player.getTeam());
					UnhealthyHelper.SetHealth(player, newModifier, false);
				}
			}
		}
	}
	
	public static int changeStoredScoreboardModifier(World worldIn, Team team, int healthModifier) {
		String teamName = team.getName();
		String teamTag = "Scoreboard" + teamName + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(worldIn);
		
		modifierStorage.setScoreboardTeamModifier(teamTag, healthModifier);
		return modifierStorage.getScoreboardTeamModifier(teamTag);
	}
	
	public static boolean scoreboardTeamModifierExists(World worldIn, Team team) {
		String teamTag = "Scoreboard" + team.getName() + "Modifier";
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(worldIn);
		CompoundNBT tag = modifierStorage.getModifierTag();
		return tag.contains(teamTag);
	}
	
	public static int getScoreboardTeamModifier(World worldIn, Team team) {
		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(worldIn);
		return modifierStorage.getScoreboardTeamModifier(team.getName());
	}
}
