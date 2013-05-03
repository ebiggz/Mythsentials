package com.gmail.ebiggz.plugins.mythsentials;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.ebiggz.plugins.mythsentials.CmdExecutors.DragonChecker;
import com.gmail.ebiggz.plugins.mythsentials.CmdExecutors.HelpMe;
import com.gmail.ebiggz.plugins.mythsentials.CmdExecutors.HelpMenu;
import com.gmail.ebiggz.plugins.mythsentials.CmdExecutors.ResTool;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.BedrockBlocker;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.ColoredSignText;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.DragonListener;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.InvincibleTools;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.NoFallDamage;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.PlayerJoinQuit;
import com.gmail.ebiggz.plugins.mythsentials.Listeners.UnregNotifier;
import com.gmail.ebiggz.plugins.mythsentials.Tools.ConfigAccessor;
import com.gmail.ebiggz.plugins.mythsentials.Tools.Utils;

public class Mythsentials extends JavaPlugin {

	FileConfiguration newConfig;
	public Integer toolRepairPoint;
	public Integer armorRepairPoint;
	public Boolean edIsAlive;
	public String edKiller;
	public HashMap<Integer, Boolean> tools;
	public HashMap<Integer, Boolean> armor;
	public Economy economy = null;

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
		loadConfig();
		moneyTracConfigLoad();
		pm.registerEvents(new UnregNotifier(), this);
		pm.registerEvents(new ColoredSignText(), this);
		pm.registerEvents(new PlayerJoinQuit(this), this);
		pm.registerEvents(new NoFallDamage(), this);
		pm.registerEvents(new BedrockBlocker(this), this);
		pm.registerEvents(new InvincibleTools(this), this);
		pm.registerEvents(new DragonListener(this), this);
		getCommand("helpme").setExecutor(new HelpMe(this));
		getCommand("modhelp").setExecutor(new HelpMe(this));
		getCommand("adminhelp").setExecutor(new HelpMe(this));
		getCommand("mod").setExecutor(new HelpMe(this));
		getCommand("admin").setExecutor(new HelpMe(this));
		getCommand("mods").setExecutor(new HelpMe(this));
		getCommand("admins").setExecutor(new HelpMe(this));
		getCommand("restool").setExecutor(new ResTool());
		getCommand("mythica").setExecutor(new HelpMenu());
		getCommand("dragon").setExecutor(new DragonChecker(this));
		new Utils(this);
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
	public void moneyTracConfigLoad() {

		ConfigAccessor moneyTracConfig = new ConfigAccessor("OfflineMoneyTracking.yml");
		String pluginFolder = this.getDataFolder().getAbsolutePath();	(new File(pluginFolder)).mkdirs();
		File moneyTracConfigF = new File(this.getDataFolder() + File.separator + "OfflineMoneyTracking.yml");

		if (!moneyTracConfigF.exists()) {
			log.info("No OfflineMoneyTracking.yml, making one now...");
			moneyTracConfig.saveDefaultConfig();
			log.info("Done!");
			return;
		}
		log.info("OfflineMoneyTracking.yml detected!");
	}
}