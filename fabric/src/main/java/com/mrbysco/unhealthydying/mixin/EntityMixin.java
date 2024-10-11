package com.mrbysco.unhealthydying.mixin;

import com.mrbysco.unhealthydying.util.IPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	public void unhealthydying$saveWithoutId(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> cir) {
		if (persistentData != null) {
			compoundTag.put("unhealthydying.entity_data", persistentData);
		}
	}

	@Inject(method = "load", at = @At("HEAD"))
	public void unhealthydying$load(CompoundTag compoundTag, CallbackInfo ci) {
		if (compoundTag.contains("unhealthydying.entity_data")) {
			persistentData = compoundTag.getCompound("unhealthydying.entity_data");
		}
	}
}
