package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class HealthUtil {
	/**
	 * Sends a message to the player when their health is changed
	 *
	 * @param player    The player to send the message to
	 * @param newHealth The new health the player has
	 * @param gained    The amount of health gained
	 */
	public static void sendHealthMessage(Player player, int newHealth, int gained) {
		if (gained >= 0 && Services.PLATFORM.isRegenHealthMessageEnabled()) {
			MutableComponent text = Component.translatable("unhealthydying:regennedHealth.message", newHealth).withStyle(ChatFormatting.DARK_GREEN);
			player.displayClientMessage(text, true);
		} else {
			if (Services.PLATFORM.isReducedHealthMessageEnabled()) {
				MutableComponent text = Component.translatable("unhealthydying:reducedHealth.message", newHealth).withStyle(ChatFormatting.DARK_RED);
				player.displayClientMessage(text, true);
			}
		}
	}
}
