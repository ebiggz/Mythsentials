package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class MeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("mystuff")) {
			sender.sendMessage(ChatColor.YELLOW + "[Mythica] Functioning command coming soon! ;)");
			//show ME GUI
		}

		return true;
	}

}
