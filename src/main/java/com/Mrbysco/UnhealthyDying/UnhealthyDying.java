package com.Mrbysco.UnhealthyDying;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Mrbysco.UnhealthyDying.commands.CommandUDTree;
import com.Mrbysco.UnhealthyDying.handlers.EasterEgg;
import com.Mrbysco.UnhealthyDying.handlers.HealthHandler;
import com.Mrbysco.UnhealthyDying.proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

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
