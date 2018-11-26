package com.Mrbysco.UnhealthyDying.util;

import com.Mrbysco.UnhealthyDying.config.DyingConfigGen;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class UnhealthyHelper {
	public static void setHealth(EntityPlayer entity, int maxHealth, boolean regained) {
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        if(regained)
        {
        	int entityHealth = (int)entity.getHealth() + DyingConfigGen.regen.healthPerKill;
            entity.setHealth(entityHealth);
        }
        else
        {
            entity.setHealth(maxHealth);
        }
	}
	
	public static void setHealth(EntityPlayer entity, double maxHealth, boolean regained) {
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        if(regained)
        {
        	int entityHealth = (int)entity.getHealth() + DyingConfigGen.regen.healthPerKill;
            entity.setHealth(entityHealth);
        }
        else
        {
            entity.setHealth((int)maxHealth);
        }
	}
	
	public static ResourceLocation getEntityLocation(String name)
	{
		String[] splitResource = name.split(":");
		if (splitResource.length != 2)
			return null;
		else
			return new ResourceLocation(splitResource[0], splitResource[1]);
	}
	
	public static NBTTagCompound getTag(NBTTagCompound tag, String key) {
		if(tag == null || !tag.hasKey(key)) {
			return new NBTTagCompound();
		}
		return tag.getCompoundTag(key);
	}
}
