package com.mrbysco.unhealthydying.platform;

import com.mrbysco.unhealthydying.config.EnumHealthSetting;
import com.mrbysco.unhealthydying.config.UnhealthyConfigNeoForge;
import com.mrbysco.unhealthydying.platform.services.IPlatformHelper;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public int getMinimumHealth() {
        return UnhealthyConfigNeoForge.SERVER.minimumHealth.get();
    }

    @Override
    public int getHealthPerDeath() {
        return UnhealthyConfigNeoForge.SERVER.healthPerDeath.get();
    }

    @Override
    public boolean isReducedHealthMessageEnabled() {
        return UnhealthyConfigNeoForge.SERVER.reducedHealthMessage.get();
    }

    @Override
    public EnumHealthSetting getHealthSetting() {
        return UnhealthyConfigNeoForge.SERVER.healthSetting.get();
    }

    @Override
    public boolean isRegenHealthEnabled() {
        return UnhealthyConfigNeoForge.SERVER.regenHealth.get();
    }

    @Override
    public int getMaxRegained() {
        return UnhealthyConfigNeoForge.SERVER.maxRegained.get();
    }

    @Override
    public boolean isRegenHealthMessageEnabled() {
        return UnhealthyConfigNeoForge.SERVER.regenHealthMessage.get();
    }

    @Override
    public List<? extends String> getRegenTargets() {
        return UnhealthyConfigNeoForge.SERVER.regenTargets.get();
    }

    @Override
    public boolean isPlayer(Entity entity) {
        return !(entity instanceof FakePlayer);
    }

    @Override
    public void setAmountData(Player player, String customTag, int targetAmount, int healthGained) {
        CompoundTag playerData = player.getPersistentData();

        if (playerData.contains(customTag)) {
            int currentAmount = playerData.getInt(customTag);
            if ((currentAmount + 1) >= targetAmount) {
                switch (Services.PLATFORM.getHealthSetting()) {
                    case EVERYBODY -> UnhealthyHelper.setEveryonesHealth(player, healthGained);
                    case SCOREBOARD_TEAM -> UnhealthyHelper.setScoreboardHealth(player, healthGained);
                    default -> UnhealthyHelper.setHealth(player, healthGained);
                }
                playerData.remove(customTag);
            } else {
                playerData.putInt(customTag, currentAmount + 1);
            }
        } else {
            playerData.putInt(customTag, 1);
        }
    }
}
