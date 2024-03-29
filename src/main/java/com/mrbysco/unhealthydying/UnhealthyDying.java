package com.mrbysco.unhealthydying;

import com.mojang.logging.LogUtils;
import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class UnhealthyDying {
	public static final Logger LOGGER = LogUtils.getLogger();

	public UnhealthyDying() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, UnhealthyConfig.serverSpec);
		eventBus.register(UnhealthyConfig.class);

		MinecraftForge.EVENT_BUS.register(new HealthHandler());
		MinecraftForge.EVENT_BUS.register(new EasterEgg());

		MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		UnhealthyCommands.initializeCommands(event.getDispatcher());
	}
}
