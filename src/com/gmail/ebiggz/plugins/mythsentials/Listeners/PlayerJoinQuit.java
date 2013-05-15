package com.gmail.ebiggz.plugins.mythsentials.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;
import com.gmail.ebiggz.plugins.mythsentials.Tools.ConfigAccessor;
import com.gmail.ebiggz.plugins.mythsentials.Tools.Utils;

public class PlayerJoinQuit implements Listener {

	ConfigAccessor moneyTracCfg = new ConfigAccessor("OfflineMoneyTracking.yml");
	private Mythsentials plugin;

	public PlayerJoinQuit(Mythsentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Utils.offlineBalanceChange(p);
		Utils.modMessage(p);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String playerName = p.getDisplayName();
		plugin.economy.getBalance(playerName);
		Double balance = plugin.economy.getBalance(playerName);
		if(moneyTracCfg.getConfig().contains(playerName)) {
			moneyTracCfg.getConfig().set(playerName, balance);
			moneyTracCfg.saveConfig();
			return;
		}
		moneyTracCfg.getConfig().addDefault(playerName, balance);
		moneyTracCfg.saveConfig();
		return;
	}
}