package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class HealthUtil {
	public static void sendHealthMessage(Player player, int newHealth, int gained) {
		if (gained >= 0 && UnhealthyConfig.SERVER.regenHealthMessage.get()) {
			MutableComponent text = Component.translatable("unhealthydying:regennedHealth.message", newHealth).withStyle(ChatFormatting.DARK_GREEN);
			player.displayClientMessage(text, true);
		} else {
			if (UnhealthyConfig.SERVER.reducedHealthMessage.get()) {
				MutableComponent text = Component.translatable("unhealthydying:reducedHealth.message", newHealth).withStyle(ChatFormatting.DARK_RED);
				player.displayClientMessage(text, true);
			}
		}
	}
}
