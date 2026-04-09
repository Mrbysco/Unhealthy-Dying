package com.mrbysco.unhealthydying;

import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

	public static final String MOD_ID = "unhealthydying";
	public static final String MOD_PREFIX = MOD_ID + ":";
	public static final String MOD_NAME = "Unhealthy Dying";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final Identifier HEALTH_MODIFIER_ID = modLoc("health_modifier");

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}