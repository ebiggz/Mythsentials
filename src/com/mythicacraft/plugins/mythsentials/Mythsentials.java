package com.mythicacraft.plugins.mythsentials;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.alecgorge.minecraft.jsonapi.JSONAPI;
import com.alecgorge.minecraft.jsonapi.api.JSONAPIStream;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.HelpMe;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.HelpMenu;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.Registration;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.ResMax;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.ResTool;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.TestCmds;
import com.mythicacraft.plugins.mythsentials.Listeners.BedrockBlocker;
import com.mythicacraft.plugins.mythsentials.Listeners.BoatListener;
import com.mythicacraft.plugins.mythsentials.Listeners.ChatListener;
import com.mythicacraft.plugins.mythsentials.Listeners.ColoredSignText;
import com.mythicacraft.plugins.mythsentials.Listeners.CommandAliases;
import com.mythicacraft.plugins.mythsentials.Listeners.GroupChange;
import com.mythicacraft.plugins.mythsentials.Listeners.InvincibleTools;
import com.mythicacraft.plugins.mythsentials.Listeners.NoFallDamage;
import com.mythicacraft.plugins.mythsentials.Listeners.PlayerListener;
import com.mythicacraft.plugins.mythsentials.Listeners.UnleashListener;
import com.mythicacraft.plugins.mythsentials.Listeners.UnregNotifier;
import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;
import com.mythicacraft.plugins.mythsentials.admintools.AdminTools;
import com.mythicacraft.plugins.mythsentials.compass.CompassTarget;
import com.mythicacraft.plugins.mythsentials.dragon.DragonChecker;
import com.mythicacraft.plugins.mythsentials.dragon.DragonListener;
import com.mythicacraft.plugins.mythsentials.jsonapi.AppCommuncatior;
import com.mythicacraft.plugins.mythsentials.jsonapi.ChannelChat;
import com.mythicacraft.plugins.mythsentials.jsonapi.HerochatJSONHandler;
import com.mythicacraft.plugins.mythsentials.jsonapi.JsonStream;
import com.mythicacraft.plugins.mythsentials.jsonapi.ResidenceJSONHandler;
import com.mythicacraft.plugins.mythsentials.pets.CmdProcessor;
import com.mythicacraft.plugins.mythsentials.pets.CmdProperties;
import com.mythicacraft.plugins.mythsentials.pets.PetCmds;
import com.mythicacraft.plugins.mythsentials.pets.PetSelectListener;

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
	public final HashMap<Player, String> emailHash = new HashMap<Player, String>();
	public final HashMap<Player, String> passHash = new HashMap<Player, String>();
	public final HashMap<Player, String> taskIDHash = new HashMap<Player, String>();
	public static HashMap<Player,CmdProperties> petSelector = new HashMap<Player,CmdProperties>();
	public Economy economy = null;
	public Chat chat = null;
	private JSONAPI jsonapi;
	public static JSONAPIStream notificationStream = new JsonStream("notifications");
	public static JSONAPIStream herochatStream = new JsonStream("herochat");
	public static JSONAPIStream ircStream = new JsonStream("irc");
	public static HashSet<PrintWriter> clients = new HashSet<PrintWriter>();

	private static final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		log.info("[Mythsentials] Disabled!");
	}

	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();

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
		loadDragonData();

		//initiate json streams and api's
		jsonapi.getStreamManager().registerStream("herochat", herochatStream);
		jsonapi.getStreamManager().registerStream("notifications", notificationStream);
		jsonapi.getStreamManager().registerStream("irc", ircStream);
		jsonapi.registerAPICallHandler(new HerochatJSONHandler());
		jsonapi.registerAPICallHandler(new ResidenceJSONHandler());

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

		//register all the commands
		getCommand("register").setExecutor(new Registration(this));
		getCommand("reg").setExecutor(new Registration(this));
		getCommand("confirm").setExecutor(new Registration(this));
		getCommand("cancel").setExecutor(new Registration(this));
		getCommand("helpme").setExecutor(new HelpMe());
		getCommand("playerinfo").setExecutor(new AdminTools());
		getCommand("deathdrops").setExecutor(new AdminTools());
		getCommand("loginlocation").setExecutor(new AdminTools());
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
		getCommand("dragon").setExecutor(new DragonChecker(this));
		getCommand("pet").setExecutor(new PetCmds());

		//initiate a copy of the utils class and the cmdprocessor class (used for pet commands)
		new Utils(this);
		new CmdProcessor();

		//start thread to talk to connected desktop client
		Thread appCommun = new AppCommuncatior();
		appCommun.start();

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

	public void loadConfig() { //load main config
		try {
			reloadConfig();
			newConfig = this.getConfig();
			newConfig.options().copyDefaults(true);

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
}