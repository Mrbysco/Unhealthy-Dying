package com.mrbysco.unhealthydying.util;

import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class HealthUtil {
    public static void sendHealthMessage(PlayerEntity player, int newHealth, int gained) {
        if(gained > 0 && UnhealthyConfig.SERVER.regenHealthMessage.get()) {
            ITextComponent text = new TranslationTextComponent("unhealthydying:regennedHealth.message", newHealth).applyTextStyle(TextFormatting.DARK_GREEN);
            player.sendStatusMessage(text, true);
        } else {
            if(UnhealthyConfig.SERVER.reducedHealthMessage.get()) {
                ITextComponent text = new TranslationTextComponent("unhealthydying:reducedHealth.message", newHealth).applyTextStyle(TextFormatting.DARK_RED);
                player.sendStatusMessage(text, true);
            }
        }
    }
}
