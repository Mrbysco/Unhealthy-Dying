package com.mrbysco.unhealthydying.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class UnhealthyCommands {
    public static void initializeCommands (CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> root = Commands.literal("unhealthydying");
        root.requires((p_198721_0_) -> p_198721_0_.hasPermission(2))
                .then(Commands.literal("hearts").then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.addHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.addHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("set").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.setHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.setHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))));
        dispatcher.register(root);
    }

    private static int addHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                if (health > 0 ) {
                    float playerHealth = player.getMaxHealth();
                    UnhealthyHelper.setHealth(player, (int)UnhealthyHelper.getModifierForAmount(player,playerHealth + health), false);
                    if (!silent) {
                        ITextComponent text = new TranslationTextComponent("unhealthydying:addhearts.message", (double) health / 2).withStyle(TextFormatting.RED);
                        ctx.getSource().sendSuccess(text, false);
                    }
                }
            }
        return 0;
    }

    private static int removeHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {

                float playerHealth = player.getMaxHealth();
                UnhealthyHelper.setHealth(player, (int)UnhealthyHelper.getModifierForAmount(player,playerHealth - health), false);
                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:removehearts.message", (double)health/2).withStyle(TextFormatting.RED);
                    ctx.getSource().sendSuccess(text, false);
                }
            }
        }

        return 0;
    }

    private static int setHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.setHealth(player, (int)UnhealthyHelper.getModifierForAmount(player, health), false);

                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:sethealth.message", health).withStyle(TextFormatting.RED);
                    ctx.getSource().sendSuccess(text, false);
                }
            }
        }

        return 0;
    }
}
