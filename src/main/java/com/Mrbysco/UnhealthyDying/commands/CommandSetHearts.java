package com.Mrbysco.UnhealthyDying.commands;

import java.util.List;

import com.Mrbysco.UnhealthyDying.Reference;
import com.Mrbysco.UnhealthyDying.util.UnhealthyHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
            	NBTTagCompound playerData = entityplayer.getEntityData();
    			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
    			data.setInteger(Reference.REDUCED_HEALTH_TAG, (int)health);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
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
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)amount);
		player.setHealth(amount);
		
		ITextComponent text = new TextComponentTranslation("unhealthydying:sethealth.message", new Object[] {amount});
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
