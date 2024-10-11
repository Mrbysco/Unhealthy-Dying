package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfigFabric;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class UnhealthyDyingFabric implements ModInitializer {
	public static ConfigHolder<UnhealthyConfigFabric> config;

	@Override
	public void onInitialize() {
		config = AutoConfig.register(UnhealthyConfigFabric.class, Toml4jConfigSerializer::new);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UnhealthyCommands.initializeCommands(dispatcher));
	}
}
