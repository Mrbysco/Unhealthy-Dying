package com.mrbysco.unhealthydying.platform;

import com.mrbysco.unhealthydying.UnhealthyDyingFabric;
import com.mrbysco.unhealthydying.config.EnumHealthSetting;
import com.mrbysco.unhealthydying.platform.services.IPlatformHelper;
import com.mrbysco.unhealthydying.util.IPersistentData;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public int getMinimumHealth() {
		return UnhealthyDyingFabric.config.get().general.minimumHealth;
	}

	@Override
	public int getHealthPerDeath() {
		return UnhealthyDyingFabric.config.get().general.healthPerDeath;
	}

	@Override
	public boolean isReducedHealthMessageEnabled() {
		return UnhealthyDyingFabric.config.get().general.reducedHealthMessage;
	}

	@Override
	public EnumHealthSetting getHealthSetting() {
		return UnhealthyDyingFabric.config.get().general.healthSetting;
	}

	@Override
	public boolean isRegenHealthEnabled() {
		return UnhealthyDyingFabric.config.get().regen.regenHealth;
	}

	@Override
	public int getMaxRegained() {
		return UnhealthyDyingFabric.config.get().regen.maxRegained;
	}

	@Override
	public boolean isRegenHealthMessageEnabled() {
		return UnhealthyDyingFabric.config.get().regen.regenHealthMessage;
	}

	@Override
	public List<? extends String> getRegenTargets() {
		return UnhealthyDyingFabric.config.get().regen.regenTargets;
	}

	@Override
	public boolean isPlayer(Entity entity) {
		return entity instanceof FakePlayer;
	}

	@Override
	public void setAmountData(Player player, String customTag, int targetAmount, int healthGained) {
		CompoundTag playerData = ((IPersistentData) player).unhealthydying$getPersistentData();

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
