package com.mythicacraft.plugins.mythsentials.MiscCommands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class TwitterCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("twitter")) {
			ArrayList<String> tweets = Utils.getRecentTweets();
			if(tweets.isEmpty()) {
				sender.sendMessage(ChatColor.AQUA + "[Twitter] " + ChatColor.WHITE + "There isn't any recent tweets by @MythicaCraft.");
			} else {
				sender.sendMessage(ChatColor.AQUA + "[Twitter] " + ChatColor.WHITE + "Recent Tweets:");
				int count = 1;
				for(String tweet : tweets) {
					if(count % 2 != 0) {
						sender.sendMessage(count + ") " + ChatColor.YELLOW + tweet);
					} else {
						sender.sendMessage(count + ") " + ChatColor.GOLD + tweet);
					}
					count++;
				}
			}

		}
		return true;
	}
}
