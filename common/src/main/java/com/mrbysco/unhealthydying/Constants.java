package com.mrbysco.unhealthydying;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

	public static final String MOD_ID = "unhealthydying";
	public static final String MOD_PREFIX = MOD_ID + ":";
	public static final String MOD_NAME = "Unhealthy Dying";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "health_modifier");
}