package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jibble.pircbot.Colors;



public class IRCCommands implements CommandExecutor {


	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		IRCBot bot = IRCBot.getBot();
		if(commandLabel.equalsIgnoreCase("irc")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("users")) {
					sender.sendMessage(ChatColor.GOLD + "Online IRC Users:");
					sender.sendMessage(IRCUtils.getIrcUserList());
					return true;
				}
				else if(twitterStyleCheck(args[0])) {
					String recipitent = args[0].replaceFirst("@", "");
					String ircUser = IRCUtils.completeIRCNick(recipitent);
					if(ircUser != null) {
						if(args.length == 1) {
							sender.sendMessage(ChatColor.RED + "Please include a message to send to the irc user!");
						} else {
							String message = "";
							for(int i = 1; i < args.length; i++){ //Combine arguments into one string
								message += " " + args[i];
							}
							message = ChatColor.stripColor(message.substring(1));
							bot.sendMessage(ircUser, Colors.MAGENTA + "From " + sender.getName() + ": " + message);
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "To " + ircUser + "(IRC): " + message);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "\"" + recipitent + "\" is not a vaild online irc user. Please check /irc users");
					}
				}
			}
		}
		return true;
	}

	public static boolean twitterStyleCheck(String args) {
		Pattern checkRegex = Pattern.compile("@(\\S)");
		Matcher regexMatcher = checkRegex.matcher(args);
		if(regexMatcher.find()) return true;
		return false;
	}
}
