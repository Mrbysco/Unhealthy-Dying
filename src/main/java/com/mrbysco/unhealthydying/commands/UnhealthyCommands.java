package com.mrbysco.unhealthydying.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.unhealthydying.config.UnhealthyConfig;
import com.mrbysco.unhealthydying.util.HealthUtil;
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
        root.requires((p_198721_0_) -> p_198721_0_.hasPermissionLevel(2))
                .then(Commands.literal("hearts").then(Commands.literal("add").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.addHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.addHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.removeHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("hearts").then(Commands.literal("set").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", IntegerArgumentType.integer(1)).executes((ctx) -> UnhealthyCommands.setHearts(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.setHearts(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))))
                .then(Commands.literal("max_health").then(Commands.literal("set").then(Commands.argument("player", EntityArgument.players()).then(Commands.argument("health", DoubleArgumentType.doubleArg()).executes((ctx) -> UnhealthyCommands.setMaxHealth(ctx, false)).then(Commands.argument("hideMessage", BoolArgumentType.bool()).executes((ctx) -> UnhealthyCommands.setMaxHealth(ctx, !BoolArgumentType.getBool(ctx, "hideMessage"))))))));
        dispatcher.register(root);
    }

    private static int addHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.SetHealth(player, health);

                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:addhearts.message", (double)health/2);
                    text.getStyle().setColor(TextFormatting.RED);
                    ctx.getSource().sendFeedback(text, false);
                }
            }
        }

        return 0;
    }

    private static int removeHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.SetHealth(player, -health);

                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:removehearts.message", (double)health/2);
                    text.getStyle().setColor(TextFormatting.RED);
                    ctx.getSource().sendFeedback(text, false);
                }
            }
        }

        return 0;
    }

    private static int setHearts(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                UnhealthyHelper.SetHealth(player, getModifier(health), false);

                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:sethealth.message", health);
                    text.getStyle().setColor(TextFormatting.RED);
                    ctx.getSource().sendFeedback(text, false);
                }
            }
        }

        return 0;
    }

    public static int getModifier(int amount) {
        int defaultHealth = UnhealthyConfig.SERVER.defaultHealth.get();

        return (amount - defaultHealth);
    }

    private static int setMaxHealth(CommandContext<CommandSource> ctx, boolean silent) throws CommandSyntaxException {
        final int health = IntegerArgumentType.getInteger(ctx, "health");
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            if(health > 0) {
                int modifier = getModifier(health);
                UnhealthyHelper.setModifier(player, modifier);
                HealthUtil.setMaxHealth(player, health);

                if(!silent) {
                    ITextComponent text = new TranslationTextComponent("unhealthydying:setmaxhealth.message", health);
                    text.getStyle().setColor(TextFormatting.RED);
                    ctx.getSource().sendFeedback(text, false);
                }
            }
        }

        return 0;
    }
}
