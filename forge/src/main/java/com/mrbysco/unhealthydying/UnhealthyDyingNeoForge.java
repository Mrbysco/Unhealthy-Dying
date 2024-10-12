package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.UnhealthyCommands;
import com.mrbysco.unhealthydying.config.UnhealthyConfigNeoForge;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(Constants.MOD_ID)
public class UnhealthyDyingNeoForge {

	public UnhealthyDyingNeoForge(IEventBus eventBus, ModContainer container, Dist dist) {
		container.registerConfig(ModConfig.Type.SERVER, UnhealthyConfigNeoForge.serverSpec);
		eventBus.register(UnhealthyConfigNeoForge.class);

		NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
		NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
		NeoForge.EVENT_BUS.addListener(this::onRespawn);

		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
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