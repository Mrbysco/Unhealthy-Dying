package com.mrbysco.unhealthydying;

import com.mrbysco.unhealthydying.commands.CommandUDTree;
import com.mrbysco.unhealthydying.config.DyingConfigGen;
import com.mrbysco.unhealthydying.handlers.EasterEgg;
import com.mrbysco.unhealthydying.handlers.HealthHandler;
import com.mrbysco.unhealthydying.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, 
	name = Reference.MOD_NAME, 
	version = Reference.VERSION, 
	acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS,
	dependencies = Reference.DEPENDENCIES)

public class UnhealthyDying {
	@Instance(Reference.MOD_ID)
	public static UnhealthyDying instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
		logger.info("Registering config");
		MinecraftForge.EVENT_BUS.register(new DyingConfigGen());

		proxy.PreInit();
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event)
	{
		logger.info("Registering event handlers");
		MinecraftForge.EVENT_BUS.register(new HealthHandler());
		MinecraftForge.EVENT_BUS.register(new EasterEgg());
		
		proxy.Init();
    }
	
	@EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
		proxy.PostInit();
    }
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
    {
		event.registerServerCommand(new CommandUDTree());
    }
}
