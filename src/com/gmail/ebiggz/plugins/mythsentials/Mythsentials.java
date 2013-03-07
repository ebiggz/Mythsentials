package com.gmail.ebiggz.plugins.mythsentials;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class Mythsentials extends JavaPlugin {
	
	private final BedrockBlocker bedrockbreaker = new BedrockBlocker(this);
	
	private static final Logger log = Logger.getLogger("Minecraft");
    
    public void onDisable() {
    	log.info("[Mythsentials] Disabled!");
    }

    public void onEnable() {
    	log.info("[Mythsentials] Enabled!");
    	getServer().getPluginManager().registerEvents(new UnregNotifier(), this);
    	getServer().getPluginManager().registerEvents(new OnlineModNotifier(), this);
    	getServer().getPluginManager().registerEvents(bedrockbreaker, this);
    	getCommand("helpme").setExecutor(new HelpMe(this));
    	getCommand("modhelp").setExecutor(new HelpMe(this));
    	getCommand("adminhelp").setExecutor(new HelpMe(this));
    	getCommand("mod").setExecutor(new HelpMe(this));
    	getCommand("admin").setExecutor(new HelpMe(this));
    	getCommand("mods").setExecutor(new HelpMe(this));
    	getCommand("admins").setExecutor(new HelpMe(this));
    	getCommand("restool").setExecutor(new ResTool());
    	getCommand("mythica").setExecutor(new HelpMenu());
    }
}