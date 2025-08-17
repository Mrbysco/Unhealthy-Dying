package com.mrbysco.unhealthydying.mixin;

import com.mrbysco.unhealthydying.util.IPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements IPersistentData {
	@Unique
	private CompoundTag persistentData;

	@Override
	public CompoundTag unhealthydying$getPersistentData() {
		if (persistentData == null) {
			persistentData = new CompoundTag();
		}

		return persistentData;
	}

	@Inject(method = "saveWithoutId", at = @At("HEAD"))
	public void unhealthydying$saveWithoutId(ValueOutput valueOutput, CallbackInfo ci) {
		if (persistentData != null) {
			valueOutput.store("unhealthydying.entity_data", CompoundTag.CODEC, persistentData);
		}
	}

	@Inject(method = "load", at = @At("HEAD"))
	public void unhealthydying$load(ValueInput input, CallbackInfo ci) {
		if (input.contains("unhealthydying.entity_data")) {
			persistentData = input.read("unhealthydying.entity_data", CompoundTag.CODEC).orElse(new CompoundTag());
		}
	}
}
