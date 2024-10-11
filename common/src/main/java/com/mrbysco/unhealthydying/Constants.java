package com.mrbysco.unhealthydying;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Constants {

	public static final String MOD_ID = "unhealthydying";
	public static final String MOD_PREFIX = MOD_ID + ":";
	public static final String MOD_NAME = "Unhealthy Dying";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final UUID HEALTH_MODIFIER_ID = UUID.fromString("F0FFC9E3-1EF7-4DC7-A1CE-20B55192AA97");
}