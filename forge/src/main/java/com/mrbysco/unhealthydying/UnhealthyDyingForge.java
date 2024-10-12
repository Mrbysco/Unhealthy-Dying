package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfigForge;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class UnhealthyDyingForge {

	public UnhealthyDyingForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, UnhealthyConfigForge.serverSpec);
		eventBus.register(UnhealthyConfigForge.class);

		MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::onRespawn);

		MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);
	}

	private void onLivingDeath(LivingDeathEvent event) {
		EasterEgg.killedEntityEvent(event.getEntity(), event.getSource());
	}

	private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		HealthHandler.onPlayerJoin(player);
	}

	private void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		Player player = event.getEntity();
		HealthHandler.onRespawn(player, event.isEndConquered());
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		UnhealthyCommands.initializeCommands(event.getDispatcher());
	}
}