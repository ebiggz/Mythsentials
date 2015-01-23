package com.mythicacraft.plugins.mythsentials.Utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Censor.CensoredWord;
import com.mythicacraft.plugins.mythsentials.DeathLedger.DeathLog;

public class Utils {


	private static Mythsentials plugin;

	public Utils(Mythsentials plugin) {
		Utils.plugin = plugin;
	}

	public static void reloadPexSmartly(Player player) {
		if(!Mythsentials.permissionsReloaded.contains(player)) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex reload");
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(!Mythsentials.permissionsReloaded.contains(p)) {
					Mythsentials.permissionsReloaded.add(p);
				}
			}
		}
	}

	public static void messagePlayerIfOnline(String playerName, String message) {
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()) {
			player.sendMessage(message);
		}
	}

	public static boolean playerHasArmor(Player player) {
		ItemStack[] contents = player.getInventory().getArmorContents();
		for(ItemStack item : contents) {
			if(item != null && item.getType() != Material.AIR) {
				return true;
			}
		}
		return false;
	}


	public static String getLastTweet() {
		Twitter twitter = Mythsentials.getTwitter();
		List<Status> statuses;
		try {
			Paging paging = new Paging(1, 25);
			statuses = twitter.getUserTimeline(paging);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		for (Status status : statuses) {
			if(status.getText().startsWith("@")) continue;
			return status.getText();
		}
		return "";
	}

	public static ArrayList<String> getRecentTweets() {
		Twitter twitter = Mythsentials.getTwitter();
		List<Status> statuses;
		try {
			Paging paging = new Paging(1, 25);
			statuses = twitter.getUserTimeline(paging);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<String>();
		}
		ArrayList<String> notMentions = new ArrayList<String>();
		for (Status status : statuses) {
			if(status.getText().startsWith("@")) continue;
			notMentions.add(status.getText());
			if(notMentions.size() >= 5) break;
		}
		return notMentions;
	}

	public static Boolean modsOnline() {
		for(Player player: plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("mythica.helpreceive")) {
				try {
					if(VanishNoPacket.isVanished(player.getName())) continue; } catch (VanishNotLoadedException e) {}
				return true;
			}
		}
		return false;
	}
	public static int getPlayerOpenInvSlots(Player player) {
		Inventory inv = player.getInventory();
		ItemStack[] contents = inv.getContents();
		int count = 0;
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null)
				count++;
		}
		return count;
	}
	public static boolean worldIsBlacklisted(String worldName) {
		ConfigAccessor storeData = new ConfigAccessor("mythica-store.yml");
		List<String> blacklistStr = storeData.getConfig().getStringList("blacklisted-worlds");
		if(blacklistStr.contains(worldName)) return true;
		return false;
	}
	public static String worldsString(List<String> worlds) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < worlds.size(); i++) {
			sb.append(worlds.get(i));
			int lastIndex = worlds.size()-1;
			if(i < lastIndex-1) {
				sb.append(", ");
			}
			if(i == lastIndex-1) {
				sb.append(", and ");
			}
		}
		return sb.toString();
	}

	public static Color getColorEnumFromName(String name) {
		if(name.equalsIgnoreCase("aqua")) {
			return Color.AQUA;
		}
		if(name.equalsIgnoreCase("black")) {
			return Color.BLACK;
		}
		if(name.equalsIgnoreCase("blue")) {
			return Color.BLUE;
		}
		if(name.equalsIgnoreCase("fuchsia")) {
			return Color.FUCHSIA;
		}
		if(name.equalsIgnoreCase("gray")) {
			return Color.GRAY;
		}
		if(name.equalsIgnoreCase("green")) {
			return Color.GREEN;
		}
		if(name.equalsIgnoreCase("lime")) {
			return Color.LIME;
		}
		if(name.equalsIgnoreCase("maroon")) {
			return Color.MAROON;
		}
		if(name.equalsIgnoreCase("navy")) {
			return Color.NAVY;
		}
		if(name.equalsIgnoreCase("olive")) {
			return Color.OLIVE;
		}
		if(name.equalsIgnoreCase("orange")) {
			return Color.ORANGE;
		}
		if(name.equalsIgnoreCase("purple")) {
			return Color.PURPLE;
		}
		if(name.equalsIgnoreCase("red")) {
			return Color.RED;
		}
		if(name.equalsIgnoreCase("silver")) {
			return Color.SILVER;
		}
		if(name.equalsIgnoreCase("teal")) {
			return Color.TEAL;
		}
		if(name.equalsIgnoreCase("white")) {
			return Color.WHITE;
		}
		if(name.equalsIgnoreCase("yellow")) {
			return Color.YELLOW;
		}
		else {
			return null;
		}
	}
	public static String getTargetName(Player sender) {

		Location currentTar = sender.getCompassTarget();
		String playerName = sender.getName();
		SpawnManager sm = MythicaSpawn.getSpawnManager();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		if(currentTar.equals(sm.getPlayerSurvivalBed(sender))) {
			return "Survival Bed";
		}

		if(currentTar.equals(sm.getPlayerPvpBed(sender))) {
			return "Pvp Bed";
		}

		if(currentTar.equals(sm.getPvpSpawn())) {
			return "PvP Spawn";
		}

		if(currentTar.equals(sm.getSurvivalSpawn())) {
			return "Survival Spawn";
		}

		Player trackingPlayer = Utils.getTrackingPlayer(sender);
		if(trackingPlayer != null) {
			return trackingPlayer.getName();
		}

		Location lastDeath = mythian.getLastDeathLoc();
		if(lastDeath != null) {
			if(currentTar.equals(lastDeath)) {
				return "Last Death";
			}
		}

		Set<String> targets = mythian.getCompassTargetNames();

		if (targets != null) {
			for (String targetName : targets) {
				if(targetName != null) {
					if(currentTar.equals(mythian.getCompassTarget(targetName))) {
						return targetName;
					}
				}
			}
		}

		return "Spawn";

	}

	public static Player getTrackingPlayer(Player tracker) {
		Set<Player> targets = plugin.playerTargets.keySet();
		for(Player target : targets) {
			if(plugin.playerTargets.get(target).hasTracker(tracker)) {
				return target;
			}
		}
		return null;
	}




	public static void modMessage(Player player) {
		if(!player.hasPermission("mythica.registered")) return;
		String yMod = ChatColor.GREEN + "[Mythica] " + ChatColor.YELLOW + "Our staff is " + ChatColor.GREEN + "available" + ChatColor.YELLOW + "! Type /helpme if you need us.";
		String nMod = ChatColor.GREEN + "[Mythica] " + ChatColor.YELLOW + "Our staff is " + ChatColor.RED + "unavailable" + ChatColor.YELLOW + ". If you need help, make an Issue on the website for staff to review as soon as they get on.";
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

	public static void offlineBalanceChangeCheck(Player player) {
		if(!player.hasPermission("mythica.registered")) return;

		String playerName = player.getName();
		Double balance = Mythsentials.economy.getBalance(playerName);
		String message = "";

		Double previousBal = Mythsentials.getMythianManager().getMythian(playerName).getLogoffBalance();
		if(previousBal == -1) {
			return;
		}
		else if(balance.equals(previousBal)) {
			message = ChatColor.GREEN + "[Mythica] " + ChatColor.YELLOW + "Your bank balance did not change since you last logged off.";
		}
		else if(balance < previousBal) {
			double difference = roundTwoDecimals(previousBal - balance);
			message = ChatColor.GREEN + "[Mythica] " + ChatColor.YELLOW + "Your bank balance fell " + ChatColor.RED + "-$" + difference + ChatColor.YELLOW + " to " + ChatColor.GOLD + balance + ChatColor.YELLOW + " since you last logged off.";
		}
		else if(balance > previousBal) {
			double difference = roundTwoDecimals(balance - previousBal);
			message = ChatColor.GREEN + "[Mythica] " + ChatColor.YELLOW + "Your bank balance grew " + ChatColor.GREEN + "$" + difference + ChatColor.YELLOW + " to " + ChatColor.GOLD + balance + ChatColor.YELLOW + " since you last logged off.";
		}
		player.sendMessage(message);
	}

	public static List<DeathLog> getPlayerDeathDrops(String playerName) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		ConfigurationSection cs = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops");
		List<DeathLog> dropsList = new ArrayList<DeathLog>();
		if(cs != null) {
			for(String deathDrop: cs.getKeys(false)) {
				ConfigurationSection deathDropData = playerData.getConfig().getConfigurationSection(playerName + ".lastDeathDrops." + deathDrop);
				if(deathDropData != null) {
					dropsList.add(new DeathLog(playerName, deathDropData));
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
	public static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
	public static int getEnchantXpWorth(Enchantment enchant, int level) {
		if(enchant == Enchantment.LOOT_BONUS_MOBS) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 2;
			}
		}
		else if(enchant == Enchantment.SILK_TOUCH) {
			return 2;
		}
		else if(enchant == Enchantment.DAMAGE_ALL) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.DAMAGE_ARTHROPODS) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.DAMAGE_UNDEAD) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.KNOCKBACK) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
		}
		else if(enchant == Enchantment.PROTECTION_ENVIRONMENTAL) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.PROTECTION_EXPLOSIONS) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.PROTECTION_FALL) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
		}
		else if(enchant == Enchantment.PROTECTION_FIRE) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.PROTECTION_PROJECTILE) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
			if(level == 5) {
				return 3;
			}
		}
		else if(enchant == Enchantment.OXYGEN) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 2;
			}
		}
		else if(enchant == Enchantment.WATER_WORKER) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 2;
			}
		}
		else if(enchant == Enchantment.THORNS) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
		}
		else if(enchant == Enchantment.FIRE_ASPECT) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
		}
		else if(enchant == Enchantment.DIG_SPEED) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 1;
			}
			if(level == 5) {
				return 1;
			}
		}
		else if(enchant == Enchantment.DURABILITY) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
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
				return 10/2;
			}
			if(level == 3) {
				return 25/2;
			}
		}
		else if(enchant == Enchantment.LURE) {
			if(level == 1) {
				return 7/2;
			}
			if(level == 2) {
				return 10/2;
			}
			if(level == 3) {
				return 25/2;
			}
		}
		return 0;
	}
	public static int getEnchantXpWorthSurvival(Enchantment enchant, int level) {
		if(enchant == Enchantment.PROTECTION_FALL) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 1;
			}
			if(level == 3) {
				return 1;
			}
			if(level == 4) {
				return 2;
			}
		}
		else if(enchant == Enchantment.DURABILITY) {
			if(level == 1) {
				return 1;
			}
			if(level == 2) {
				return 2;
			}
			if(level == 3) {
				return 3;
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

	public static String censorString(String input) {
		for(CensoredWord badWord : Mythsentials.censoredWords) {
			Pattern p = Pattern.compile(badWord.getRegexPattern());
			Matcher m = p.matcher(input);
			input = m.replaceAll(badWord.getReplacement());
		}
		return input;
	}

	public static String censorStringFunny(String input) {
		for(CensoredWord badWord : Mythsentials.censoredWordsFunny) {
			Pattern p = Pattern.compile(badWord.getRegexPattern());
			Matcher m = p.matcher(input);
			input = m.replaceAll(badWord.getReplacement());
		}
		return input;
	}
}
