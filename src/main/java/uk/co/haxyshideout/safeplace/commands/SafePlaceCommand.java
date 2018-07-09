package uk.co.haxyshideout.safeplace.commands;

import com.pixelmonmod.pixelmon.comm.CommandChatHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import uk.co.haxyshideout.safeplace.SafePlace;
import uk.co.haxyshideout.safeplace.config.Utilities;

public class SafePlaceCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "safeplace";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/safeplace delete:list:tp:add";
	}
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		//Could hardly be bothered with this class. I will probably make it look prettier later on.
		if(args.length == 0)
		{
			CommandChatHandler.sendChat(sender, getCommandUsage(sender), "");
			return;
		}
		if(!(sender instanceof EntityPlayer))
		{
			CommandChatHandler.sendChat(sender, "Only usable by players", "");
			return;
		}
		if(args[0].equalsIgnoreCase("add") && args.length != 3)
				CommandChatHandler.sendChat(sender, "Incorrect Usage: /SafePlace Add Name Radius", "");
		else
		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("tp"))
				SafePlace.eventHandler.teleportPlayerToNearestSafePlace((EntityPlayerMP)sender);
			else
			if(args[0].equalsIgnoreCase("list"))
				Utilities.listSafePlaces((EntityPlayer)sender);
			else
			if(args[0].equalsIgnoreCase("delete"))
				Utilities.deleteSafePlace((EntityPlayer)sender);
			else
				CommandChatHandler.sendChat(sender, getCommandUsage(sender), "");
		}
		else
		if(args.length == 3)
		{
			if(args[0].equalsIgnoreCase("add"))
			{
				Utilities.addSafePlace((EntityPlayer)sender, args[1], parseInt(args[2]));
			}
		}
		else
			CommandChatHandler.sendChat(sender, getCommandUsage(sender), "");
	}

}
