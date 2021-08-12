package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HealthHandler {	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onJoin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();

		if(!player.level.isClientSide) {
			UnhealthyHelper.initializeModifier(player, 0.0D);

			//Sync teams
			UnhealthyHelper.syncHealth(player);
		}
	}
	public static AttributeModifier getModifier(double modifier) {
		return new AttributeModifier(Reference.HEALTH_MODIFIER_ID, "UnhealthyHealthModifier", modifier, AttributeModifier.Operation.ADDITION);
	}
	@SubscribeEvent
	public void setHealth(PlayerRespawnEvent event) {
		PlayerEntity player = event.getPlayer();
		if(!event.isEndConquered()) {
			int healthPerDeath = -UnhealthyConfig.SERVER.healthPerDeath.get();
			float playerHealth = player.getMaxHealth();
			switch (UnhealthyConfig.SERVER.healthSetting.get()) {
				case EVERYBODY:
					UnhealthyHelper.setEveryonesHealth(player, healthPerDeath);
					break;
				case SCOREBOARD_TEAM:
					UnhealthyHelper.setScoreboardHealth(player, healthPerDeath);
					break;
				default:
					UnhealthyHelper.setHealth(player, healthPerDeath);
					break;
			}
		} else {
			//Sync health
			UnhealthyHelper.syncHealth(player);
		}
	}
}
