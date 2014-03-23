package com.mythicacraft.plugins.mythsentials;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.alecgorge.minecraft.jsonapi.JSONAPI;
import com.alecgorge.minecraft.jsonapi.api.JSONAPIStream;
import com.mythicacraft.plugins.mythsentials.AdminTools.AdminTools;
import com.mythicacraft.plugins.mythsentials.AdminTools.MobCopyListener;
import com.mythicacraft.plugins.mythsentials.Affixer.AffixerCmds;
import com.mythicacraft.plugins.mythsentials.Announcer.AnnouncerListener;
import com.mythicacraft.plugins.mythsentials.Compass.CompassTarget;
import com.mythicacraft.plugins.mythsentials.Dragon.DragonChecker;
import com.mythicacraft.plugins.mythsentials.Dragon.DragonListener;
import com.mythicacraft.plugins.mythsentials.JsonAPI.AppCommuncatior;
import com.mythicacraft.plugins.mythsentials.JsonAPI.ChannelChat;
import com.mythicacraft.plugins.mythsentials.JsonAPI.HerochatJSONHandler;
import com.mythicacraft.plugins.mythsentials.JsonAPI.JsonStream;
import com.mythicacraft.plugins.mythsentials.JsonAPI.ResidenceJSONHandler;
import com.mythicacraft.plugins.mythsentials.MiscCommands.HelpMe;
import com.mythicacraft.plugins.mythsentials.MiscCommands.HelpMenu;
import com.mythicacraft.plugins.mythsentials.MiscCommands.Registration;
import com.mythicacraft.plugins.mythsentials.MiscCommands.RepairCmd;
import com.mythicacraft.plugins.mythsentials.MiscCommands.ResMax;
import com.mythicacraft.plugins.mythsentials.MiscCommands.ResTool;
import com.mythicacraft.plugins.mythsentials.MiscCommands.TestCmds;
import com.mythicacraft.plugins.mythsentials.MiscListeners.BedrockBlocker;
import com.mythicacraft.plugins.mythsentials.MiscListeners.BoatListener;
import com.mythicacraft.plugins.mythsentials.MiscListeners.ChatListener;
import com.mythicacraft.plugins.mythsentials.MiscListeners.ColoredSignText;
import com.mythicacraft.plugins.mythsentials.MiscListeners.CommandAliases;
import com.mythicacraft.plugins.mythsentials.MiscListeners.GroupChange;
import com.mythicacraft.plugins.mythsentials.MiscListeners.InvincibleTools;
import com.mythicacraft.plugins.mythsentials.MiscListeners.NoFallDamage;
import com.mythicacraft.plugins.mythsentials.MiscListeners.PlayerListener;
import com.mythicacraft.plugins.mythsentials.MiscListeners.UnleashListener;
import com.mythicacraft.plugins.mythsentials.MiscListeners.UnregNotifier;
import com.mythicacraft.plugins.mythsentials.Pets.PetCmdProperties;
import com.mythicacraft.plugins.mythsentials.Pets.PetCmds;
import com.mythicacraft.plugins.mythsentials.Pets.PetSelectListener;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCBot;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCCommands;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCEventListener;
import com.mythicacraft.plugins.mythsentials.Store.PurchaseHandler;
import com.mythicacraft.plugins.mythsentials.Store.StoreCommands;
import com.mythicacraft.plugins.mythsentials.Store.StoreItem;
import com.mythicacraft.plugins.mythsentials.Store.StoreManager;
import com.mythicacraft.plugins.mythsentials.Unenchant.UnenchantCmd;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.JarUtils;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;
import com.mythicacraft.plugins.mythsentials.Weather.WeatherCommands;
import com.mythicacraft.plugins.mythsentials.Weather.WeatherListener;
public class Mythsentials extends JavaPlugin {

