package com.mrbysco.unhealthydying.util.team.compat;

public class FTBTeamHelper {
//	@Optional.Method(modid = "ftblib")
//	public static void FTBTeamSync(PlayerEntity player) {
//		if ( DyingConfigGen.general.HealthSetting.equals(EnumHealthSetting.FTB_TEAMS) ) {
//			if(!player.world.isRemote) {
//				if(teamModifierExists(player.getTeam())) {
//					int newModifier = getTeamModifier(player.getTeam());
//					UnhealthyHelper.SetHealth(player, newModifier, false);
//				}
//			}
//		}
//	}
//
//	@Optional.Method(modid = "ftblib")
//	public static int changeStoredTeamModifier(World worldIn, Team team, int healthModifier) {
//		String teamName = team.getName();
//		String teamTag = "FTB_team" + teamName + "Modifier";
//		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
//
//		modifierStorage.setTeamModifier(teamName, healthModifier);
//		return modifierStorage.getTeamModifier(teamName);
//	}
//
//	@Optional.Method(modid = "ftblib")
//	public static boolean teamModifierExists(Team team) {
//		String teamTag = "FTB_team" + team.getName() + "Modifier";
//		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
//		CompoundNBT tag = modifierStorage.getModifierTag();
//		return tag.hasKey(teamTag);
//	}
//
//	@Optional.Method(modid = "ftblib")
//	public static int getTeamModifier(Team team) {
//		ModifierWorldData modifierStorage = ModifierWorldData.getForWorld(DimensionManager.getWorld(0));
//		return modifierStorage.getTeamModifier(team.getName());
//	}
}
