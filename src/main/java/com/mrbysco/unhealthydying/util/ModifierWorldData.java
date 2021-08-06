package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.UUID;

public class ModifierWorldData extends SavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_world_data";
	private static final String MODIFIER_TAG = "stored_modifiers";

	private static final String EVERYBODY_TAG = "EverybodyModifier";
	
	private CompoundTag modifierTag;

	public ModifierWorldData(CompoundTag tag) {
		setModifierTag(tag);
	}

	public ModifierWorldData() {
		this(new CompoundTag());
	}

	public static ModifierWorldData load(CompoundTag nbt) {
		if(nbt.contains(MODIFIER_TAG)) {
			return new ModifierWorldData((CompoundTag)nbt.get(MODIFIER_TAG));
		}
		return new ModifierWorldData();
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		compound.put(MODIFIER_TAG, this.modifierTag);
		return compound;
	}
	
	public CompoundTag getModifierTag() {
		return this.modifierTag;
	}
	
	public void setModifierTag(CompoundTag modifierTag) {
		this.modifierTag = modifierTag;
	}
	
	public void setScoreboardTeamModifier(String scoreboardName, int healthModifier) {
		String teamTag = "Scoreboard" + scoreboardName + "Modifier";
		getModifierTag().putInt(teamTag, healthModifier);
	}
	
	public int getScoreboardTeamModifier(String scoreboardName) {
		String teamTag = "Scoreboard" + scoreboardName + "Modifier";
		if(getModifierTag().contains(teamTag)) {
			return getModifierTag().getInt(teamTag);
		} else {
			getModifierTag().putInt(teamTag, 0);
			return 0;
		}
	}

	/* TODO: Leftover FTB support
	public void setTeamModifier(String teamName, int healthModifier) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		getModifierTag().putInt(teamTag, healthModifier);
	}

	public int getTeamModifier(String teamName) {
		String teamTag = "FTB_team" + teamName + "Modifier";
		if(getModifierTag().contains(teamTag)) {
			return getModifierTag().getInt(teamTag);
		} else {
			getModifierTag().putInt(teamTag, 0);
			return 0;
		}
	}*/

	public void setEverybodyModifier(int healthModifier) {
		getModifierTag().putInt(EVERYBODY_TAG, healthModifier);
	}

	public int getEverybodyModifier() {
		if(getModifierTag().contains(EVERYBODY_TAG)) {
			return getModifierTag().getInt(EVERYBODY_TAG);
		} else {
			getModifierTag().putInt(EVERYBODY_TAG, 0);
			return 0;
		}
	}

	public void setPlayerModifier(UUID uuid, int healthModifier) {
		getModifierTag().putInt(uuid.toString(), healthModifier);
	}

	public int getPlayerModifier(UUID uuid) {
		if(getModifierTag().contains(uuid.toString())) {
			return getModifierTag().getInt(uuid.toString());
		} else {
			getModifierTag().putInt(uuid.toString(), 0);
			return 0;
		}
	}

	public static ModifierWorldData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(ModifierWorldData::load, ModifierWorldData::new, DATA_NAME);
	}
}
