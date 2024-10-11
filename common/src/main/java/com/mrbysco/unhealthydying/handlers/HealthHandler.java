package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.platform.Services;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.world.entity.player.Player;

public class HealthHandler {
	public static void onJoin(Player player) {
		if (!player.level().isClientSide) {
			UnhealthyHelper.initializeModifier(player, 0.0D);

			//Sync teams
			UnhealthyHelper.syncHealth(player);
		}
	}

	public static void setHealth(Player player, boolean endConquered) {
		if (!endConquered) {
			int healthPerDeath = -Services.PLATFORM.getHealthPerDeath();

			switch (Services.PLATFORM.getHealthSetting()) {
				case EVERYBODY -> UnhealthyHelper.setEveryonesHealth(player, healthPerDeath);
				case SCOREBOARD_TEAM -> UnhealthyHelper.setScoreboardHealth(player, healthPerDeath);
				default -> UnhealthyHelper.setHealth(player, healthPerDeath);
			}
		} else {
			//Sync health
			UnhealthyHelper.syncHealth(player);
		}
	}
}
