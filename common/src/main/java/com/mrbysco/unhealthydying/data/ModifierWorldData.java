package com.mrbysco.unhealthydying.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.unhealthydying.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.UUID;

public class ModifierWorldData extends SavedData {
	private static final Identifier DATA_NAME = Constants.modLoc("modifier_data");
	private static final String MODIFIER_TAG = "stored_modifiers";

	public static final Codec<ModifierWorldData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					CompoundTag.CODEC.fieldOf(MODIFIER_TAG).forGetter(data -> data.modifierTag))
			.apply(inst, ModifierWorldData::new));


	private static final String EVERYBODY_TAG = "EverybodyModifier";

	private CompoundTag modifierTag;

	public ModifierWorldData(CompoundTag tag) {
		setModifierTag(tag);
	}

	public ModifierWorldData() {
		this(new CompoundTag());
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
		if (getModifierTag().contains(teamTag)) {
			return getModifierTag().getIntOr(teamTag, 0);
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
			return getModifierTag().getIntOr(EVERYBODY_TAG, 0);
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
			return getModifierTag().getIntOr(uuid.toString(), 0);
		} else {
			getModifierTag().putInt(uuid.toString(), 0);
			return 0;
		}
	}

	public static SavedDataType<ModifierWorldData> type() {
		return new SavedDataType<>(DATA_NAME, ModifierWorldData::new, CODEC, null);
	}

	public static ModifierWorldData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		SavedDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
