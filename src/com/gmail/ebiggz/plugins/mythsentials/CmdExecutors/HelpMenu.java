package com.gmail.ebiggz.plugins.mythsentials.CmdExecutors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class HelpMenu implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("mythica")) {
				menuMessages(sender);
				return true;
		}
		return false;
	}
	public static void menuMessages(CommandSender player) {
		player.sendMessage(ChatColor.BLUE +   "---{" + ChatColor.YELLOW + "Mythsentials: Mythica's General Purpose Plugin" + ChatColor.BLUE + "}---");
		player.sendMessage(ChatColor.GOLD + "                            Help Menu");
		if(player.hasPermission("mythica.notnoob")) {
			player.sendMessage(ChatColor.GOLD + "/mods" + ChatColor.WHITE + " - Tells you if mods are online or not.");
		}
		if(player.hasPermission("mythica.sendhelp")) {
			player.sendMessage(ChatColor.GOLD + "/helpme [reason]" + ChatColor.WHITE + " - Alerts mods/admins\n  that you need help." + ChatColor.GRAY + ChatColor.ITALIC + "\n  Note: You can leave [reason] blank.");
		}
		if(player.hasPermission("mythica.restool")) {
			player.sendMessage(ChatColor.GOLD + "/restool" + ChatColor.WHITE + " - Puts the res selection tool in your hand.");	
		}
	}
}	