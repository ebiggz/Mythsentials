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
import com.mythicacraft.plugins.mythsentials.CmdExecutors.AdminTools;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.CmdProcessor;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.CmdProperties;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.CompassTarget;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.DragonChecker;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.HelpMe;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.HelpMenu;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.PetCmds;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.Registration;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.ResTool;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.TestCmds;
import com.mythicacraft.plugins.mythsentials.Listeners.BedrockBlocker;
import com.mythicacraft.plugins.mythsentials.Listeners.BoatListener;
import com.mythicacraft.plugins.mythsentials.Listeners.ChannelChat;
import com.mythicacraft.plugins.mythsentials.Listeners.ChatListener;
import com.mythicacraft.plugins.mythsentials.Listeners.ColoredSignText;
import com.mythicacraft.plugins.mythsentials.Listeners.CommandAliases;
import com.mythicacraft.plugins.mythsentials.Listeners.DragonListener;
import com.mythicacraft.plugins.mythsentials.Listeners.GroupChange;
import com.mythicacraft.plugins.mythsentials.Listeners.InvincibleTools;
import com.mythicacraft.plugins.mythsentials.Listeners.NoFallDamage;
import com.mythicacraft.plugins.mythsentials.Listeners.PetSelectListener;
import com.mythicacraft.plugins.mythsentials.Listeners.PlayerListener;
import com.mythicacraft.plugins.mythsentials.Listeners.UnleashListener;
import com.mythicacraft.plugins.mythsentials.Listeners.UnregNotifier;
import com.mythicacraft.plugins.mythsentials.Tools.AppCommuncatior;
import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.HerochatJSONHandler;
import com.mythicacraft.plugins.mythsentials.Tools.JsonStream;
import com.mythicacraft.plugins.mythsentials.Tools.ResidenceJSONHandler;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;

public class Mythsentials extends JavaPlugin {

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

		if(!setupVault()) {
			pm.disablePlugin(this);
			return;
		}
		Plugin jsonAPI = this.getServer().getPluginManager().getPlugin("JSONAPI");
		if(jsonAPI == null) {
			pm.disablePlugin(this);
		} else {
			jsonapi = (JSONAPI)jsonAPI;
		}
		loadConfig();
		loadPlayerData();
		loadDragonData();
		jsonapi.getStreamManager().registerStream("herochat", herochatStream);
		jsonapi.getStreamManager().registerStream("notifications", notificationStream);
		jsonapi.getStreamManager().registerStream("irc", ircStream);
		jsonapi.registerAPICallHandler(new HerochatJSONHandler(this));
		jsonapi.registerAPICallHandler(new ResidenceJSONHandler(this));

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

		getCommand("register").setExecutor(new Registration(this));
		getCommand("reg").setExecutor(new Registration(this));
		getCommand("confirm").setExecutor(new Registration(this));
		getCommand("cancel").setExecutor(new Registration(this));
		getCommand("helpme").setExecutor(new HelpMe(this));
		getCommand("playerinfo").setExecutor(new AdminTools());
		getCommand("deathdrops").setExecutor(new AdminTools());
		getCommand("loginlocation").setExecutor(new AdminTools());
		getCommand("compass").setExecutor(new CompassTarget(this));
		getCommand("modhelp").setExecutor(new HelpMe(this));
		getCommand("adminhelp").setExecutor(new HelpMe(this));
		getCommand("mod").setExecutor(new HelpMe(this));
		getCommand("admin").setExecutor(new HelpMe(this));
		getCommand("mods").setExecutor(new HelpMe(this));
		getCommand("admins").setExecutor(new HelpMe(this));
		getCommand("restool").setExecutor(new ResTool());
		getCommand("mythica").setExecutor(new HelpMenu());
		getCommand("mythicatest").setExecutor(new TestCmds());
		getCommand("dragon").setExecutor(new DragonChecker(this));
		getCommand("pet").setExecutor(new PetCmds());

		new Utils(this);
		new CmdProcessor();

		Thread appCommun = new AppCommuncatior();
		appCommun.start();

		log.info("[Mythsentials] Enabled!");
	}

	private boolean setupVault() {
		Plugin vault =  getServer().getPluginManager().getPlugin("Vault");
		if (vault != null && vault instanceof net.milkbowl.vault.Vault) {
			log.info("[Mythsentials] Hooked into Vault v" + vault.getDescription().getVersion());
			if(!setupEconomy()) {
				log.severe("[Mythsentials] No permissions plugin to handle cash!");
				return false;
			}
			if(!setupChat()) {
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

	public void loadConfig() {
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