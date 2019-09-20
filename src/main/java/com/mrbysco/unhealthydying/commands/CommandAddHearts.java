package com.mrbysco.unhealthydying.commands;

import java.util.List;

import com.mrbysco.unhealthydying.util.UnhealthyHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandAddHearts extends CommandBase{

	@Override
	public String getName() {
		return "addhearts";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.unhealthydying.addhearts.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2)
        {
            throw new WrongUsageException("commands.unhealthydying.addhearts.usage", new Object[0]);
        }
		else
		{
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            Integer hearts = Integer.valueOf(args[1]);
            if(hearts == null)
            {
                throw new NumberInvalidException("commands.unhealthydying.addhearts.invalid.number", new Object[] {args[1]});
            }
            
            boolean flag = hearts != 0;
            if(flag)
            {
            	setHealth(entityplayer, hearts);
            }
            else
            {
                throw new NumberInvalidException("commands.unhealthydying.addhearts.invalid.number", new Object[] {args[1]});
            }
            
            notifyCommandListener(sender, this, "commands.unhealthydying.addhearts.success", new Object[] {(double)hearts/2, entityplayer.getName()});
		}
	}
	
	public static void setHealth(EntityPlayer player, int amount)
	{
    	UnhealthyHelper.SetHealth(player, amount);
		
		ITextComponent text = new TextComponentTranslation("unhealthydying:addhearts.message", new Object[] {(double)amount/2});
		text.getStyle().setColor(TextFormatting.RED);
		player.sendStatusMessage(text, true);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
		else return super.getTabCompletions(server, sender, args, targetPos);
	}
	
}
