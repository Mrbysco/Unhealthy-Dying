package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.HealthUtil;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import com.mrbysco.unhealthydying.util.team.TeamHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HealthHandler {	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void firstJoin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		
		if(!player.world.isRemote) {
			CompoundNBT playerData = player.getPersistentData();
			CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);

			if(!data.contains(Reference.HEALTH_MODIFIER_TAG))
				data.putInt(Reference.HEALTH_MODIFIER_TAG, 0);

			if(data.contains(Reference.REDUCED_HEALTH_TAG)) {
				int reducedHealth = data.getInt(Reference.REDUCED_HEALTH_TAG);
				int maxHealth = UnhealthyConfig.SERVER.defaultHealth.get();
				if(UnhealthyConfig.SERVER.regenHealth.get() && reducedHealth > UnhealthyConfig.SERVER.maxRegained.get()) {
					maxHealth = UnhealthyConfig.SERVER.maxRegained.get();
				}
				data.putInt(Reference.HEALTH_MODIFIER_TAG, reducedHealth - maxHealth);
				data.remove(Reference.REDUCED_HEALTH_TAG);
			}

			playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
			
			//Sync teams
			TeamHelper.scoreboardSync(player);
		}
	}
	

	
	@SubscribeEvent
	public void setHealth(PlayerRespawnEvent event) {
		if(!event.isEndConquered()) {
			int healthPerDeath = -UnhealthyConfig.SERVER.healthPerDeath.get();
			PlayerEntity player = event.getPlayer();
			CompoundNBT playerData = player.getPersistentData();
			CompoundNBT data = UnhealthyHelper.getTag(playerData, PlayerEntity.PERSISTED_NBT_TAG);

			if(!data.contains(Reference.HEALTH_MODIFIER_TAG)) {
				if(!player.world.isRemote) {
					HealthUtil.setHealth(player, HealthUtil.getOldHealth(player), healthPerDeath);
					UnhealthyHelper.setModifier(player, healthPerDeath);
				}
			} else {
				switch (UnhealthyConfig.SERVER.healthSetting.get()) {
					case EVERYBODY:
						UnhealthyHelper.setEveryonesHealth(player, healthPerDeath);
						break;
					case SCOREBOARD_TEAM:
						UnhealthyHelper.setScoreboardHealth(player, healthPerDeath);
						break;
					default:
						UnhealthyHelper.SetHealth(player, healthPerDeath);
						break;
				}
			}
		} else {
			//Sync health
			HealthUtil.SyncHealth(event.getPlayer());
		}
	}
	
	@SubscribeEvent
	public void DimensionChange(PlayerChangedDimensionEvent event) {
		//Sync health
		HealthUtil.SyncHealth(event.getPlayer());
	}
}
