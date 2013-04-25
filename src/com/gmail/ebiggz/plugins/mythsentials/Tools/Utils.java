package com.gmail.ebiggz.plugins.mythsentials.Tools;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;

public class Utils {

	private static final Logger log = Logger.getLogger("Minecraft");

	public static Mythsentials plugin;

	public Utils(Mythsentials plugin) {
		Utils.plugin = plugin;
	}

	public static Boolean modsOnline() {
		log.info("[Mythsentials] Checking for mods...");
		for(Player mod: plugin.getServer().getOnlinePlayers()) {
			log.info("[Mythsentials] Looking...");
			if(mod.hasPermission("mythica.helpreceive")) {
				log.info("[Mythsentials] Mod found!");
				return true;
			}
		}
		return false;
	}

	public static void modMessage(Player player) {
		if(modsOnline() == true) {
			player.sendMessage(ChatColor.GOLD + "Moderators are " + ChatColor.GREEN + "Online" + ChatColor.GOLD + "! Type /helpme if you need us.");
		} else {
			player.sendMessage(ChatColor.GOLD + "Moderators are " + ChatColor.RED + "Offline" + ChatColor.GOLD + ". If you need help, make an Issue on the website for staff to review as soon as they get on.");
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
		String playerName = player.getDisplayName();
		Double balance = Mythsentials.economy.getBalance(playerName);
		if(plugin.getConfig().contains("PlayerData." + playerName + ".LogOffBal") == false) {
			return;
		}
		Double previousBal = plugin.getConfig().getDouble("PlayerData." + playerName + ".LogOffBal");
		if(balance.equals(previousBal)) {
			player.sendMessage(ChatColor.YELLOW + "Your bank balance did not change since you last logged off.");
			return;
		}
		if(balance < previousBal) {
			double difference = previousBal - balance;
			player.sendMessage(ChatColor.YELLOW + "Your bank balance fell " + ChatColor.RED + "-$" + difference + ChatColor.YELLOW + " since you last logged off.");
			return;
		}
		if(balance > previousBal) {
			double difference = balance - previousBal;
			player.sendMessage(ChatColor.YELLOW + "Your bank balance grew " + ChatColor.GREEN + "$" + difference + ChatColor.YELLOW + " since you last logged off.");
			return;
		}
	}
}
