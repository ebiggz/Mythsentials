package com.gmail.ebiggz.plugins.mythsentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAliases implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {	

		return false;
	}
	
	public void executeAsPlayer(String command, String sender) {
        Player player = Bukkit.getServer().getPlayer(sender);
		if(player != null) {
			player.performCommand(command);
		}	
	}
}
