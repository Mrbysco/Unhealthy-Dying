package com.mrbysco.unhealthydying.config;

import com.mrbysco.unhealthydying.Constants;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;

@Config(name = Constants.MOD_ID)
public class UnhealthyConfigFabric implements ConfigData {
	@ConfigEntry.Gui.CollapsibleObject
	public final General general = new General();

	@ConfigEntry.Gui.CollapsibleObject
	public final Regen regen = new Regen();

	public static class General {
		@Comment("The chance of losing health upon death (0.1 = 10%) (Valid range: 0.01 - 1.0) [default: 1.0]")
		public double healthLossChance = 1.0;

		@Comment("Minimum amount of health the player can end up with (2 = 1 heart) [default: 2]")
		public int minimumHealth = 2;

		@Comment("The amount of health taken from the player upon death (2 = 1 heart) [default: 2]")
		public int healthPerDeath = 2;

		@Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public boolean reducedHealthMessage = true;

		@Comment("Decides if the reduced health is per player, for everybody or per team [default: SEPARATE]")
		public EnumHealthSetting healthSetting = EnumHealthSetting.SEPARATE;
	}

	public static class Regen {
		@Comment("When set to true allows you to gain back health upon killing set target(s) [default: false]")
		public boolean regenHealth = false;

		@Comment("The amount of max health the player can get from killing the target(s) (20 = 10 hearts) [default: 20]")
		public int maxRegained = 20;

		@Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public boolean regenHealthMessage = true;

		@Comment("""
				Adding lines / removing lines specifies which mobs will cause the players to regen max health
				Syntax: modid:mobname,healthRegenned,amount
				For wildcards use *. For instance [*:*,1,20] would mean every 20 kills regain half a heart
				While [minecraft:*,1,10] would mean every 10 kills of vanilla mobs regains half a heart""")
		public List<String> regenTargets = Arrays.asList(
				"minecraft:ender_dragon,4,1",
				"minecraft:wither,2,1"
		);
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		// Correct invalid values
		if (general.healthLossChance < 0.01 || general.healthLossChance > 1.0) {
			Constants.LOGGER.warn("Invalid value for healthLossChance: {}. Resetting to default.", general.healthLossChance);
			general.healthLossChance = 1.0;
		}
		if (general.minimumHealth < 1) {
			Constants.LOGGER.warn("Invalid value for minimumHealth: {}. Resetting to default.", general.minimumHealth);
			general.minimumHealth = 1;
		}
		if (general.healthPerDeath < 1) {
			Constants.LOGGER.warn("Invalid value for healthPerDeath: {}. Resetting to default.", general.healthPerDeath);
			general.healthPerDeath = 1;
		}
		if (regen.maxRegained < 1) {
			Constants.LOGGER.warn("Invalid value for maxRegained: {}. Resetting to default.", regen.maxRegained);
			regen.maxRegained = 1;
		}
	}
}
