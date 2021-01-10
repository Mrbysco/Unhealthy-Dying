package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ModifierWorldData extends WorldSavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_world_data";
	private static final String MODIFIER_TAG = "stored_modifiers";
	
	private NBTTagCompound modifierTag;

	public ModifierWorldData(String name) {
		super(name);
		
		this.modifierTag = new NBTTagCompound();
	}
	
	public ModifierWorldData() {
		super(DATA_NAME);
		
		this.modifierTag = new NBTTagCompound();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey(MODIFIER_TAG)) {
			this.modifierTag = (NBTTagCompound)nbt.getTag(MODIFIER_TAG);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag(MODIFIER_TAG, this.modifierTag);
		return compound;
	}
	
	public NBTTagCompound getModifierTag() {
		return modifierTag;
	}
	
	public void setModifierTag(NBTTagCompound modifierTag) {
		this.modifierTag = modifierTag;
	}
	
	public void setScoreboardTeamModifier(String teamName, int healthModifier) {
		String teamTag = "Scoreboard" + teamName + "Modifier";
		if(this.modifierTag.hasKey(teamTag)) {
			int modifierAmount = UnhealthyHelper.getSafeModifier(healthModifier);
			this.modifierTag.setInteger(teamTag, modifierAmount);
		} else {
			int storedModifier = this.modifierTag.getInteger(teamTag);
			storedModifier = UnhealthyHelper.getSafeModifier(storedModifier + healthModifier);

			this.modifierTag.setInteger(teamTag, storedModifier);
		}
	}
	
	public int getScoreboardTeamModifier(String teamName) {
		String teamTag = "Scoreboard" + teamName + "Modifier";
		if(this.modifierTag.hasKey(teamTag)) {
			return this.modifierTag.getInteger(teamTag);
		} else {
			this.modifierTag.setInteger(teamTag, 0);
			return 0;
		}
	}
	
	
	public void setTeamModifier(String teamName, int healthModifier) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		if(this.modifierTag.hasKey(teamTag)) {
			int modifierAmount = UnhealthyHelper.getSafeModifier(healthModifier);
			this.modifierTag.setInteger(teamTag, modifierAmount);
		} else {
			int storedModifier = this.modifierTag.getInteger(teamTag);
			storedModifier = UnhealthyHelper.getSafeModifier(storedModifier + healthModifier);

			this.modifierTag.setInteger(teamTag, storedModifier);
		}
	}
	
	public int getTeamModifier(String teamName) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		if(this.modifierTag.hasKey(teamTag)) {
			return this.modifierTag.getInteger(teamTag);
		} else {
			this.modifierTag.setInteger(teamTag, 0);
			return 0;
		}
	}
	
	public static ModifierWorldData getForWorld(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ModifierWorldData data = (ModifierWorldData) storage.getOrLoadData(ModifierWorldData.class, DATA_NAME);
		if (data == null) {
			data = new ModifierWorldData();
			storage.setData(DATA_NAME, data);
		}
		return data;
	}
}
