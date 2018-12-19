package com.Mrbysco.UnhealthyDying.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandUDTree extends CommandTreeBase{

	public CommandUDTree() {
		super.addSubcommand(new CommandSetHearts());
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
