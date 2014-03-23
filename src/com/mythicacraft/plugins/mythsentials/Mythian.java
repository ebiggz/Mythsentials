package com.mythicacraft.plugins.mythsentials;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager.Universe;
import com.mythicacraft.plugins.mythsentials.AdminTools.PlayerDeathDrop;
import com.mythicacraft.plugins.mythsentials.Store.StoreItem;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;


public class Mythian {

	private Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Mythsentials");
	private String playerName;
	//private SpawnManager sm = MythicaSpawn.getSpawnManager();

	public Mythian(String playerName) {
		this.playerName = playerName;
		createFile(plugin.getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "players", playerName + ".yml");
	}

	public long getPlayTime() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(playerData.getConfig().contains("joinTime")) {
			String joinTime = playerData.getConfig().getString("joinTime");
			try {
				return Time.compareTimeMills(joinTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int getRunes() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getInt("runes", 0);
	}

	public void setRunes(int amount) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("runes", amount);
		playerData.saveConfig();
	}

	public boolean getAutohideWeather() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getBoolean("autohideWeather", false);
	}

	public void setAutohideWeather(boolean autohide) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("autohideWeather", autohide);
		playerData.saveConfig();
	}

	public Universe getCurrentUniverse() {
		Player p = Bukkit.getPlayer(playerName);
		if(p != null && p.isOnline()) {
			return MythicaSpawn.getSpawnManager().getWorldsUniverse(p.getWorld());
		} else {
			return null;
		}
	}
	public void saveUnclaimedItem(String playerName, String itemName) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> itemsList = playerData.getConfig().getStringList("unclaimedItems");
		itemsList.add(itemName);
		playerData.getConfig().set("unclaimedItems", itemsList);
		playerData.saveConfig();
	}

	public void removeUnclaimedItem(String playerName, String itemName) {

		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> itemsList = playerData.getConfig().getStringList("unclaimedItems");
		itemsList.remove(itemName);
		playerData.getConfig().set("unclaimedItems", itemsList);
		playerData.saveConfig();

	}

	public List<StoreItem> getUnclaimedItems(String playerName) {

		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		ConfigAccessor storeCfg = new ConfigAccessor("mythica-store.yml");

		List<String> itemsList = new ArrayList<String>();
		itemsList = playerData.getConfig().getStringList("unclaimedItems");

		List<StoreItem> items = new ArrayList<StoreItem>();

		ConfigurationSection cs = storeCfg.getConfig().getConfigurationSection("Items");

		if(cs != null) {
			for(String itemName : itemsList) {
				ConfigurationSection itemOptions = cs.getConfigurationSection(itemName);
				if (itemOptions != null) {
					items.add(new StoreItem(itemName, itemOptions));
				} else {
					removeUnclaimedItem(playerName, itemName);
				}
			}
		}
		return items;
	}

	public int getUnclaimedItemCount(String playerName) {
		List<String> itemsList = new ArrayList<String>();

		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		itemsList = playerData.getConfig().getStringList("unclaimedItems");

		return itemsList.size();
	}

	public List<PlayerDeathDrop> getDeathDrops() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		ConfigurationSection cs = playerData.getConfig().getConfigurationSection("lastDeathDrops");
		List<PlayerDeathDrop> dropsList = new ArrayList<PlayerDeathDrop>();
		if(cs != null) {
			for(String deathDrop: cs.getKeys(false)) {
				ConfigurationSection deathDropData = playerData.getConfig().getConfigurationSection("lastDeathDrops." + deathDrop);
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

	public void addNewDeathDrop(PlayerDeathDrop playerDeathDrop) {
		List<PlayerDeathDrop> allDeathDrops = getDeathDrops();
		allDeathDrops.add(0, playerDeathDrop);
		while(allDeathDrops.size() >= 10) {
			allDeathDrops.remove(allDeathDrops.size()-1);
		}
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		for(int i = 0; i < allDeathDrops.size(); i++) {
			int dropNum = i + 1;
			playerData.getConfig().set("lastDeathDrops." + dropNum + ".Drops", allDeathDrops.get(i).getDrops());
			if(allDeathDrops.get(i).hasArmor()) {
				playerData.getConfig().set("lastDeathDrops." + dropNum + ".Armor", allDeathDrops.get(i).getArmor());
			}
			playerData.getConfig().set("lastDeathDrops."  + dropNum + ".Time", allDeathDrops.get(i).getDeathTime());
			playerData.getConfig().set("lastDeathDrops." + dropNum + ".Location", allDeathDrops.get(i).getDeathLoc());
			playerData.getConfig().set("lastDeathDrops."  + dropNum + ".World", allDeathDrops.get(i).getWorld());
		}
		playerData.saveConfig();
	}

	public void setLastDeathLoc(Location death) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		String deathLoc = Integer.toString((int) death.getX()) + "," + Integer.toString((int) death.getY()) + "," + Integer.toString((int) death.getZ()) + "," + death.getWorld().getName();
		playerData.getConfig().set("lastDeathLoc", deathLoc);
		playerData.saveConfig();
	}

	public String getLastDeathLocStr() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getString("lastDeathLoc", "");
	}

	public Location getLastDeathLoc() {
		String deathLoc = getLastDeathLocStr();
		if(deathLoc.isEmpty()) return null;
		String[] points = deathLoc.split(",");
		World deathWorld = Bukkit.getWorld(points[3]);
		Location death = new Location(deathWorld, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
		return death;
	}

	public void setLogoffBalance(double balance) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("logoffBalance", balance);
		playerData.saveConfig();
	}

	public double getLogoffBalance() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(playerData.getConfig().contains("logoffBalance")) {
			return playerData.getConfig().getDouble("logoffBalance", 0.0);
		} else {
			return -1;
		}
	}

	public void setJoinTime(String time) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("joinTime", time);
		playerData.saveConfig();
	}

	private void createFile(String path, String fileName) {
		(new File(path)).mkdirs();
		File file = new File(path + File.separator + fileName);

		if (!file.exists()) {
			try {
				file.createNewFile();
				System.out.println("[Mythsentials] Created \"" + fileName + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
