package com.mythicacraft.plugins.mythsentials.Tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.admintools.DeathDrops;

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
		String yMod = ChatColor.GOLD + "Staff is " + ChatColor.GREEN + "available" + ChatColor.GOLD + "! Type /helpme if you need us.";
		String nMod = ChatColor.GOLD + "Staff is " + ChatColor.RED + "unavailable" + ChatColor.GOLD + ". If you need help, make an Issue on the website for staff to review as soon as they get on.";
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
			double difference = roundTwoDecimals(previousBal - balance);
			message = ChatColor.YELLOW + "Your bank balance fell " + ChatColor.RED + "-$" + difference + ChatColor.YELLOW + " since you last logged off.";
		}
		if(balance > previousBal) {
			double difference = roundTwoDecimals(balance - previousBal);
			message = ChatColor.YELLOW + "Your bank balance grew " + ChatColor.GREEN + "$" + difference + ChatColor.YELLOW + " since you last logged off.";
		}
		player.sendMessage(message);
	}

	public static List<DeathDrops> getPlayerDeathDrops(String playerName) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		ConfigurationSection cs = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops");
		List<DeathDrops> dropsList = new ArrayList<DeathDrops>();
		if(cs != null) {
			for(String deathDrop: cs.getKeys(false)) {
				ConfigurationSection deathDropData = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops." + deathDrop);
				if(deathDropData != null) {
					dropsList.add(new DeathDrops(playerName, deathDropData));
				}
			}
			while(dropsList.size() >= 10) {
				dropsList.remove(dropsList.size()-1);
			}
			Collections.sort(dropsList);
		}
		return dropsList;
	}

	public static String deathDropsItems(List<ItemStack> dropItems) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(int i = 0; i < dropItems.size(); i++) {
			ItemStack is = dropItems.get(i);
			sb.append(is.getType().toString().toLowerCase().replace("_", " "));
			if(is.getAmount() > 1) {
				sb.append("(x" + is.getAmount() + ")");
			}
			int lastIndex = dropItems.size()-1;
			if(i != lastIndex) {
				sb.append(", ");
			}
			if(count % 2 == 0) {
				sb.append(ChatColor.AQUA);
			} else {
				sb.append(ChatColor.DARK_AQUA);
			}
			count++;
		}
		return sb.toString();
	}

	public static String completeName(String playername) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for(int i = 0; i < onlinePlayers.length; i++) {
			if(onlinePlayers[i].getName().toLowerCase().startsWith(playername.toLowerCase())) {
				return onlinePlayers[i].getName();
			}
		}
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for(int i = 0; i < offlinePlayers.length; i++) {
			if(offlinePlayers[i].getName().toLowerCase().startsWith(playername.toLowerCase())) {
				return offlinePlayers[i].getName();
			}
		}
		return null;
	}
	static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