	//ton of class and plugin vars
	FileConfiguration newConfig;
	public Integer toolRepairPoint;
	public Integer armorRepairPoint;
	public Boolean edIsAlive;
	public String edKiller;
	public HashMap<Integer, Boolean> tools;
	public HashMap<Integer, Boolean> armor;
	public HashMap<Player, BukkitTask> playerTrackers = new HashMap<Player, BukkitTask>();
	public HashMap<Player, BukkitTask> compassInfoPanels = new HashMap<Player, BukkitTask>();
	public final HashMap<Player, String> emailHash = new HashMap<Player, String>();
	public final HashMap<Player, String> passHash = new HashMap<Player, String>();
	public final HashMap<Player, String> taskIDHash = new HashMap<Player, String>();
	public static HashMap<Player,PetCmdProperties> petSelector = new HashMap<Player,PetCmdProperties>();
	public static List<Player> mobCopy = new ArrayList<Player>();
	public static HashMap<Player, Entity> copiedMob = new HashMap<Player, Entity>();

	public static Economy economy = null;
	public static Chat chat = null;
	public static Permission permission = null;
	public static boolean hasPermPlugin = true;
	public static boolean hasEconPlugin = false;

	private JSONAPI jsonapi;
	public static JSONAPIStream notificationStream = new JsonStream("notifications");
	public static JSONAPIStream herochatStream = new JsonStream("herochat");
	public static JSONAPIStream ircStream = new JsonStream("irc");
	public static HashSet<PrintWriter> clients = new HashSet<PrintWriter>();

	private long announcementInterval;
	public static boolean usePermGroupLoginAnnouncements = true;
	private static List<String> periodicAnnouncements;
	private int nextAnnouncement = 0;

	public static final Logger log = Logger.getLogger("Minecraft");
	private static JavaPlugin plugin;
	private static MythianManager mm;
	private static StoreManager sm;

	//config constants
	public boolean MESSAGE_PLAYER;
	public boolean BROADCAST_TO_SERVER;
	public boolean LOG_TO_CONSOLE;
	public List<String> BLACKLIST_PLAYERS;
	public List<String> BLACKLIST_WORLDS;

	public void onDisable() {
		log.info("[Mythsentials] Disabled!");
	}

	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();

		plugin = this;

		mm = new MythianManager();
		sm = new StoreManager(this);

		//make sure vault is installed
		if(!setupVault()) {
			//disable if not
			pm.disablePlugin(this);
			return;
		}
		//make sure jsonapi is installed
		Plugin jsonAPI = this.getServer().getPluginManager().getPlugin("JSONAPI");
		if(jsonAPI == null) {
			//disable if not
			pm.disablePlugin(this);
		} else {
			jsonapi = (JSONAPI)jsonAPI;
		}

		//load flat files
		loadConfig();
		loadPlayerData();
		loadStoreConfig();
		loadDragonData();
		loadPircbotLib();
		loadIRCLogFile();
		loadPeriodicAnnouncements();
		loadLoginAnnouncements();

		//make irc spirebot
		IRCBot.makeBot();

		//initiate json streams and api's
		jsonapi.getStreamManager().registerStream("herochat", herochatStream);
		jsonapi.getStreamManager().registerStream("notifications", notificationStream);
		jsonapi.getStreamManager().registerStream("irc", ircStream);
		jsonapi.registerAPICallHandler(new HerochatJSONHandler());
		jsonapi.registerAPICallHandler(new ResidenceJSONHandler());
		jsonapi.registerAPICallHandler(new PurchaseHandler());

