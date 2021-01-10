package com.mrbysco.unhealthydying.handlers;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.config.DyingConfigGen;
import com.mrbysco.unhealthydying.util.HealthUtil;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import com.mrbysco.unhealthydying.util.team.TeamHelper;
import com.mrbysco.unhealthydying.util.team.compat.FTBTeamHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class HealthHandler {	
	@SubscribeEvent
	public void firstJoin(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		
		if(!player.world.isRemote) {
			NBTTagCompound playerData = player.getEntityData();
			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
			double playerHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
			
			if(!data.hasKey(Reference.MODIFIED_HEALTH_TAG))
				data.setInteger(Reference.MODIFIED_HEALTH_TAG, 0);
			
			if(data.hasKey(Reference.REDUCED_HEALTH_TAG)) {
				int reducedHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);
				int maxHealth = DyingConfigGen.defaultSettings.defaultHealth;
				if(DyingConfigGen.regen.regenHealth && reducedHealth > DyingConfigGen.regen.maxRegenned) {
					maxHealth = DyingConfigGen.regen.maxRegenned;
				}
				data.setInteger(Reference.MODIFIED_HEALTH_TAG, reducedHealth - maxHealth);
				data.removeTag(Reference.REDUCED_HEALTH_TAG);
			}
			
			playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			
			//Sync teams
			TeamHelper.scoreboardSync(player);
			
			if(Loader.isModLoaded("ftblib")) {
				FTBTeamHelper.FTBTeamSync(player);
			}
		}
	}
	

	
	@SubscribeEvent
	public void setHealth(PlayerRespawnEvent event) {
		if(!event.isEndConquered()) {
			int healthPerDeath = -DyingConfigGen.general.healthPerDeath;
			EntityPlayer player = event.player;
			NBTTagCompound playerData = player.getEntityData();
			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

			if(!data.hasKey(Reference.MODIFIED_HEALTH_TAG)) {

				if(!player.world.isRemote) {
					HealthUtil.setHealth(player, HealthUtil.getOldHealth(player), healthPerDeath);
					UnhealthyHelper.setModifier(player, healthPerDeath);
				}
			} else {
				switch (DyingConfigGen.general.HealthSetting) {
					case EVERYBODY:
						UnhealthyHelper.setEveryonesHealth(player, healthPerDeath);
						break;
					case SEPERATE:
						UnhealthyHelper.SetHealth(player, healthPerDeath);
						break;
					case SCOREBOARD_TEAM:
						UnhealthyHelper.setScoreboardHealth(player, healthPerDeath);
						break;
					case FTB_TEAMS:
						UnhealthyHelper.teamHealth(player, healthPerDeath);
						break;
					default:
						UnhealthyHelper.SetHealth(player, healthPerDeath);
						break;
				}
			}
		}
		else
		{
			//Sync health
			HealthUtil.SyncHealth(event.player);
		}
	}
	
	@SubscribeEvent
	public void DimensionChange(PlayerChangedDimensionEvent event) {
		//Sync health
		HealthUtil.SyncHealth(event.player);
	}
}
