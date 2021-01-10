package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class UnhealthyDying {
	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

	public UnhealthyDying() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, UnhealthyConfig.serverSpec);
		eventBus.register(UnhealthyConfig.class);

		MinecraftForge.EVENT_BUS.register(new HealthHandler());
		MinecraftForge.EVENT_BUS.register(new EasterEgg());

		MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
	}

	public void onCommandRegister(FMLServerStartingEvent event) {
		UnhealthyCommands.initializeCommands(event.getCommandDispatcher());
	}
}
