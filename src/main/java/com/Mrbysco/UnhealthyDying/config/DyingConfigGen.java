package com.mrbysco.unhealthydying.config;

import com.mrbysco.unhealthydying.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID, category = "", name = "UnhealthyDying")
@Config.LangKey("unhealthydying.config.title")
public class DyingConfigGen {
	
	@Config.Comment({"General settings"})
	public static General general = new General();

	@Config.Comment({"Regen settings"})
	public static Regen regen = new Regen();
	
	@Config.Comment({"Default settings"})
	public static Default defaultSettings = new Default();
	
	public static class General{
		@Config.LangKey("configgui.unhealthydying.general.minimumHealth")
		@Config.Comment("Minimum amount of health the player can end up with (2 = 1 heart) [default: 2]")
		@Config.RangeInt(min = 1, max = 20)
		public int minimumHealth = 2;

		@Config.LangKey("configgui.unhealthydying.general.healthPerDeath")
		@Config.Comment("The amount of health taken from the player upon death (2 = 1 heart) [default: 2]")
		@Config.RangeInt(min = 1, max = 20)
		public int healthPerDeath = 2;

		@Config.LangKey("configgui.unhealthydying.general.reducedHealthMessage")
		@Config.Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public boolean reducedHealthMessage = true;

		@Config.LangKey("configgui.unhealthydying.general.HealthSetting")
		@Config.Comment({"Decides if the reduced health is per player, for everybody, per team",
			"or per FTB Team (Only works when FTBUtils is installed) [default: true]"})
		public EnumHealthSetting HealthSetting = EnumHealthSetting.SEPERATE;
	}
	
	public enum EnumHealthSetting {
		SEPERATE,
		SCOREBOARD_TEAM,
		EVERYBODY,
		FTB_TEAMS
	}
	
	public static class Regen{
		@Config.LangKey("configgui.unhealthydying.regen.regenHealth")
		@Config.Comment("When set to true allows you to gain back health upon killing set target(s) [default: false]")
		public boolean regenHealth = false;

		@Config.LangKey("configgui.unhealthydying.regen.maxRegen")
		@Config.Comment("The amount of max health the player can get from killing the target(s) (20 = 10 hearts) [default: 20]")
		@Config.RangeInt(min = 1)
		public int maxRegenned = 20;

		@Config.LangKey("configgui.unhealthydying.regen.regennedHealthMessage")
		@Config.Comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
		public boolean regennedHealthMessage = true;

		@Config.LangKey("configgui.unhealthydying.regen.regenTargets")
		@Config.Comment({"Adding lines / removing lines specifies which mobs will cause the players to regen max health",
				"Syntax: modid:mobname,healthRegenned,amount",
				"For wildcards use *. For instance [*:*,1,20] would mean every 20 kills regain half a heart",
				"While [minecraft:*,1,10] would mean every 10 kills of vanilla mobs regains half a heart"})
		public String[] regenTargets = new String[]
		{
				"minecraft:ender_dragon,4,1",
				"minecraft:wither,2,1"
		};
	}
	
	public static class Default{
		@Config.LangKey("configgui.unhealthydying.default.defaultHealth")
		@Config.Comment("The default amount of health a player starts with (Only change this if another mod changes starting health) [default: 20]")
		public int defaultHealth = 20;
	}
	
	@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
	private static class EventHandler {

		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Reference.MOD_ID)) {
				ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}
