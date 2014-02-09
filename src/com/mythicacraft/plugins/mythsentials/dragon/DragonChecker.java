package com.mythicacraft.plugins.mythsentials.Dragon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;

public class DragonChecker implements CommandExecutor {

	/*private final Mythsentials plugin;

	public DragonChecker(final Mythsentials instance) {
		this.plugin = instance;
	}
	 */

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		if(commandLabel.equalsIgnoreCase("dragon")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.YELLOW + "Survival End: ");
				if(dragonCheck("survival")) {
					sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.GREEN +"alive" + ChatColor.GOLD + "!");
				} else {
					String killer = dragonData.getConfig().getString("DragonData.survival.dragonLastKilledBy", "none");
					String dragonDeathTime = dragonData.getConfig().getString("DragonData.survival.deathTime", "0");
					if(killer.equalsIgnoreCase("none")) {
						sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ".");
					} else {
						sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ". It was last slain by " + ChatColor.YELLOW + killer + ChatColor.GOLD + "," + deathTimeMessage(dragonDeathTime));
					}
				}
				sender.sendMessage(ChatColor.YELLOW + "PvP End: ");
				if(dragonCheck("pvp")) {
					sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.GREEN +"alive" + ChatColor.GOLD + "!");
				} else {
					String killer = dragonData.getConfig().getString("DragonData.pvp.dragonLastKilledBy", "none");
					String dragonDeathTime = dragonData.getConfig().getString("DragonData.pvp.deathTime", "0");
					if(killer.equalsIgnoreCase("none")) {
						sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ".");
					} else {
						sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ". It was last slain by " + ChatColor.YELLOW + killer + ChatColor.GOLD + "," + deathTimeMessage(dragonDeathTime));
					}
				}
			}
			/*if(args.length == 1) {
				if(args[0].equalsIgnoreCase("top")) {
					ConfigurationSection cs = dragonData.getConfig().getConfigurationSection("Kills.survival");
					for(String player : cs.getKeys(false)) {

					}
				}
			}
			 */
		}
		return true;
	}

	public boolean dragonCheck(String universe){ //Checks for dragon by loading some chunks in the End and looking for dragon
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		return dragonData.getConfig().getBoolean("DragonData." + universe + ".dragonIsAlive", false);
	}

	public static String deathTimeMessage(String deathTime) { //creates a string that describes how long ago the dragon died
		int allMinutes = Time.returnMins(deathTime);
		int hours = allMinutes/60;
		int rMinutes = allMinutes - (60*hours);
		String minOrS = null;
		if(rMinutes == 1) {
			minOrS = " minute";
		}
		if(rMinutes > 1) {
			minOrS = " minutes";
		}
		if(hours < 1 ) {
			if(rMinutes < 1) {
				String message = " just moments ago.";
				return message;
			}
			String message = " " + rMinutes + minOrS + " ago.";
			return message;
		}
		if(hours == 1) {
			if(rMinutes == 0) {
				String message = " an hour ago.";
				return message;
			}
			if(rMinutes > 0) {
				String message = " 1 hour and " + rMinutes + minOrS + " ago.";
				return message;
			}
		}
		if(hours > 1) {
			if(rMinutes == 0) {
				String message = " " + hours + " hours ago.";
				return message;
			}
			if(rMinutes > 0) {
				String message = " " + hours + " hours and " + rMinutes + minOrS + " ago.";
				return message;
			}
		}
		return null;
	}
}
