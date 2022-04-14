package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HealthHandler {
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onJoin(PlayerLoggedInEvent event) {
		Player player = event.getPlayer();

		if (!player.level.isClientSide) {
			UnhealthyHelper.initializeModifier(player, 0.0D);

			//Sync teams
			UnhealthyHelper.syncHealth(player);
		}
	}

	@SubscribeEvent
	public void setHealth(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (!event.isEndConquered()) {
			int healthPerDeath = -UnhealthyConfig.SERVER.healthPerDeath.get();

			switch (UnhealthyConfig.SERVER.healthSetting.get()) {
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
