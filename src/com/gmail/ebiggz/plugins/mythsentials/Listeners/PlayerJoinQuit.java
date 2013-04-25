package com.gmail.ebiggz.plugins.mythsentials.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;
import com.gmail.ebiggz.plugins.mythsentials.Tools.Utils;

public class PlayerJoinQuit implements Listener {

	private final Mythsentials plugin;

	public PlayerJoinQuit(Mythsentials instance) {
		this.plugin = instance;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(p.hasPermission("mythica.notnoob")) {
			Utils.modMessage(p);
			Utils.offlineBalanceChange(p);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String playerName = p.getDisplayName();
		Mythsentials.economy.getBalance(playerName);
		Double balance = Mythsentials.economy.getBalance(playerName);
		if(plugin.getConfig().contains("PlayerData." + playerName + ".LogOffBal")) {
			plugin.getConfig().set("PlayerData." + playerName + ".LogOffBal", balance);
			plugin.saveConfig();
			return;
		}
		plugin.getConfig().addDefault("PlayerData." + playerName + ".LogOffBal", balance);
		plugin.saveConfig();
		return;
	}
}