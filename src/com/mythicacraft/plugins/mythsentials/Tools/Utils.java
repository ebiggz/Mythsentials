package com.mythicacraft.plugins.mythsentials.Tools;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class Utils {


	private static Mythsentials plugin;

	public Utils(Mythsentials plugin) {
		Utils.plugin = plugin;
	}

	public static Boolean modsOnline() {
		for(Player mod: plugin.getServer().getOnlinePlayers()) {
			if(mod.hasPermission("mythica.helpreceive")) {
				return true;
			}
		}
		return false;
	}

	public static void modMessage(Player player) {
		if(!player.hasPermission("mythica.registered")) return;
		String yMod = ChatColor.GOLD + "Moderators are " + ChatColor.GREEN + "Online" + ChatColor.GOLD + "! Type /helpme if you need us.";
		String nMod = ChatColor.GOLD + "Moderators are " + ChatColor.RED + "Offline" + ChatColor.GOLD + ". If you need help, make an Issue on the website for staff to review as soon as they get on.";
		if(modsOnline() == true) {
			player.sendMessage(yMod);
		} else {
			player.sendMessage(nMod);
		}
	}

	public static void playerNotify(String permission, String message) {
		for(Player p: plugin.getServer().getOnlinePlayers()) {
			if(p.hasPermission(permission)) {
				p.sendMessage(message);
			}
		}
	}

	public static void offlineBalanceChange(Player player) {
		if(!player.hasPermission("mythica.registered")) return;
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		String playerName = player.getDisplayName();
		Double balance = plugin.economy.getBalance(playerName);
		String message = "";
		if(!playerData.getConfig().contains(playerName + ".logoffBalance")) {
			return;
		}
		Double previousBal = playerData.getConfig().getDouble(playerName + ".logoffBalance");
		if(balance.equals(previousBal)) {
			message = ChatColor.YELLOW + "Your bank balance did not change since you last logged off.";
		}
		if(balance < previousBal) {
			double difference = previousBal - balance;
			message = ChatColor.YELLOW + "Your bank balance fell " + ChatColor.RED + "-$" + difference + ChatColor.YELLOW + " since you last logged off.";
		}
		if(balance > previousBal) {
			double difference = balance - previousBal;
			message = ChatColor.YELLOW + "Your bank balance grew " + ChatColor.GREEN + "$" + difference + ChatColor.YELLOW + " since you last logged off.";
		}
		player.sendMessage(message);
	}
}
