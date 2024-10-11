package com.mrbysco.unhealthydying.mixin;

import com.mrbysco.unhealthydying.handlers.EasterEgg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Player.class, ServerPlayer.class})
public class LivingDeathMixin {

	@Inject(method = "die", at = @At("HEAD"))
	private void unhealthydying$die(DamageSource source, CallbackInfo ci) {
		EasterEgg.killedEntityEvent((LivingEntity) (Object) this, source);
	}

}