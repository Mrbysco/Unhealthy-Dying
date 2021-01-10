package com.mrbysco.unhealthydying.commands;

import com.mrbysco.unhealthydying.config.DyingConfigGen;
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

import java.util.List;

public class CommandSetHearts extends CommandBase{

	@Override
	public String getName() {
		return "sethearts";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.unhealthydying.sethearts.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2)
        {
            throw new WrongUsageException("commands.unhealthydying.sethearts.usage", new Object[0]);
        }
		else
		{
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            Integer health = Integer.valueOf(args[1]);
            if(health == null)
            {
                throw new NumberInvalidException("commands.unhealthydying.sethearts.invalid.number", new Object[] {args[1]});
            }
            
            boolean flag = health != 0;
            if(flag)
            {
            	setHealth(entityplayer, health);
            }
            else
            {
                throw new NumberInvalidException("commands.unhealthydying.sethearts.invalid.number", new Object[] {args[1]});
            }
            
            notifyCommandListener(sender, this, "commands.unhealthydying.sethearts.success", new Object[] {entityplayer.getName(), health});
		}
	}
	
	public static void setHealth(EntityPlayer player, int amount)
	{
		UnhealthyHelper.SetHealth(player, getModifier(amount), false);
		
		ITextComponent text = new TextComponentTranslation("unhealthydying:sethealth.message", new Object[] {amount});
		text.getStyle().setColor(TextFormatting.RED);
		player.sendStatusMessage(text, true);
	}
	
	public static int getModifier(int amount) {
		int defaultHealth = DyingConfigGen.defaultSettings.defaultHealth;
		int modifierAmount = amount;
		
		return (modifierAmount - defaultHealth);
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
