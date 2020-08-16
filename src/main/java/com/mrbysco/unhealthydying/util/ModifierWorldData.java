package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class ModifierWorldData extends WorldSavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_world_data";
	private static final String MODIFIER_TAG = "stored_modifiers";
	
	private CompoundNBT modifierTag;

	public ModifierWorldData(String name) {
		super(name);
		
		this.modifierTag = new CompoundNBT();
	}
	
	public ModifierWorldData() {
		super(DATA_NAME);
		
		this.modifierTag = new CompoundNBT();
	}

	@Override
	public void read(CompoundNBT nbt) {
		if(nbt.contains(MODIFIER_TAG)) {
			this.modifierTag = (CompoundNBT)nbt.get(MODIFIER_TAG);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(MODIFIER_TAG, this.modifierTag);
		return compound;
	}
	
	public CompoundNBT getModifierTag() {
		return modifierTag;
	}
	
	public void setModifierTag(CompoundNBT modifierTag) {
		this.modifierTag = modifierTag;
	}
	
	public void setScoreboardTeamModifier(String teamName, int healthModifier) {
		String teamTag = "Scoreboard" + teamName + "Modifier";
		if(this.modifierTag.contains(teamTag)) {
			int modifierAmount = UnhealthyHelper.getSafeModifier(healthModifier);
			this.modifierTag.putInt(teamTag, modifierAmount);
		} else {
			int storedModifier = this.modifierTag.getInt(teamTag);
			storedModifier = UnhealthyHelper.getSafeModifier(storedModifier + healthModifier);

			this.modifierTag.putInt(teamTag, storedModifier);
		}
	}
	
	public int getScoreboardTeamModifier(String teamName) {
		String teamTag = "Scoreboard" + teamName + "Modifier";
		if(this.modifierTag.contains(teamTag)) {
			return this.modifierTag.getInt(teamTag);
		} else {
			this.modifierTag.putInt(teamTag, 0);
			return 0;
		}
	}
	
	
	public void setTeamModifier(String teamName, int healthModifier) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		if(this.modifierTag.contains(teamTag)) {
			int modifierAmount = UnhealthyHelper.getSafeModifier(healthModifier);
			this.modifierTag.putInt(teamTag, modifierAmount);
		} else {
			int storedModifier = this.modifierTag.getInt(teamTag);
			storedModifier = UnhealthyHelper.getSafeModifier(storedModifier + healthModifier);

			this.modifierTag.putInt(teamTag, storedModifier);
		}
	}
	
	public int getTeamModifier(String teamName) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		if(this.modifierTag.contains(teamTag)) {
			return this.modifierTag.getInt(teamTag);
		} else {
			this.modifierTag.putInt(teamTag, 0);
			return 0;
		}
	}

	public static ModifierWorldData getForWorld(World world) {
		if (!(world instanceof ServerWorld)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerWorld overworld = world.getServer().getWorld(DimensionType.OVERWORLD);

		DimensionSavedDataManager storage = overworld.getSavedData();
		return storage.getOrCreate(ModifierWorldData::new, DATA_NAME);
	}
}
