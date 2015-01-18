package com.mythicacraft.plugins.mythsentials.Censor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class CensorCommands implements CommandExecutor {


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("censorchat")) {
			if(args.length == 0) {
				if(sender.hasPermission("mythica.censorchat.self")) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(sender.getName());
					if(mythian.getCensorChat()) {
						mythian.setCensorChat(false);
						sender.sendMessage(ChatColor.AQUA + "You have uncensored chat locally.");
					} else {
						mythian.setCensorChat(true);
						sender.sendMessage(ChatColor.AQUA + "You have censored chat locally.");
					}
				}
			}
			else if(args.length > 0) {
				if(!sender.hasPermission("mythica.mod")) {
					sender.sendMessage(ChatColor.RED + "Invalid censor command.");
					return true;
				}
				if(args[0].equalsIgnoreCase("on") ||
						args[0].equalsIgnoreCase("off")) {
					if(sender.isOp()){
						if(args[0].equalsIgnoreCase("on")) {
							Mythsentials.shouldCensor = true;
							sender.sendMessage(ChatColor.AQUA + "Chat censoring enabled.");
						} else {
							Mythsentials.shouldCensor = false;
							sender.sendMessage(ChatColor.AQUA + "Chat censoring disabled.");
						}
						return true;
					}
				}
				String otherPlayer = Utils.completeName(args[0]);
				if(otherPlayer == null) {
					sender.sendMessage(ChatColor.RED + "Can't find that player.");
					return true;
				}
				Mythian otherMythian = Mythsentials.getMythianManager().getMythian(otherPlayer);
				if(args.length == 1) {
					if(otherMythian.getCensorChatGlobal()) {
						otherMythian.setCensorChatGlobal(false);
						sender.sendMessage(ChatColor.AQUA + "You have uncensored chat from " + otherPlayer + ".");
					} else {
						otherMythian.setCensorChatGlobal(true);
						sender.sendMessage(ChatColor.AQUA + "You have censored chat from " + otherPlayer + ".");
					}
				}
				else if(args.length == 2) {
					if(args[1].equalsIgnoreCase("to")) {
						if(otherMythian.getCensorChat()) {
							otherMythian.setCensorChat(false);
							sender.sendMessage(ChatColor.AQUA + "You have uncensored chat to " + otherPlayer + ".");
						} else {
							otherMythian.setCensorChat(true);
							sender.sendMessage(ChatColor.AQUA + "You have censored chat to " + otherPlayer + ".");
						}
					}
					else if(args[1].equalsIgnoreCase("from")) {
						if(otherMythian.getCensorChatGlobal()) {
							otherMythian.setCensorChatGlobal(false);
							sender.sendMessage(ChatColor.AQUA + "Chat from " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is now uncensored.");
						} else {
							otherMythian.setCensorChatGlobal(true);
							sender.sendMessage(ChatColor.AQUA + "Chat from " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is now censored.");
						}
					}
					else if(args[1].equalsIgnoreCase("info")) {
						if(otherMythian.getCensorChatGlobal()) {
							sender.sendMessage(ChatColor.AQUA + "Chat from " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is censored.");
						} else {
							sender.sendMessage(ChatColor.AQUA + "Chat from " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is uncensored.");
						}
						if(otherMythian.getCensorChat()) {
							sender.sendMessage(ChatColor.AQUA + "Chat to " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is censored.");
						} else {
							sender.sendMessage(ChatColor.AQUA + "Chat to " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " is uncensored.");
						}
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Invalid censor command.");
			}
		}
		return true;
	}
}
