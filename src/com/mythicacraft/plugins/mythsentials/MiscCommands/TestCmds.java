package com.mythicacraft.plugins.mythsentials.MiscCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.MiscListeners.PlayerListener;

public class TestCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("pet")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("setowner")) {
					PlayerListener.giveStarterKit((Player) sender);
				}
			} else {
				//send help menu
			}
		}
		return true;
	}
}