package com.mrbysco.unhealthydying.mixin;

import com.mrbysco.unhealthydying.handlers.HealthHandler;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At("RETURN"))
	private void unhealthydying$placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie listenerCookie, CallbackInfo ci) {
		HealthHandler.onPlayerJoin(serverPlayer);
	}

	@Inject(method = "respawn", at = @At("RETURN"))
	private void unhealthydying$respawn(ServerPlayer serverPlayer, boolean endConquered, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
		HealthHandler.onRespawn(cir.getReturnValue(), endConquered);
	}
}
