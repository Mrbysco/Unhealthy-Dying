package com.Mrbysco.UnhealthyDying.handlers;

import com.Mrbysco.UnhealthyDying.Reference;
import com.Mrbysco.UnhealthyDying.config.DyingConfigGen;
import com.Mrbysco.UnhealthyDying.util.UnhealthyHelper;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class HealthHandler {	
	@SubscribeEvent
	public void firstJoin(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		
		if(!player.world.isRemote)
		{
			NBTTagCompound playerData = player.getEntityData();
			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
			double playerHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
			
			if(!data.hasKey(Reference.REDUCED_HEALTH_TAG)) 
			{
				data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)playerHealth);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			}
		}
	}
	
	@SubscribeEvent
	public void setHealth(PlayerRespawnEvent event) {
		if(!event.isEndConquered())
		{
			EntityPlayer player = event.player;
			NBTTagCompound playerData = player.getEntityData();
			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);

			if(!data.hasKey(Reference.REDUCED_HEALTH_TAG))
			{
				double playerHealth = (int)player.getMaxHealth();

				if(!player.world.isRemote)
				{
					if(!data.hasKey(Reference.REDUCED_HEALTH_TAG)) 
					{
						data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)playerHealth);
						playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
						UnhealthyHelper.setHealth(player, playerHealth, false);
					}
				}
			}
			else
			{
				int oldMaxHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);
				int healthMinusDeath = oldMaxHealth - DyingConfigGen.general.healthPerDeath;
				int newMaxHealth = healthMinusDeath <= DyingConfigGen.general.minimumHealth ? DyingConfigGen.general.minimumHealth : healthMinusDeath;
				
				data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)newMaxHealth);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
				UnhealthyHelper.setHealth(player, newMaxHealth, false);
				
				if(DyingConfigGen.general.reducedHealthMessage)
				{
					player.sendStatusMessage(new TextComponentTranslation("unhealthydying:reducedHealth.message", new Object[newMaxHealth]), true);
				}
			}
		}
	}
}
