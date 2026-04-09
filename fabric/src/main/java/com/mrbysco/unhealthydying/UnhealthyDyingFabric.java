package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.neoforged.fml.config.ModConfig;

public class UnhealthyDyingFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.SERVER, UnhealthyConfig.serverSpec);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> UnhealthyCommands.initializeCommands(dispatcher));
	}
}
