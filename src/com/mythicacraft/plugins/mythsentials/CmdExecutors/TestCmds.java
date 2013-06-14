package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Listeners.PlayerListener;

public class TestCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!sender.hasPermission("mythica.test")) return false;
		if(commandLabel.equalsIgnoreCase("mythicatest")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("kit")) {
					PlayerListener.giveStarterKit((Player) sender);
				}
			}
		}
		return true;
	}
}