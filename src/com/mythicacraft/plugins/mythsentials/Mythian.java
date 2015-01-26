package com.mythicacraft.plugins.mythsentials;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager.Universe;
import com.mythicacraft.plugins.mythsentials.DeathLedger.DeathLog;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Experience;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.PackageObj;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Payment;
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

	public boolean hasLogoffXP() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().contains("logoffXP");
	}

	public int getLogoffXP() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getInt("logoffXP", 0);
	}

	public void setLogoffXP(int amount) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("logoffXP", amount);
		playerData.saveConfig();
	}

	public ConfigurationSection getInboxConfigSection() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getConfigurationSection("mailbox.inbox");
	}

	public ConfigurationSection getSentConfigSection() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getConfigurationSection("mailbox.sent");
	}

	public void savePlayerMailboxType(List<Mail> box, MailboxType mbType) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		String boxType;
		if(mbType == MailboxType.INBOX) {
			boxType = "inbox";
		} else {
			boxType = "sent";
		}
		playerData.getConfig().set("mailbox." + boxType, null);
		playerData.saveConfig();
		Collections.sort(box);
		int count = 1;
		for(Mail mail : box) {
			if(count <= 100) {
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".from", mail.getFrom());
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".to", mail.getTo());
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".type", mail.getType().toString());
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".message", mail.getMessage());
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".timeStamp", mail.getTimeStamp());
				playerData.getConfig().set("mailbox." + boxType + "." + count + ".status", mail.getStatus().toString());
				if(mail.getType() == MailType.PACKAGE) {
					playerData.getConfig().set("mailbox." + boxType + "." + count + ".items", ((PackageObj) mail).getItems());
				}
				else if(mail.getType() == MailType.PAYMENT) {
					playerData.getConfig().set("mailbox." + boxType + "." + count + ".money", ((Payment) mail).getPayment());
				}
				else if(mail.getType() == MailType.EXPERIENCE) {
					playerData.getConfig().set("mailbox." + boxType + "." + count + ".xp", ((Experience) mail).getExperience());
				}
				playerData.saveConfig();
				count++;
			} else {
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<Mail> loadPlayerMailboxType(MailboxType mbType) {

		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		String boxType;
		if(mbType == MailboxType.INBOX) {
			boxType = "inbox";
		} else {
			boxType = "sent";
		}

		ConfigurationSection boxSection = playerData.getConfig().getConfigurationSection("mailbox." + boxType);
		List<Mail> box = new ArrayList<Mail>();
		if(boxSection != null) {
			for(String mailNumber: boxSection.getKeys(false)) {
				ConfigurationSection mailData = playerData.getConfig().getConfigurationSection("mailbox." + boxType + "." + mailNumber);
				if(mailData != null) {

					String to = mailData.getString("to");
					String from = mailData.getString("from");
					MailType type = MailType.valueOf(mailData.getString("type"));
					String message = mailData.getString("message");
					String timeStamp = mailData.getString("timeStamp");
					MailStatus status = MailStatus.valueOf(mailData.getString("status"));

					switch(type) {
						case PACKAGE:
							box.add(new PackageObj(to, from, message, timeStamp, status, (List<ItemStack>) mailData.getList("items")));
							break;
						case PAYMENT:
							box.add(new Payment(to, from, message, timeStamp, status, mailData.getDouble("money")));
							break;
						case EXPERIENCE:
							box.add(new Experience(to, from, message, timeStamp, status, mailData.getInt("xp")));
							break;
						default:
							box.add(new Mail(to, from, message, timeStamp, type, status));
					}
				}
			}
			Collections.sort(box);
		}
		return box;
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getMailDropbox() {
		List<ItemStack> dropbox = new ArrayList<ItemStack>();
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(playerData.getConfig().contains("mailbox.dropbox")) {
			List<ItemStack> dropboxData = (List<ItemStack>) playerData.getConfig().getList("mailbox.dropbox");
			if(dropboxData != null && !dropboxData.isEmpty()) {
				dropbox = dropboxData;
			}
		}
		return dropbox;
	}

	public void saveMailDropbox(List<ItemStack> items) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("mailbox.dropbox", items);
		playerData.saveConfig();
	}

	public void saveMailboxLoc(String locString) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> locs = getMailboxLocs();
		locs.add(locString);
		playerData.getConfig().set("mailbox.locations", locs);
		playerData.saveConfig();
	}

	public List<String> getMailboxLocs() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().contains("mailbox.locations") ? playerData.getConfig().getStringList("mailbox.locations") : new ArrayList<String>();
	}

	public void deleteMailboxLoc(String locString) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> locs = getMailboxLocs();
		locs.remove(locString);
		playerData.getConfig().set("mailbox.locations", locs);
		playerData.saveConfig();
	}

	public void clearMailboxLocs() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("mailbox.locations", null);
		playerData.saveConfig();
	}

	public void addRecentCommand(String command) {
		List<String> commands = getRecentCommands();
		commands.add(0, command);
		while(commands.size() > 10) {
			commands.remove(commands.size()-1);
		}
		setRecentCommands(commands);
	}

	public void setRecentCommands(List<String> commands) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("recentCommands", commands);
		playerData.saveConfig();
	}

	public List<String> getRecentCommands() {
		List<String> commands = new ArrayList<String>();
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		commands = playerData.getConfig().getStringList("recentCommands");
		return commands;
	}

	public void setRunes(int amount) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("runes", amount);
		playerData.saveConfig();
	}

	public void setCensorChat(boolean shouldCensor) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("censorChat", shouldCensor);
		playerData.saveConfig();
	}

	public boolean getCensorChat() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getBoolean("censorChat", false);
	}

	public boolean getCensorChatGlobal() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getBoolean("censorChatGlobal", false);
	}

	public void setCensorChatGlobal(boolean shouldCensor) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("censorChatGlobal", shouldCensor);
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

	public List<DeathLog> getDeathLogs() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		ConfigurationSection cs = playerData.getConfig().getConfigurationSection("lastDeathDrops");
		List<DeathLog> dropsList = new ArrayList<DeathLog>();
		if(cs != null) {
			for(String deathDrop: cs.getKeys(false)) {
				ConfigurationSection deathDropData = playerData.getConfig().getConfigurationSection("lastDeathDrops." + deathDrop);
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

	public void addNewDeathLog(DeathLog playerDeathDrop) {
		List<DeathLog> allDeathDrops = getDeathLogs();
		allDeathDrops.add(playerDeathDrop);
		while(allDeathDrops.size() >= 10) {
			allDeathDrops.remove(allDeathDrops.size()-1);
		}
		Collections.sort(allDeathDrops);
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		for(int i = 0; i < allDeathDrops.size(); i++) {
			int dropNum = i + 1;
			playerData.getConfig().set("lastDeathDrops." + dropNum + ".Drops", allDeathDrops.get(i).getDrops());
			if(allDeathDrops.get(i).hasArmor()) {
				playerData.getConfig().set("lastDeathDrops." + dropNum + ".Armor", allDeathDrops.get(i).getArmor());
			}
			playerData.getConfig().set("lastDeathDrops."  + dropNum + ".Time", allDeathDrops.get(i).getDeathTime());
			playerData.getConfig().set("lastDeathDrops."  + dropNum + ".Reason", allDeathDrops.get(i).getReason());
			playerData.getConfig().set("lastDeathDrops." + dropNum + ".Location", allDeathDrops.get(i).getDeathLoc());
			playerData.getConfig().set("lastDeathDrops."  + dropNum + ".World", allDeathDrops.get(i).getWorld());
			playerData.saveConfig();
		}
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

	public List<String> getFriendList() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> friends = new ArrayList<String>();
		if(playerData.getConfig().contains("friends.list")) {
			friends = playerData.getConfig().getStringList("friends.list");
		}
		return friends;
	}

	public boolean isFriendsWith(String friend) {
		return getFriendList().contains(friend);
	}

	public void removeFriend(String friend) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> friends = this.getFriendList();
		friends.remove(friend);
		playerData.getConfig().set("friends.list", friends);
		playerData.saveConfig();
	}

	public void addFriend(String friend) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		List<String> friends = this.getFriendList();
		if(!friends.contains(friend)) {
			friends.add(friend);
		}
		playerData.getConfig().set("friends.list", friends);
		playerData.saveConfig();
	}

	public void setAcceptFriendRequests(boolean isAccepting) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		playerData.getConfig().set("friends.accept-requests", isAccepting);
		playerData.saveConfig();
	}

	public boolean isAcceptingFriendRequests() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().getBoolean("friends.accept-requests", true);
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

	public boolean hasNewLoginLoc() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(playerData.getConfig().contains("newLoginLoc")) return true;
		return false;
	}

	public Location getNewLoginLoc() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(playerData.getConfig().contains("newLoginLoc")) {
			String[] points = playerData.getConfig().getString("newLoginLoc").split(",");
			World world = Bukkit.getWorld(points[3]);
			return new Location(world, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
		}
		return null;
	}

	public void setNewLoginLoc(Location location) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		if(location != null) {
			String loginLocStr = Integer.toString((int) location.getX()) + "," + Integer.toString((int) location.getY()) + "," + Integer.toString((int) location.getZ()) + "," + location.getWorld().getName();
			playerData.getConfig().set("newLoginLoc", loginLocStr);
		} else {
			playerData.getConfig().set("newLoginLoc", null);
		}
		playerData.saveConfig();
	}

	public Set<String> getCompassTargetNames() {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		Set<String> playerTargets = null;
		if(playerData.getConfig().contains("compassTargets")) {
			playerTargets = playerData.getConfig().getConfigurationSection("compassTargets").getKeys(false);
		}
		return playerTargets;
	}

	public Location getCompassTarget(String name) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		String targetData = playerData.getConfig().getString("compassTargets." + name, "");
		if(targetData.isEmpty()) return null;
		String[] points = targetData.split(",");
		World targetWorld = Bukkit.getWorld(points[3]);
		return new Location(targetWorld, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
	}

	public boolean hasCompassTarget(String name) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		return playerData.getConfig().contains("compassTargets." + name);
	}

	public void removeCompassTarget(String name) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		Map<String, Object> targets = null;
		targets = playerData.getConfig().getConfigurationSection("compassTargets").getValues(false);
		if (targets != null) {
			if(targets.containsKey(name)) {
				targets.remove(name);
				playerData.getConfig().set("compassTargets", targets);
				playerData.saveConfig();
			}
		}
	}

	public void saveCompassTarget(String name, Location targetLoc) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		String worldName = targetLoc.getWorld().getName();
		String newTarget = Integer.toString((int) targetLoc.getX()) + "," + Integer.toString((int) targetLoc.getY()) + "," + Integer.toString((int) targetLoc.getZ()) + "," + worldName;
		playerData.getConfig().set("compassTargets." + name, newTarget);
		playerData.saveConfig();
	}

	public void renameCompassTarget(String oldName, String newName) {
		ConfigAccessor playerData = new ConfigAccessor("players" + File.separator + playerName + ".yml");
		Map<String, Object> targets = null;
		targets = playerData.getConfig().getConfigurationSection("compassTargets").getValues(false);
		if (targets != null) {
			if(targets.containsKey(oldName)) {
				targets.put(newName, targets.get(oldName));
				targets.remove(oldName);
				playerData.getConfig().set(playerName + ".compassTargets", targets);
				playerData.saveConfig();
			}
		}
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
