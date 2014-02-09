package com.mythicacraft.plugins.mythsentials.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.AdminTools.PlayerDeathDrop;

public class Utils {


	private static Mythsentials plugin;

	public Utils(Mythsentials plugin) {
		Utils.plugin = plugin;
	}

	public static Boolean modsOnline() {
		for(Player player: plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("mythica.helpreceive")) {
				return true;
			}
		}
		return false;
	}

	public static String getTargetName(Player sender) {
		Location currentTar = sender.getCompassTarget();
		String playerName = sender.getName();
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		SpawnManager sm = MythicaSpawn.getSpawnManager();

		if(currentTar == sm.getPlayerSurvivalBed(sender)) {
			return "Survival Bed";
		}

		if(currentTar == sm.getPlayerPvpBed(sender)) {
			return "Pvp Bed";
		}

		if(currentTar == sm.getPvpSpawn()) {
			return "PvP Spawn";
		}

		if(currentTar == sm.getSurvivalSpawn()) {
			return "Survival Spawn";
		}

		if(plugin.playerTrackers.containsKey(sender)) {
			if(playerData.getConfig().contains(playerName + ".playerTrack")) {
				String playerTrack = playerData.getConfig().getString(playerName + ".playerTrack");
				return playerTrack;
			}
		}
		if(playerData.getConfig().contains(playerName + ".lastDeathLoc")) {
			String[] points = playerData.getConfig().getString(playerName + ".lastDeathLoc").split(",");
			World deathWorld = Bukkit.getWorld(points[3]);
			Location death = new Location(deathWorld, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
			if(currentTar == death) {
				return "Last Death";
			}
		}

		Set<String> targets = null;
		if (playerData.getConfig().contains(playerName + ".compassTargets")) {
			targets = playerData.getConfig().getConfigurationSection(playerName + ".compassTargets").getKeys(false);
			if (targets != null) {
				for (String targetName : targets) {
					if(targetName != null) {
						String[] points = playerData.getConfig().getString(playerName + ".compassTargets." + targetName).split(",");
						if(points.length >= 4) {
							World targetWorld = Bukkit.getWorld(points[3]);
							Location targetLoc = new Location(targetWorld, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
							if(currentTar == targetLoc) {
								return targetName;
							}
						}
					}
				}
			}
		}
		return "Unknown";
	}


	public static void modMessage(Player player) {
		if(!player.hasPermission("mythica.registered")) return;
		String yMod = ChatColor.GOLD + "Our staff is " + ChatColor.GREEN + "available" + ChatColor.GOLD + "! Type /helpme if you need us.";
		String nMod = ChatColor.GOLD + "Our staff is " + ChatColor.RED + "unavailable" + ChatColor.GOLD + ". If you need help, make an Issue on the website for staff to review as soon as they get on.";
		if(modsOnline()) {
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
		Double balance = Mythsentials.economy.getBalance(playerName);
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

	public static List<PlayerDeathDrop> getPlayerDeathDrops(String playerName) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		ConfigurationSection cs = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops");
		List<PlayerDeathDrop> dropsList = new ArrayList<PlayerDeathDrop>();
		if(cs != null) {
			for(String deathDrop: cs.getKeys(false)) {
				ConfigurationSection deathDropData = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops." + deathDrop);
				if(deathDropData != null) {
					dropsList.add(new PlayerDeathDrop(playerName, deathDropData));
				}
			}
			while(dropsList.size() >= 10) {
				dropsList.remove(dropsList.size()-1);
			}
			Collections.sort(dropsList);
		}
		return dropsList;
	}

	public static String getDeathDropItemList(List<ItemStack> dropItems) {
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
	public static int getEnchantXpWorth(Enchantment enchant, int level) {
		if(enchant == Enchantment.LOOT_BONUS_MOBS) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.SILK_TOUCH) {
			return 30/2;
		}
		else if(enchant == Enchantment.DAMAGE_ALL) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.DAMAGE_ARTHROPODS) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.DAMAGE_UNDEAD) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.KNOCKBACK) {
			if(level == 1) {
				return 15/2;
			}
			if(level == 2) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_ENVIRONMENTAL) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_EXPLOSIONS) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_FALL) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 20/2;
			}
			if(level == 4) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_FIRE) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_PROJECTILE) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.OXYGEN) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.WATER_WORKER) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.THORNS) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.FIRE_ASPECT) {
			if(level == 1) {
				return 15/2;
			}
			if(level == 2) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.DIG_SPEED) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.DURABILITY) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.LOOT_BONUS_BLOCKS) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.ARROW_DAMAGE) {
			if(level == 1) {
				return 5/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 15/2;
			}
			if(level == 4) {
				return 20/2;
			}
			if(level == 5) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.ARROW_FIRE) {
			return 30/2;
		}
		else if(enchant == Enchantment.ARROW_INFINITE) {
			return 30/2;
		}
		else if(enchant == Enchantment.ARROW_KNOCKBACK) {
			if(level == 1) {
				return 15/2;
			}
			if(level == 2) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.LUCK) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		else if(enchant == Enchantment.LURE) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 15/2;
			}
			if(level == 3) {
				return 30/2;
			}
		}
		return 0;
	}
	public static int getEnchantXpWorthSurvival(Enchantment enchant, int level) {
		if(enchant == Enchantment.PROTECTION_FALL) {
			if(level == 1) {
				return 3;
			}
			if(level == 2) {
				return 10;
			}
			if(level == 3) {
				return 20;
			}
			if(level == 4) {
				return 30;
			}
		}
		else if(enchant == Enchantment.DURABILITY) {
			if(level == 1) {
				return 3;
			}
			if(level == 2) {
				return 15;
			}
			if(level == 3) {
				return 30;
			}
		}
		return 0;
	}
	public static Enchantment getEnchantEnumFromName(String name) {
		if(name.equalsIgnoreCase("looting")) {
			return Enchantment.LOOT_BONUS_MOBS;
		}
		if(name.equalsIgnoreCase("silktouch")) {
			return Enchantment.SILK_TOUCH;
		}
		if(name.equalsIgnoreCase("sharpness")) {
			return Enchantment.DAMAGE_ALL;
		}
		if(name.equalsIgnoreCase("baneofarthropods")) {
			return Enchantment.DAMAGE_ARTHROPODS;
		}
		if(name.equalsIgnoreCase("smite")) {
			return Enchantment.DAMAGE_UNDEAD;
		}
		if(name.equalsIgnoreCase("knockback")) {
			return Enchantment.KNOCKBACK;
		}
		if(name.equalsIgnoreCase("protection")) {
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		}
		if(name.equalsIgnoreCase("fireprotection")) {
			return Enchantment.PROTECTION_FIRE;
		}
		if(name.equalsIgnoreCase("blastprotection")) {
			return Enchantment.PROTECTION_EXPLOSIONS;
		}
		if(name.equalsIgnoreCase("projectileprotection")) {
			return Enchantment.PROTECTION_PROJECTILE;
		}
		if(name.equalsIgnoreCase("featherfalling")) {
			return Enchantment.PROTECTION_FALL;
		}
		if(name.equalsIgnoreCase("respiration")) {
			return Enchantment.OXYGEN;
		}
		if(name.equalsIgnoreCase("aquaaffinity")) {
			return Enchantment.WATER_WORKER;
		}
		if(name.equalsIgnoreCase("thorns")) {
			return Enchantment.THORNS;
		}
		if(name.equalsIgnoreCase("fireaspect")) {
			return Enchantment.FIRE_ASPECT;
		}
		if(name.equalsIgnoreCase("efficiency")) {
			return Enchantment.DIG_SPEED;
		}
		if(name.equalsIgnoreCase("unbreaking")) {
			return Enchantment.DURABILITY;
		}
		if(name.equalsIgnoreCase("fortune")) {
			return Enchantment.LOOT_BONUS_BLOCKS;
		}
		if(name.equalsIgnoreCase("power")) {
			return Enchantment.ARROW_DAMAGE;
		}
		if(name.equalsIgnoreCase("punch")) {
			return Enchantment.ARROW_KNOCKBACK;
		}
		if(name.equalsIgnoreCase("flame")) {
			return Enchantment.ARROW_FIRE;
		}
		if(name.equalsIgnoreCase("infinity")) {
			return Enchantment.ARROW_INFINITE;
		}
		if(name.equalsIgnoreCase("luckofthesea")) {
			return Enchantment.LUCK;
		}
		if(name.equalsIgnoreCase("luck")) {
			return Enchantment.LUCK;
		}
		if(name.equalsIgnoreCase("lure")) {
			return Enchantment.LURE;
		}
		else {
			return null;
		}
	}
	public static String getNameFromEnchant(Enchantment enchant) {
		String name = "";
		if(enchant == Enchantment.LOOT_BONUS_MOBS) {
			name = "Looting";
		}
		else if(enchant == Enchantment.SILK_TOUCH) {
			name = "Silk Touch";
		}
		else if(enchant == Enchantment.DAMAGE_ALL) {
			name = "Sharpness";
		}
		else if(enchant == Enchantment.DAMAGE_ARTHROPODS) {
			name = "Bane of Arthropods";
		}
		else if(enchant == Enchantment.DAMAGE_UNDEAD) {
			name = "Smite";
		}
		else if(enchant == Enchantment.KNOCKBACK) {
			name = "Knockback";
		}
		else if(enchant == Enchantment.PROTECTION_ENVIRONMENTAL) {
			name = "Protection";
		}
		else if(enchant == Enchantment.PROTECTION_EXPLOSIONS) {
			name = "Blast Protection";
		}
		else if(enchant == Enchantment.PROTECTION_FALL) {
			name = "Feather Falling";
		}
		else if(enchant == Enchantment.PROTECTION_FIRE) {
			name = "Fire Protection";
		}
		else if(enchant == Enchantment.PROTECTION_PROJECTILE) {
			name = "Projectile Protection";
		}
		else if(enchant == Enchantment.OXYGEN) {
			name = "Respiration";
		}
		else if(enchant == Enchantment.WATER_WORKER) {
			name = "Aqua Affinity";
		}
		else if(enchant == Enchantment.THORNS) {
			name = "Thorns";
		}
		else if(enchant == Enchantment.FIRE_ASPECT) {
			name = "fire aspect";
		}
		else if(enchant == Enchantment.DIG_SPEED) {
			name = "Efficiency";
		}
		else if(enchant == Enchantment.DURABILITY) {
			name = "Unbreaking";
		}
		else if(enchant == Enchantment.LOOT_BONUS_BLOCKS) {
			name = "Fortune";
		}
		else if(enchant == Enchantment.ARROW_DAMAGE) {
			name = "Power";
		}
		else if(enchant == Enchantment.ARROW_FIRE) {
			name = "Flame";
		}
		else if(enchant == Enchantment.ARROW_INFINITE) {
			name = "Infinity";
		}
		else if(enchant == Enchantment.ARROW_KNOCKBACK) {
			name = "Punch";
		}
		else if(enchant == Enchantment.LUCK) {
			name = "Luck of the Sea";
		}
		else if(enchant == Enchantment.LURE) {
			name = "Lure";
		}
		return name;
	}
}
