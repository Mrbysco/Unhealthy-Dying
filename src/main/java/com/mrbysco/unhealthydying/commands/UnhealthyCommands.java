package com.mrbysco.unhealthydying.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class UnhealthyCommands {
    public static void initializeCommands (CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("unhealthydying");
        root.requires((p_198721_0_) -> p_198721_0_.hasPermission(2))
                .then(Commands.literal("hearts").then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.addHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.addHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("set").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.setHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.setHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))));
        dispatcher.register(root);
    }

    private static int addHearts(CommandContext<CommandSourceStack> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.setHealth(player, health);

                if(!silent) {
                    Component text = new TranslatableComponent("unhealthydying:addhearts.message", (double)health/2).withStyle(ChatFormatting.RED);
                    ctx.getSource().sendSuccess(text, false);
                }
            }
        }

        return 0;
    }

    private static int removeHearts(CommandContext<CommandSourceStack> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.setHealth(player, -health);

                if(!silent) {
                    Component text = new TranslatableComponent("unhealthydying:removehearts.message", (double)health/2).withStyle(ChatFormatting.RED);
                    ctx.getSource().sendSuccess(text, false);
                }
            }
        }

        return 0;
    }

    private static int setHearts(CommandContext<CommandSourceStack> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.setHealth(player, (int)UnhealthyHelper.getModifierForAmount(player, health), false);

                if(!silent) {
                    Component text = new TranslatableComponent("unhealthydying:sethealth.message", health).withStyle(ChatFormatting.RED);
                    ctx.getSource().sendSuccess(text, false);
                }
            }
        }

        return 0;
    }
}
