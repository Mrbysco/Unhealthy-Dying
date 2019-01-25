package com.mrbysco.unhealthydying.commands;

import java.util.List;

import com.mrbysco.unhealthydying.Reference;
import com.mrbysco.unhealthydying.util.UnhealthyHelper;

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

public class CommandRemoveHearts extends CommandBase{

	@Override
	public String getName() {
		return "removehearts";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.unhealthydying.removehearts.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2)
        {
            throw new WrongUsageException("commands.unhealthydying.removehearts.usage", new Object[0]);
        }
		else
		{
            EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
            Integer hearts = Integer.valueOf(args[1]);
            if(hearts == null)
            {
                throw new NumberInvalidException("commands.unhealthydying.removehearts.invalid.number", new Object[] {args[1]});
            }
            
            boolean flag = hearts != 0;
            if(flag)
            {
            	NBTTagCompound playerData = entityplayer.getEntityData();
    			NBTTagCompound data = UnhealthyHelper.getTag(playerData, EntityPlayer.PERSISTED_NBT_TAG);
    			int currentHealth = data.getInteger(Reference.REDUCED_HEALTH_TAG);
    			int newHealth = currentHealth - hearts;
    			data.setInteger(Reference.REDUCED_HEALTH_TAG, newHealth);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
            	setHealth(entityplayer, hearts);
            }
            else
            {
                throw new NumberInvalidException("commands.unhealthydying.removehearts.invalid.number", new Object[] {args[1]});
            }
            
            notifyCommandListener(sender, this, "commands.unhealthydying.removehearts.success", new Object[] {(double)hearts/2, entityplayer.getName()});
		}
	}
	
	public static void setHealth(EntityPlayer player, int amount)
	{
		double currentHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
		double newValue = currentHealth - amount;
		player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)newValue);
		player.setHealth((int)newValue);
		
		ITextComponent text = new TextComponentTranslation("unhealthydying:removehearts.message", new Object[] {(double)amount/2});
		text.getStyle().setColor(TextFormatting.DARK_RED);
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
