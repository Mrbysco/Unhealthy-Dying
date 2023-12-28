package com.mrbysco.unhealthydying;

import com.mojang.logging.LogUtils;
import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class UnhealthyDying {
	public static final Logger LOGGER = LogUtils.getLogger();

	public UnhealthyDying(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(Type.SERVER, UnhealthyConfig.serverSpec);
		eventBus.register(UnhealthyConfig.class);

		NeoForge.EVENT_BUS.register(new HealthHandler());
		NeoForge.EVENT_BUS.register(new EasterEgg());

		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		UnhealthyCommands.initializeCommands(event.getDispatcher());
	}
}
