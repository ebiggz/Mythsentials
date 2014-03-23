package com.mythicacraft.plugins.mythsentials.Weather;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class WeatherCommands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("mw")) {
			Player player = (Player) sender;
			if(args.length == 1) {
				boolean isRaining = player.getWorld().hasStorm();
				if(args[0].equalsIgnoreCase("forecast")) {
					if(isRaining) {
						sender.sendMessage(ChatColor.AQUA + "There is currently a storm in your world.");
					} else {
						sender.sendMessage(ChatColor.AQUA + "The skys are clear in your world.");
					}
				}
				else if(args[0].equalsIgnoreCase("hide")) {
					player.setPlayerWeather(WeatherType.CLEAR);
					sender.sendMessage(ChatColor.AQUA + "Hiding weather...");
				}
				else if(args[0].equalsIgnoreCase("show")) {
					if(isRaining) {
						sender.sendMessage(ChatColor.AQUA + "Showing weather... it is currently storming.");
					} else {
						sender.sendMessage(ChatColor.AQUA + "Showing weather... skys are clear!");
					}
					player.resetPlayerWeather();
				}
				else if(args[0].equalsIgnoreCase("autohide")) {
					sender.sendMessage(ChatColor.AQUA + "Please type \"/mw autohide on\" or \"/mw autohide off\" to toggle hiding of weather automatically.");
				}
				else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					sender.sendMessage(ChatColor.YELLOW + "[MythicaWeather Help]");
					sender.sendMessage(ChatColor.AQUA + "/mw forecast" + ChatColor.GRAY + " - See what the current weather actually is for the world you are in.");
					sender.sendMessage(ChatColor.AQUA + "/mw hide" + ChatColor.GRAY + " - Hide the weather.");
					sender.sendMessage(ChatColor.AQUA + "/mw show" + ChatColor.GRAY + " - Show the weather.");
					sender.sendMessage(ChatColor.AQUA + "/mw autohide on" + ChatColor.GRAY + " - Have storms automatically hidden.");
					sender.sendMessage(ChatColor.AQUA + "/mw autohide off" + ChatColor.GRAY + " - Turn off the automatic hiding of storms.");
				}
			}
			else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("autohide")) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(sender.getName());
					boolean isAlready = mythian.getAutohideWeather();
					if(args[1].equalsIgnoreCase("on")) {
						if(isAlready) {
							sender.sendMessage(ChatColor.AQUA + "You are already autohiding weather!");
						} else {
							mythian.setAutohideWeather(true);
							player.setPlayerWeather(WeatherType.CLEAR);
							sender.sendMessage(ChatColor.AQUA + "You are now autohiding weather.");
						}
					}
					else if(args[1].equalsIgnoreCase("off")) {
						if(isAlready) {
							mythian.setAutohideWeather(false);
							player.resetPlayerWeather();
							sender.sendMessage(ChatColor.AQUA + "You've turned off the autohiding of weather.");
						} else {
							sender.sendMessage(ChatColor.AQUA + "You already have autohide weather off!");
						}
					}

				}
			}
		}
		return true;
	}
}