		//register all the listeners
		pm.registerEvents(new UnregNotifier(), this);
		pm.registerEvents(new BoatListener(), this);
		pm.registerEvents(new ColoredSignText(), this);
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new NoFallDamage(), this);
		pm.registerEvents(new UnleashListener(), this);
		pm.registerEvents(new BedrockBlocker(this), this);
		pm.registerEvents(new InvincibleTools(this), this);
		pm.registerEvents(new DragonListener(), this);
		pm.registerEvents(new CommandAliases(this), this);
		pm.registerEvents(new GroupChange(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new PetSelectListener(), this);
		pm.registerEvents(new ChannelChat(), this);
		pm.registerEvents(new IRCEventListener(), this);
		pm.registerEvents(new AnnouncerListener(), this);
		pm.registerEvents(new MobCopyListener(), this);
		pm.registerEvents(new WeatherListener(), this);

		//register all the commands
		getCommand("register").setExecutor(new Registration(this));
		getCommand("reg").setExecutor(new Registration(this));
		getCommand("confirm").setExecutor(new Registration(this));
		getCommand("cancel").setExecutor(new Registration(this));
		getCommand("helpme").setExecutor(new HelpMe());
		getCommand("playerinfo").setExecutor(new AdminTools());
		getCommand("deathdrops").setExecutor(new AdminTools());
		getCommand("loginlocation").setExecutor(new AdminTools());
		getCommand("mobselect").setExecutor(new AdminTools());
		getCommand("mobtp").setExecutor(new AdminTools());
		getCommand("compass").setExecutor(new CompassTarget(this));
		getCommand("modhelp").setExecutor(new HelpMe());
		getCommand("adminhelp").setExecutor(new HelpMe());
		getCommand("mod").setExecutor(new HelpMe());
		getCommand("admin").setExecutor(new HelpMe());
		getCommand("mods").setExecutor(new HelpMe());
		getCommand("admins").setExecutor(new HelpMe());
		getCommand("restool").setExecutor(new ResTool());
		getCommand("mythica").setExecutor(new HelpMenu());
		getCommand("spirebot").setExecutor(new HelpMenu());
		getCommand("resmax").setExecutor(new ResMax());
		getCommand("mythicatest").setExecutor(new TestCmds());
		getCommand("dragon").setExecutor(new DragonChecker());
		getCommand("pet").setExecutor(new PetCmds());
		getCommand("affixer").setExecutor(new AffixerCmds());
		getCommand("colors").setExecutor(new AffixerCmds());
		getCommand("formats").setExecutor(new AffixerCmds());
		getCommand("prefix").setExecutor(new AffixerCmds());
		getCommand("suffix").setExecutor(new AffixerCmds());
		getCommand("color").setExecutor(new AffixerCmds());
		getCommand("colornames").setExecutor(new AffixerCmds());
		getCommand("colorname").setExecutor(new AffixerCmds());
		getCommand("irc").setExecutor(new IRCCommands());
		getCommand("mythicarepair").setExecutor(new RepairCmd());
		getCommand("runes").setExecutor(new UnenchantCmd(this));
		getCommand("rune").setExecutor(new UnenchantCmd(this));
		getCommand("unenchant").setExecutor(new UnenchantCmd(this));
		getCommand("mythicastore").setExecutor(new StoreCommands(this));
		getCommand("mw").setExecutor(new WeatherCommands());

		//initiate the utils class
		new Utils(this);

		//start thread to talk to connected desktop client
		Thread appCommun = new AppCommuncatior();
		appCommun.start();

		//start announcements schedule
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if(periodicAnnouncements != null && !periodicAnnouncements.isEmpty()) {
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', periodicAnnouncements.get(nextAnnouncement)));
					nextAnnouncement++;
					if(nextAnnouncement > periodicAnnouncements.size()-1) {
						nextAnnouncement = 0;
					}
				}
			}
		}, announcementInterval, announcementInterval);

		//finished
		log.info("[Mythsentials] Enabled!");
	}

	private boolean setupVault() {
		Plugin vault =  getServer().getPluginManager().getPlugin("Vault");
		if (vault != null && vault instanceof net.milkbowl.vault.Vault) { //first check that vault exists
			log.info("[Mythsentials] Hooked into Vault v" + vault.getDescription().getVersion());
			if(!setupEconomy()) { //check for econ plugin
				log.severe("[Mythsentials] No permissions plugin to handle cash!");
				return false;
			}
			if(!setupChat()) { //check for plugin to handle chat stuff
				log.severe("[Mythsentials] No chat plugin to handle prefix/suffix!");
				return false;
			}
			if(!setupPermission()) { //check for plugin to handle permissons
				log.severe("[Mythsentials] No chat plugin to handle permissons!");
				return false;
			}
		} else {
			log.severe("[Mythsentials] Vault plugin not found!");
			return false;
		}
		return true;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
			hasEconPlugin = true;
		}
		return (economy != null);
	}

	private boolean setupChat()
	{
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	public boolean setupPermission() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public void loadConfig() { //load main config
		try {
			reloadConfig();
			newConfig = this.getConfig();
			newConfig.options().copyDefaults(true);

			//get announcement data
			announcementInterval = getConfig().getLong("Announcer.Minutes_Between_Periodic_Announcements")*1200;
			usePermGroupLoginAnnouncements = getConfig().getBoolean("Announcer.Use_Permission_Group_Login_Announcements");
			log.info("Time between accouncements (in minutes): " + announcementInterval/1200);

			toolRepairPoint = newConfig.getInt("InvincibleStuff.toolRepairPoint", 99);
			armorRepairPoint = newConfig.getInt("InvincibleStuff.armorRepairPoint", 1);
			edIsAlive = newConfig.getBoolean("DragonData.dragonIsAlive");
			edKiller = newConfig.getString("DragonData.dragonLastKilledBy");

			// Load tools that are invincible. Convert to integers.
			tools = new HashMap<Integer, Boolean>();
			String[] tmp = newConfig.getString("InvincibleStuff.Tools", "276,277,278,279,293,261,359").split(",");
			for (String tool : tmp) {
				if (tool.equals("")) continue;
				tools.put(Integer.parseInt(tool), true);
			}
			// Load invincible armor
			armor = new HashMap<Integer, Boolean>();
			tmp = newConfig.getString("InvincibleStuff.Armor", "302,303,304,305").split(",");
			for (String arm : tmp) {
				if (arm.equals("")) continue;
				armor.put(Integer.parseInt(arm), true);
			}
			saveConfig();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while loading Mythsentials/config.yml", e);
		}
	}

	public void loadPlayerData() {

		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		String pluginFolder = this.getDataFolder().getAbsolutePath() + File.separator + "data";	(new File(pluginFolder)).mkdirs();
		File moneyTracConfigF = new File(pluginFolder + File.separator + "players.yml");

		if (!moneyTracConfigF.exists()) {
			log.info("No players.yml, making one now...");
			playerData.saveDefaultConfig();
			log.info("Done!");
			return;
		}
		log.info("players.yml detected!");
	}

	public void loadStoreConfig() {

		ConfigAccessor storeData = new ConfigAccessor("mythica-store.yml");
		String pluginFolder = this.getDataFolder().getAbsolutePath() + File.separator + "data";	(new File(pluginFolder)).mkdirs();
		File storeDataF = new File(pluginFolder + File.separator + "mythica-store.yml");

		if (!storeDataF.exists()) {
			log.info("No mythica-store.yml, making one now...");
			storeData.saveDefaultConfig();
			log.info("Done!");
		} else {
			log.info("mythica-store.yml detected!");
		}
		storeData.reloadConfig();

		MESSAGE_PLAYER = storeData.getConfig().getBoolean("message-player");

		BROADCAST_TO_SERVER = storeData.getConfig().getBoolean("broadcast-to-server");

		LOG_TO_CONSOLE = storeData.getConfig().getBoolean("log-to-console");

		BLACKLIST_PLAYERS = storeData.getConfig().getStringList("blacklisted-players");

		BLACKLIST_WORLDS = storeData.getConfig().getStringList("blacklisted-worlds");

		loadStoreItems();

	}

	public void loadStoreItems() {
		ConfigAccessor storeData = new ConfigAccessor("mythica-store.yml");
		sm.clearItems();
		ConfigurationSection cs = storeData.getConfig().getConfigurationSection("Items");
		if(cs != null) {
			for(String itemName : cs.getKeys(false)) {
				ConfigurationSection itemOptions = cs.getConfigurationSection(itemName);
				if (itemOptions != null) {
					StoreItem newItem = new StoreItem(itemName, itemOptions);
					sm.addItem(newItem);
					System.out.println("[Mythsentials] Added Store Item: " + itemName);
					continue;
				}
				log.warning("[Mythsentials] The store item \"" + itemName + "\" is empty! Skipping item.");
			}
		} else {
			log.warning("[Mythsentials] Your store item section is empty, no items will be given!");
		}
	}


	public void loadDragonData() {

		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		String pluginFolder = this.getDataFolder().getAbsolutePath() + File.separator + "data";	(new File(pluginFolder)).mkdirs();
		File dragonDataF = new File(pluginFolder + File.separator + "dragon.yml");

		if (!dragonDataF.exists()) {
			log.info("No dragon.yml, making one now...");
			dragonData.saveDefaultConfig();
			log.info("Done!");
			return;
		}
		log.info("dragon.yml detected!");
	}

	public void loadPircbotLib() {
		try {
			final File[] libs = new File[] {
					new File(getDataFolder() + File.separator + "data" + File.separator + "libs", "pircbot.jar")};
			for (final File lib : libs) {
				if (!lib.exists()) {
					JarUtils.extractFromJar(lib.getName(),
							lib.getAbsolutePath());
				}
			}
			for (final File lib : libs) {
				if (!lib.exists()) {
					getLogger().warning(
							"There was a critical error loading Mythsentials! Could not find lib: "
									+ lib.getName());
					Bukkit.getServer().getPluginManager().disablePlugin(this);
					return;
				}
				addClassPath(JarUtils.getJarUrl(lib));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	private void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			final Method method = sysclass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url
					+ " to system classloader");
		}
	}
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	void loadIRCLogFile() {
		try {
			File chesterFile = new File(getDataFolder() + File.separator + "data", "irc.log");
			if (!chesterFile.exists()) {
				chesterFile.createNewFile();
			}
		} catch (IOException ioe) {
		}
	}
	public static void writeLog(String message) {
		File chesterFile = new File(plugin.getDataFolder() + File.separator + "data", "irc.log");
		try {
			FileWriter fw = new FileWriter(chesterFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(getTime() + ": " + message + "\n");
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		Date dateO= cal.getTime();
		DateFormat df = new SimpleDateFormat("h:mm a");
		String date = df.format(dateO);
		return date;
	}
	public static void loadPeriodicAnnouncements() {
		ConfigAccessor periodicAnnouncementsAccess = new ConfigAccessor("announcer" + File.separator + "Periodic Announcements.yml");
		String pluginFolder = plugin.getDataFolder().getAbsolutePath();	(new File(pluginFolder)).mkdirs();
		File announcementsFile = new File(pluginFolder + File.separator + "data" + File.separator + "announcer" + File.separator + "Periodic Announcements.yml");

		if (!announcementsFile.exists()) {
			log.info("No Periodic Announcements.yml, making one now...");
			periodicAnnouncementsAccess.saveDefaultConfig();
			log.info("Done!");
		} else {
			log.info("Periodic Announcements.yml detected!");
		}
		periodicAnnouncements = periodicAnnouncementsAccess.getConfig().getStringList("Announcements");

		log.info("Number of announcements: " + periodicAnnouncements.size());

	}

	private void loadLoginAnnouncements() {
		String pluginFolderPath = this.getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "announcer" + File.separator + "Login Announcements";
		createFile(pluginFolderPath,"everyone.txt");
		if(hasPermPlugin) {
			for(String group: permission.getGroups()) {
				createFile(pluginFolderPath + File.separator + "Permission Groups", group + ".txt");
			}
		}
	}

	private void createFile(String path, String fileName) {
		(new File(path)).mkdirs();
		File file = new File(path + File.separator + fileName);

		if (!file.exists()) {
			try {
				file.createNewFile();
				log.info("Created \"" + fileName + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static MythianManager getMythianManager() {
		return mm;
	}

	public static StoreManager getStoreManager() {
		return sm;
	}
}