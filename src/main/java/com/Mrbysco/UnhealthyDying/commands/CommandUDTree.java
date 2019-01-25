package com.mrbysco.unhealthydying.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandUDTree extends CommandTreeBase{

	public CommandUDTree() {
		super.addSubcommand(new CommandSetHearts());
		super.addSubcommand(new CommandSetMaxHealth());
		super.addSubcommand(new CommandAddHearts());
		super.addSubcommand(new CommandRemoveHearts());
	}
	
	@Override
	public String getName() {
		return "unhealthydying";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.unhealthydying.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

}
