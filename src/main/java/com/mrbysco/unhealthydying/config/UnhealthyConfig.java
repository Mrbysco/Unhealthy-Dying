package com.mrbysco.unhealthydying.config;

import com.mrbysco.unhealthydying.UnhealthyDying;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class UnhealthyConfig {
	public static class Server{
		public final IntValue minimumHealth;
		public final IntValue healthPerDeath;
		public final BooleanValue reducedHealthMessage;
		public final EnumValue<EnumHealthSetting> healthSetting;

		public final BooleanValue regenHealth;
		public final IntValue maxRegained;
		public final BooleanValue regenHealthMessage;
		public final ConfigValue<List<? extends String>> regenTargets;

		Server(ForgeConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			minimumHealth = builder
					.comment("Minimum amount of health the player can end up with (2 = 1 heart) [default: 2]")
					.defineInRange("minimumHealth", 2, 1, Integer.MAX_VALUE);

			healthPerDeath = builder
					.comment("The amount of health taken from the player upon death (2 = 1 heart) [default: 2]")
					.defineInRange("healthPerDeath", 2, 1, Integer.MAX_VALUE);

			reducedHealthMessage = builder
					.comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
					.define("reducedHealthMessage", true);

			healthSetting = builder
					.comment("Decides if the reduced health is per player, for everybody or per team [default: SEPARATE]")
					.defineEnum("healthSetting", EnumHealthSetting.SEPARATE);

			builder.pop();
			builder.comment("Regen settings")
					.push("Regen");

			regenHealth = builder
					.comment("When set to true allows you to gain back health upon killing set target(s) [default: false]")
					.define("regenHealth", false);

			maxRegained = builder
					.comment("The amount of max health the player can get from killing the target(s) (20 = 10 hearts) [default: 20]")
					.defineInRange("maxRegained", 20, 1, Integer.MAX_VALUE);

			regenHealthMessage = builder
					.comment("When set to true it notifies the player about their new max health when they respawn [default: true]")
					.define("regenHealthMessage", true);

			String[] targetArray = new String[]
					{
							"minecraft:ender_dragon,4,1",
							"minecraft:wither,2,1"
					};

			regenTargets = builder
					.comment("Adding lines / removing lines specifies which mobs will cause the players to regen max health",
							"Syntax: modid:mobname,healthRegenned,amount",
							"For wildcards use *. For instance [*:*,1,20] would mean every 20 kills regain half a heart",
							"While [minecraft:*,1,10] would mean every 10 kills of vanilla mobs regains half a heart")
					.defineList("info_messages", Arrays.asList(targetArray), o -> (o instanceof String));

			builder.pop();
		}
	}

	public static final ForgeConfigSpec serverSpec;
	public static final Server SERVER;
	static {
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
	}
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		UnhealthyDying.LOGGER.debug("Loaded Unhealthy Dying's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfig.Reloading configEvent) {
		UnhealthyDying.LOGGER.debug("Unhealthy Dying's config just got changed on the file system!");
	}
}
