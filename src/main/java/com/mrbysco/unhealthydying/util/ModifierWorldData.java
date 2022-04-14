package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.Reference;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.UUID;

public class ModifierWorldData extends WorldSavedData {
	private static final String DATA_NAME = Reference.MOD_ID + "_world_data";
	private static final String MODIFIER_TAG = "stored_modifiers";

	private static final String EVERYBODY_TAG = "EverybodyModifier";

	private CompoundNBT modifierTag;

	public ModifierWorldData() {
		super(DATA_NAME);

		this.modifierTag = new CompoundNBT();
	}

	@Override
	public void load(CompoundNBT nbt) {
		if (nbt.contains(MODIFIER_TAG)) {
			setModifierTag((CompoundNBT) nbt.get(MODIFIER_TAG));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put(MODIFIER_TAG, this.modifierTag);
		return compound;
	}

	public CompoundNBT getModifierTag() {
		return this.modifierTag;
	}

	public void setModifierTag(CompoundNBT modifierTag) {
		this.modifierTag = modifierTag;
	}

	public void setScoreboardTeamModifier(String scoreboardName, int healthModifier) {
		String teamTag = "Scoreboard" + scoreboardName + "Modifier";
		getModifierTag().putInt(teamTag, healthModifier);
	}

	public int getScoreboardTeamModifier(String scoreboardName) {
		String teamTag = "Scoreboard" + scoreboardName + "Modifier";
		if (getModifierTag().contains(teamTag)) {
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
		if (getModifierTag().contains(EVERYBODY_TAG)) {
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
		if (getModifierTag().contains(uuid.toString())) {
			return getModifierTag().getInt(uuid.toString());
		} else {
			getModifierTag().putInt(uuid.toString(), 0);
			return 0;
		}
	}

	public static ModifierWorldData get(World world) {
		if (!(world instanceof ServerWorld)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerWorld overworld = world.getServer().getLevel(World.OVERWORLD);

		DimensionSavedDataManager storage = overworld.getDataStorage();
		return storage.computeIfAbsent(ModifierWorldData::new, DATA_NAME);
	}
}
