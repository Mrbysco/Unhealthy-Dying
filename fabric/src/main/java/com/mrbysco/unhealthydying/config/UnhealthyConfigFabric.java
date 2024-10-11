package com.mrbysco.unhealthydying.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;

@Config(name = com.mrbysco.unhealthydying.Constants.MOD_ID)
public class UnhealthyConfigFabric implements ConfigData {
	@ConfigEntry.Gui.CollapsibleObject
	public final General general = new General();

	@ConfigEntry.Gui.CollapsibleObject
	public final Regen regen = new Regen();

	public static class General {
		@Comment("Minimum amount of health the player can end up with (2 = 1 heart) [default: 2]")
		public final int minimumHealth = 2;

		@Comment("The amount of health taken from the player upon death (2 = 1 heart) [default: 2]")
		public final int healthPerDeath = 2;

		@Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public final boolean reducedHealthMessage = true;

		@Comment("Decides if the reduced health is per player, for everybody or per team [default: SEPARATE]")
		public final EnumHealthSetting healthSetting = EnumHealthSetting.SEPARATE;
	}

	public static class Regen {
		@Comment("When set to true allows you to gain back health upon killing set target(s) [default: false]")
		public final boolean regenHealth = false;

		@Comment("The amount of max health the player can get from killing the target(s) (20 = 10 hearts) [default: 20]")
		public final int maxRegained = 20;

		@Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public final boolean regenHealthMessage = true;

		@Comment("""
				Adding lines / removing lines specifies which mobs will cause the players to regen max health
				Syntax: modid:mobname,healthRegenned,amount
				For wildcards use *. For instance [*:*,1,20] would mean every 20 kills regain half a heart
				While [minecraft:*,1,10] would mean every 10 kills of vanilla mobs regains half a heart""")
		public final List<String> regenTargets = Arrays.asList(
				"minecraft:ender_dragon,4,1",
				"minecraft:wither,2,1"
		);
	}
}
